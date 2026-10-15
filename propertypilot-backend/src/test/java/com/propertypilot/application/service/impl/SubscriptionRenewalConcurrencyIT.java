package com.propertypilot.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

import com.propertypilot.application.dto.CreateSubscriptionRenewalRequest;
import com.propertypilot.application.service.SubscriptionRenewalService;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;
import com.propertypilot.security.SecurityService;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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

/**
 * Exercises renewal concurrency against real PostgreSQL transactions and the
 * complete Flyway schema. The container receives a random host port.
 */
@Testcontainers
@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.flyway.enabled=true",
        "spring.task.scheduling.enabled=false",
        "JWT_SECRET=propertypilot-test-only-jwt-secret-value-that-is-long-enough-for-hs256"
})
class SubscriptionRenewalConcurrencyIT {

    private static final TimeZone ORIGINAL_DEFAULT_TIME_ZONE = TimeZone.getDefault();

    static {
        // PgJDBC sends the JVM default timezone during startup. PostgreSQL 17
        // rejects this host's legacy Asia/Calcutta alias, so isolate this test JVM to UTC.
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:17")
                    .withDatabaseName("propertypilot_renewal_race")
                    .withUsername("renewal_test_user")
                    .withPassword("renewal_test_password");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry properties) {
        properties.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        properties.add("spring.datasource.username", POSTGRES::getUsername);
        properties.add("spring.datasource.password", POSTGRES::getPassword);
        properties.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
    }

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SubscriptionRenewalService renewalService;

    @MockitoBean
    private SecurityService securityService;

    @MockitoSpyBean
    private CustomerSubscriptionRepository subscriptionRepository;

    @Test
    void concurrentRenewalsForOneSourceHaveOneCommitAndRollbackTheLoser() throws Exception {
        Fixture fixture = createFixture();
        CyclicBarrier bothSourceReadsCompleted = new CyclicBarrier(2);

        doAnswer(invocation -> {
            UUID requestedId = invocation.getArgument(0);
            Optional<Object> result = Optional.ofNullable(
                    entityManager.find(com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity.class,
                            requestedId));
            if (fixture.sourceSubscriptionId().equals(requestedId)) {
                bothSourceReadsCompleted.await(30, TimeUnit.SECONDS);
            }
            return result;
        }).when(subscriptionRepository).findById(any(UUID.class));

        ExecutorService workers = Executors.newFixedThreadPool(2);
        try {
            Future<Attempt> first = workers.submit(() -> attempt(fixture, 0));
            Future<Attempt> second = workers.submit(() -> attempt(fixture, 1));
            List<Attempt> attempts = List.of(first.get(45, TimeUnit.SECONDS), second.get(45, TimeUnit.SECONDS));

            assertThat(attempts.stream().filter(Attempt::succeeded).count())
                    .as("concurrent renewal outcomes: %s", attempts.stream()
                            .map(attempt -> attempt.succeeded() ? "SUCCESS" : describeCause(attempt.failure()))
                            .toList())
                    .isEqualTo(1);
            Attempt loser = attempts.stream().filter(attempt -> !attempt.succeeded()).findFirst().orElseThrow();
            assertThat(loser.failure()).isNotNull();
            System.out.println("Renewal race losing exception: " + describeCause(loser.failure()));

            assertThat(count("SELECT count(*) FROM subscription_renewals WHERE customer_subscription_id = ? AND period_start = ? AND period_end = ?",
                    fixture.sourceSubscriptionId(), Date.valueOf(fixture.periodStart()), Date.valueOf(fixture.periodEnd())))
                    .isEqualTo(1);
            assertThat(count("SELECT count(*) FROM customer_subscriptions WHERE customer_id = ? AND start_date = ? AND end_date = ? AND customer_subscription_id <> ?",
                    fixture.customerId(), Date.valueOf(fixture.periodStart()), Date.valueOf(fixture.periodEnd()),
                    fixture.sourceSubscriptionId()))
                    .isEqualTo(1);
            assertThat(jdbc.queryForObject("SELECT status FROM customer_subscriptions WHERE customer_subscription_id = ?",
                    String.class, fixture.sourceSubscriptionId())).isEqualTo("EXPIRED");
            assertThat(count("SELECT count(*) FROM subscription_renewals r JOIN customer_subscriptions s ON s.customer_subscription_id = r.new_customer_subscription_id WHERE r.customer_subscription_id = ? AND r.status = 'SUCCESS' AND s.status = 'ACTIVE'",
                    fixture.sourceSubscriptionId())).isEqualTo(1);

            assertThat(count("SELECT count(*) FROM invoices WHERE invoice_id IN (?, ?) AND status = 'PAID' AND paid_at IS NOT NULL",
                    fixture.invoiceIds().get(0), fixture.invoiceIds().get(1))).isEqualTo(2);
            assertThat(count("SELECT count(*) FROM payments WHERE payment_id IN (?, ?) AND payment_status = 'SUCCESS' AND settled_at IS NOT NULL",
                    fixture.paymentIds().get(0), fixture.paymentIds().get(1))).isEqualTo(2);
            assertThat(count("SELECT count(*) FROM subscription_renewals WHERE payment_id IN (?, ?)",
                    fixture.paymentIds().get(0), fixture.paymentIds().get(1))).isEqualTo(1);
        } finally {
            workers.shutdownNow();
            workers.awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    private Attempt attempt(Fixture fixture, int index) {
        try {
            CreateSubscriptionRenewalRequest request = new CreateSubscriptionRenewalRequest();
            request.setCustomerSubscriptionId(fixture.sourceSubscriptionId());
            request.setPaymentId(fixture.paymentIds().get(index));
            request.setPeriodStart(fixture.periodStart());
            request.setPeriodEnd(fixture.periodEnd());
            request.setRenewalType("MANUAL");
            request.setRenewalAmount(new BigDecimal("100.00"));
            renewalService.renewSubscription(request);
            return new Attempt(true, null);
        } catch (Throwable failure) {
            return new Attempt(false, failure);
        }
    }

    private Fixture createFixture() {
        UUID userId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID planId = UUID.randomUUID();
        UUID planVersionId = UUID.randomUUID();
        UUID sourceSubscriptionId = UUID.randomUUID();
        LocalDate periodStart = LocalDate.now().plusDays(31);
        LocalDate periodEnd = periodStart.plusDays(30);

        jdbc.update("INSERT INTO users (user_id, full_name, email, password_hash, status) VALUES (?, ?, ?, ?, 'ACTIVE')",
                userId, "Renewal Race Test", "renewal-race-" + userId + "@example.test", "test-hash");
        jdbc.update("INSERT INTO customers (customer_id, user_id, customer_type, status) VALUES (?, ?, 'INDIVIDUAL', 'ACTIVE')",
                customerId, userId);
        jdbc.update("INSERT INTO subscription_plans (subscription_plan_id, plan_code, plan_name, status) VALUES (?, ?, ?, 'ACTIVE')",
                planId, "RACE_" + userId.toString().replace("-", "").substring(0, 12).toUpperCase(), "Renewal Race Test");
        jdbc.update("INSERT INTO subscription_plan_versions (plan_version_id, subscription_plan_id, version_number, price, currency_code, effective_from, status, benefits_json) VALUES (?, ?, 1, 100.00, 'INR', CURRENT_TIMESTAMP, 'ACTIVE', '{}'::jsonb)",
                planVersionId, planId);
        LocalDate sourceStart = LocalDate.now();
        LocalDate sourceEnd = periodStart.minusDays(1);
        jdbc.update("INSERT INTO customer_subscriptions (customer_subscription_id, customer_id, plan_version_id, status, start_date, end_date, auto_renew) VALUES (?, ?, ?, 'ACTIVE', ?, ?, FALSE)",
                sourceSubscriptionId, customerId, planVersionId, Date.valueOf(sourceStart), Date.valueOf(sourceEnd));

        List<UUID> invoiceIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        List<UUID> paymentIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        for (int i = 0; i < 2; i++) {
            jdbc.update("INSERT INTO invoices (invoice_id, customer_id, invoice_number, currency_code, invoice_amount, tax_amount, status, issued_at, paid_at, entity_type, entity_id, invoice_type, description, renewal_period_start, renewal_period_end) VALUES (?, ?, ?, 'INR', 100.00, 0.00, 'PAID', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SUBSCRIPTION', ?, 'SUBSCRIPTION_RENEWAL', 'Concurrency fixture', ?, ?)",
                    invoiceIds.get(i), customerId, "RACE-" + invoiceIds.get(i), sourceSubscriptionId,
                    Date.valueOf(periodStart), Date.valueOf(periodEnd));
            jdbc.update("INSERT INTO payments (payment_id, customer_id, invoice_id, provider, provider_payment_id, idempotency_key, currency_code, amount, payment_status, payment_date, settled_at) VALUES (?, ?, ?, 'TEST_PROVIDER', ?, ?, 'INR', 100.00, 'SUCCESS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                    paymentIds.get(i), customerId, invoiceIds.get(i), "race-payment-" + paymentIds.get(i),
                    "race-idempotency-" + paymentIds.get(i));
        }
        return new Fixture(customerId, sourceSubscriptionId, periodStart, periodEnd, invoiceIds, paymentIds);
    }

    private long count(String sql, Object... parameters) {
        Long result = jdbc.queryForObject(sql, Long.class, parameters);
        return result == null ? 0 : result;
    }

    private String describeCause(Throwable failure) {
        Throwable deepest = failure;
        while (deepest.getCause() != null && deepest.getCause() != deepest) {
            deepest = deepest.getCause();
        }
        return failure.getClass().getName() + ": " + failure.getMessage()
                + " | root=" + deepest.getClass().getName() + ": " + deepest.getMessage();
    }

    private record Fixture(UUID customerId, UUID sourceSubscriptionId, LocalDate periodStart,
                           LocalDate periodEnd, List<UUID> invoiceIds, List<UUID> paymentIds) { }

    private record Attempt(boolean succeeded, Throwable failure) { }

    @AfterAll
    static void restoreDefaultTimeZone() {
        TimeZone.setDefault(ORIGINAL_DEFAULT_TIME_ZONE);
    }
}
