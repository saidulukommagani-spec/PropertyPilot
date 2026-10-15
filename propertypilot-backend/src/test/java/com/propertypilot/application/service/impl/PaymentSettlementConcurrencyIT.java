package com.propertypilot.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.PaymentResponse;
import com.propertypilot.application.service.PaymentService;
import com.propertypilot.domain.enums.PaymentStatus;
import com.propertypilot.infrastructure.persistence.entity.InvoiceEntity;
import com.propertypilot.infrastructure.persistence.repository.InvoiceRepository;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.BusinessException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Payment settlement replay, conflict and rollback checks against real PostgreSQL. */
@Testcontainers
@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.flyway.enabled=true",
        "spring.task.scheduling.enabled=false",
        "JWT_SECRET=propertypilot-test-only-jwt-secret-value-that-is-long-enough-for-hs256",
        "propertypilot.payment.webhook-secret=propertypilot-test-only-payment-webhook-secret-value"
})
class PaymentSettlementConcurrencyIT {

    private static final String WEBHOOK_SECRET = "propertypilot-test-only-payment-webhook-secret-value";
    private static final TimeZone ORIGINAL_DEFAULT_TIME_ZONE = TimeZone.getDefault();

    static {
        // Keep PgJDBC from sending the host's legacy Asia/Calcutta timezone alias to PostgreSQL 17.
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("propertypilot_payment_race")
            .withUsername("payment_test_user")
            .withPassword("payment_test_password");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry properties) {
        properties.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        properties.add("spring.datasource.username", POSTGRES::getUsername);
        properties.add("spring.datasource.password", POSTGRES::getPassword);
        properties.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
    }

    @Autowired private JdbcTemplate jdbc;
    @Autowired private EntityManager entityManager;
    @Autowired private PaymentService paymentService;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private SecurityService securityService;
    @MockitoSpyBean private InvoiceRepository invoiceRepository;

    @Test
    void concurrentDeliveryOfSameEventIsIdempotentAndSettlesOnlyOnce() throws Exception {
        installPaymentUpdateObserver();

        Fixture singleDeliveryFixture = createFixture();
        SettlementEvent singleDeliveryEvent = event(singleDeliveryFixture,
                "event-single-" + singleDeliveryFixture.paymentId(), "SUCCEEDED", null);
        Attempt singleDelivery = settle(serialize(singleDeliveryEvent));
        assertThat(singleDelivery.failure()).isNull();
        long singleDeliveryUpdateCount = scalar(
                "SELECT count(*) FROM payment_update_observations WHERE payment_id = ?",
                singleDeliveryFixture.paymentId());
        long singleDeliveryVersion = scalar("SELECT version FROM payments WHERE payment_id = ?",
                singleDeliveryFixture.paymentId());

        Fixture fixture = createFixture();
        SettlementEvent event = event(fixture, "event-same-" + fixture.paymentId(), "SUCCEEDED", null);
        String body = serialize(event);
        coordinateBeforeInvoiceLock(fixture.invoiceId());

        List<Attempt> results = concurrently(
                () -> settle(body),
                () -> settle(body));

        assertThat(results).allSatisfy(result -> assertThat(result.failure()).isNull());
        assertThat(results).extracting(result -> result.response().getPaymentStatus())
                .containsExactly(PaymentStatus.SUCCESS, PaymentStatus.SUCCESS);
        assertThat(scalar("SELECT count(*) FROM payments WHERE payment_id = ? AND payment_status = 'SUCCESS' AND provider_event_id = ? AND settled_at IS NOT NULL",
                fixture.paymentId(), event.eventId())).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT status FROM invoices WHERE invoice_id = ?", String.class,
                fixture.invoiceId())).isEqualTo("PAID");
        assertThat(scalar("SELECT count(*) FROM payment_update_observations WHERE payment_id = ?",
                fixture.paymentId())).isEqualTo(singleDeliveryUpdateCount);
        assertThat(scalar("SELECT version FROM payments WHERE payment_id = ?", fixture.paymentId()))
                .isEqualTo(singleDeliveryVersion);
    }

    @Test
    void concurrentConflictingEventsAllowOneSettlementAndRejectTheOther() throws Exception {
        Fixture fixture = createFixture();
        SettlementEvent success = event(fixture, "event-success-" + fixture.paymentId(), "SUCCEEDED", null);
        SettlementEvent failure = event(fixture, "event-failure-" + fixture.paymentId(), "FAILED", "Provider declined");
        coordinateBeforeInvoiceLock(fixture.invoiceId());

        List<Attempt> results = concurrently(
                () -> settle(serialize(success)),
                () -> settle(serialize(failure)));

        assertThat(results.stream().filter(result -> result.failure() == null).count()).isEqualTo(1);
        Attempt loser = results.stream().filter(result -> result.failure() != null).findFirst().orElseThrow();
        assertThat(loser.failure()).isInstanceOf(BusinessException.class)
                .hasMessage("Only pending payments can be settled");

        String status = jdbc.queryForObject("SELECT payment_status FROM payments WHERE payment_id = ?", String.class,
                fixture.paymentId());
        String invoiceStatus = jdbc.queryForObject("SELECT status FROM invoices WHERE invoice_id = ?", String.class,
                fixture.invoiceId());
        String eventId = jdbc.queryForObject("SELECT provider_event_id FROM payments WHERE payment_id = ?", String.class,
                fixture.paymentId());
        assertThat(status).isIn("SUCCESS", "FAILED");
        assertThat(eventId).isIn(success.eventId(), failure.eventId());
        assertThat(scalar("SELECT count(*) FROM payments WHERE payment_id = ? AND provider_event_id IS NOT NULL", fixture.paymentId()))
                .isEqualTo(1);
        assertThat(invoiceStatus).isEqualTo("SUCCESS".equals(status) ? "PAID" : "SENT");
        if ("FAILED".equals(status)) {
            assertThat(jdbc.queryForObject("SELECT failure_reason FROM payments WHERE payment_id = ?", String.class,
                    fixture.paymentId())).isEqualTo("Provider declined");
        }
    }

    @Test
    void replayWithSameEventIdButDifferentFactsIsRejectedWithoutChangingSettlement() {
        Fixture fixture = createFixture();
        SettlementEvent original = event(fixture, "event-collision-" + fixture.paymentId(), "SUCCEEDED", null);
        PaymentResponse settled = settle(serialize(original)).response();
        SettlementEvent collision = new SettlementEvent(original.eventId(), original.provider(),
                original.providerPaymentId(), original.invoiceId(), new BigDecimal("99.00"), original.currencyCode(),
                original.status(), original.settledAt(), original.failureReason());
        long versionBeforeReplay = scalar("SELECT version FROM payments WHERE payment_id = ?", fixture.paymentId());

        Attempt replay = settle(serialize(collision));

        assertThat(settled.getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(replay.failure()).isInstanceOf(BusinessException.class)
                .hasMessage("Settlement event id was already used with different payment facts");
        assertThat(jdbc.queryForObject("SELECT payment_status FROM payments WHERE payment_id = ?", String.class,
                fixture.paymentId())).isEqualTo("SUCCESS");
        assertThat(scalar("SELECT version FROM payments WHERE payment_id = ?", fixture.paymentId()))
                .isEqualTo(versionBeforeReplay);
    }

    @Test
    void invoiceWriteFailureRollsBackFlushedPaymentSettlement() {
        Fixture fixture = createFixture();
        SettlementEvent event = event(fixture, "event-rollback-" + fixture.paymentId(), "SUCCEEDED", null);
        jdbc.execute("CREATE FUNCTION fail_payment_test_invoice_update() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN RAISE EXCEPTION 'injected invoice update failure'; END; $$");
        jdbc.execute("CREATE TRIGGER trg_test_fail_invoice_update BEFORE UPDATE ON invoices FOR EACH ROW WHEN (NEW.invoice_id = '"
                + fixture.invoiceId() + "'::uuid) EXECUTE FUNCTION fail_payment_test_invoice_update()");

        Attempt attempt = settle(serialize(event));

        assertThat(attempt.failure()).isNotNull();
        assertThat(deepestCause(attempt.failure())).isInstanceOf(SQLException.class)
                .hasMessageContaining("injected invoice update failure");
        assertThat(jdbc.queryForObject("SELECT payment_status FROM payments WHERE payment_id = ?", String.class,
                fixture.paymentId())).isEqualTo("PENDING");
        assertThat(jdbc.queryForObject("SELECT provider_event_id FROM payments WHERE payment_id = ?", String.class,
                fixture.paymentId())).isNull();
        assertThat(jdbc.queryForObject("SELECT status FROM invoices WHERE invoice_id = ?", String.class,
                fixture.invoiceId())).isEqualTo("SENT");
        assertThat(scalar("SELECT version FROM payments WHERE payment_id = ?", fixture.paymentId())).isZero();
    }

    private void coordinateBeforeInvoiceLock(UUID invoiceId) {
        CyclicBarrier bothTransactionsReachedLock = new CyclicBarrier(2);
        doAnswer(invocation -> {
            UUID requestedId = invocation.getArgument(0);
            if (!invoiceId.equals(requestedId)) {
                return invocation.callRealMethod();
            }
            bothTransactionsReachedLock.await(30, TimeUnit.SECONDS);
            return Optional.ofNullable(entityManager.find(InvoiceEntity.class, requestedId,
                    LockModeType.PESSIMISTIC_WRITE));
        }).when(invoiceRepository).findByIdForUpdate(any(UUID.class));
    }

    private void installPaymentUpdateObserver() {
        jdbc.execute("CREATE TABLE payment_update_observations (observation_id BIGSERIAL PRIMARY KEY, payment_id UUID NOT NULL, transaction_id BIGINT NOT NULL, old_version BIGINT NOT NULL, new_version BIGINT NOT NULL, event_id VARCHAR(160))");
        jdbc.execute("CREATE FUNCTION count_payment_update_for_test() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN INSERT INTO payment_update_observations (payment_id, transaction_id, old_version, new_version, event_id) VALUES (NEW.payment_id, txid_current(), OLD.version, NEW.version, NEW.provider_event_id); RETURN NEW; END; $$");
        jdbc.execute("CREATE TRIGGER trg_test_count_payment_update AFTER UPDATE ON payments FOR EACH ROW EXECUTE FUNCTION count_payment_update_for_test()");
    }

    private List<Attempt> concurrently(ThrowingCall first, ThrowingCall second) throws Exception {
        ExecutorService workers = Executors.newFixedThreadPool(2);
        try {
            Future<Attempt> firstResult = workers.submit(() -> capture(first));
            Future<Attempt> secondResult = workers.submit(() -> capture(second));
            return List.of(firstResult.get(60, TimeUnit.SECONDS), secondResult.get(60, TimeUnit.SECONDS));
        } finally {
            workers.shutdownNow();
            workers.awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    private Attempt capture(ThrowingCall call) {
        try {
            return call.call();
        } catch (Throwable failure) {
            return new Attempt(null, failure);
        }
    }

    private Attempt settle(String body) {
        try {
            return new Attempt(paymentService.confirmSettlement(body, sign(body)), null);
        } catch (Throwable failure) {
            return new Attempt(null, failure);
        }
    }

    private Fixture createFixture() {
        UUID userId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID invoiceId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        UUID providerPaymentId = UUID.randomUUID();
        jdbc.update("INSERT INTO users (user_id, full_name, email, password_hash, status) VALUES (?, ?, ?, ?, 'ACTIVE')",
                userId, "Payment Settlement Test", "payment-race-" + userId + "@example.test", "test-hash");
        jdbc.update("INSERT INTO customers (customer_id, user_id, customer_type, status) VALUES (?, ?, 'INDIVIDUAL', 'ACTIVE')",
                customerId, userId);
        jdbc.update("INSERT INTO invoices (invoice_id, customer_id, invoice_number, currency_code, invoice_amount, tax_amount, status, issued_at, due_at, entity_type, entity_id, invoice_type, description) VALUES (?, ?, ?, 'INR', 100.00, 0.00, 'SENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '1 day', 'SERVICE_REQUEST', ?, 'SERVICE_CHARGE', 'Settlement integration fixture')",
                invoiceId, customerId, "PAY-RACE-" + invoiceId, UUID.randomUUID());
        jdbc.update("INSERT INTO payments (payment_id, customer_id, invoice_id, provider, provider_payment_id, idempotency_key, currency_code, amount, payment_status, payment_date, entity_type, entity_id) VALUES (?, ?, ?, 'TEST_PROVIDER', ?, ?, 'INR', 100.00, 'PENDING', CURRENT_TIMESTAMP, 'SERVICE_REQUEST', ?)",
                paymentId, customerId, invoiceId, providerPaymentId.toString(), "payment-race-idem-" + paymentId,
                UUID.randomUUID());
        return new Fixture(customerId, invoiceId, paymentId, "TEST_PROVIDER", providerPaymentId.toString());
    }

    private SettlementEvent event(Fixture fixture, String eventId, String status, String failureReason) {
        return new SettlementEvent(eventId, fixture.provider(), fixture.providerPaymentId(), fixture.invoiceId(),
                new BigDecimal("100.00"), "INR", status, OffsetDateTime.now().withNano(0), failureReason);
    }

    private String serialize(SettlementEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (Exception exception) {
            throw new IllegalStateException("Cannot serialize test settlement event", exception);
        }
    }

    private String sign(String body) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(WEBHOOK_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(body.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Cannot sign test settlement event", exception);
        }
    }

    private long scalar(String sql, Object... parameters) {
        Long value = jdbc.queryForObject(sql, Long.class, parameters);
        return value == null ? 0L : value;
    }

    private Throwable deepestCause(Throwable failure) {
        Throwable current = failure;
        while (current.getCause() != null && current.getCause() != current) current = current.getCause();
        return current;
    }

    private record Fixture(UUID customerId, UUID invoiceId, UUID paymentId, String provider, String providerPaymentId) { }
    private record SettlementEvent(String eventId, String provider, String providerPaymentId, UUID invoiceId,
                                   BigDecimal amount, String currencyCode, String status,
                                   OffsetDateTime settledAt, String failureReason) { }
    private record Attempt(PaymentResponse response, Throwable failure) { }
    @FunctionalInterface private interface ThrowingCall { Attempt call() throws Exception; }

    @AfterAll
    static void restoreDefaultTimeZone() {
        TimeZone.setDefault(ORIGINAL_DEFAULT_TIME_ZONE);
    }
}
