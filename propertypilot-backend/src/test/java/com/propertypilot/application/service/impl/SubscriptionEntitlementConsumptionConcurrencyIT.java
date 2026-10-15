package com.propertypilot.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.propertypilot.application.dto.ConsumptionResponse;
import com.propertypilot.application.dto.CreateConsumptionRequest;
import com.propertypilot.application.service.SubscriptionEntitlementConsumptionService;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.BusinessException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.TimeZone;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.LockSupport;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** PostgreSQL-backed quota and idempotency checks for entitlement consumption. */
@Testcontainers
@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.flyway.enabled=true",
        "spring.task.scheduling.enabled=false",
        "JWT_SECRET=propertypilot-test-only-jwt-secret-value-that-is-long-enough-for-hs256"
})
class SubscriptionEntitlementConsumptionConcurrencyIT {

    private static final TimeZone ORIGINAL_DEFAULT_TIME_ZONE = TimeZone.getDefault();

    static {
        // PostgreSQL 17 in the Testcontainer rejects the JVM's Asia/Calcutta alias.
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("propertypilot_entitlement_race")
            .withUsername("entitlement_test_user")
            .withPassword("entitlement_test_password");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry properties) {
        properties.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        properties.add("spring.datasource.username", POSTGRES::getUsername);
        properties.add("spring.datasource.password", POSTGRES::getPassword);
        properties.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
    }

    @Autowired private JdbcTemplate jdbc;
    @Autowired private DataSource dataSource;
    @Autowired private SubscriptionEntitlementConsumptionService consumptionService;
    @MockitoBean private SecurityService securityService;

    @Test
    void simultaneousConsumptionCannotExceedThePersistedEntitlementQuantity() throws Exception {
        Fixture fixture = createFixture(false);

        List<Attempt> attempts = concurrently(
                fixture.subscriptionId(),
                () -> consume(fixture, null),
                () -> consume(fixture, null));

        assertThat(attempts.stream().filter(Attempt::succeeded).count()).isEqualTo(1);
        Attempt loser = attempts.stream().filter(attempt -> !attempt.succeeded()).findFirst().orElseThrow();
        assertThat(loser.failure()).isInstanceOf(BusinessException.class)
                .hasMessage("Entitlement limit exceeded");
        assertThat(count("SELECT count(*) FROM subscription_entitlement_consumptions "
                        + "WHERE customer_subscription_id = ? AND service_id = ? AND period_end IS NULL",
                fixture.subscriptionId(), fixture.serviceId())).isEqualTo(1);
        assertThat(count("SELECT COALESCE(sum(consumed_quantity), 0) "
                        + "FROM subscription_entitlement_consumptions "
                        + "WHERE customer_subscription_id = ? AND service_id = ? AND period_end IS NULL",
                fixture.subscriptionId(), fixture.serviceId())).isEqualTo(1);
    }

    @Test
    void simultaneousReplayForOneServiceRequestReturnsTheSameConsumption() throws Exception {
        Fixture fixture = createFixture(true);

        List<Attempt> attempts = concurrently(
                fixture.subscriptionId(),
                () -> consume(fixture, fixture.serviceRequestId()),
                () -> consume(fixture, fixture.serviceRequestId()));

        assertThat(attempts).allSatisfy(attempt -> assertThat(attempt.failure()).isNull());
        assertThat(attempts).extracting(attempt -> attempt.response().getEntitlementConsumptionId())
                .containsOnly(attempts.getFirst().response().getEntitlementConsumptionId());
        assertThat(count("SELECT count(*) FROM subscription_entitlement_consumptions "
                        + "WHERE service_request_id = ? AND period_end IS NULL",
                fixture.serviceRequestId())).isEqualTo(1);
        assertThat(count("SELECT COALESCE(sum(consumed_quantity), 0) "
                        + "FROM subscription_entitlement_consumptions "
                        + "WHERE customer_subscription_id = ? AND service_id = ? AND period_end IS NULL",
                fixture.subscriptionId(), fixture.serviceId())).isEqualTo(1);
    }

    private List<Attempt> concurrently(UUID subscriptionId, ThrowingCall first, ThrowingCall second)
            throws Exception {
        ExecutorService workers = Executors.newFixedThreadPool(2);
        try (Connection lockHolder = dataSource.getConnection()) {
            lockHolder.setAutoCommit(false);
            try (PreparedStatement lockSubscription = lockHolder.prepareStatement(
                    "SELECT customer_subscription_id FROM customer_subscriptions "
                            + "WHERE customer_subscription_id = ? FOR UPDATE")) {
                lockSubscription.setObject(1, subscriptionId);
                try (var result = lockSubscription.executeQuery()) {
                    if (!result.next()) {
                        throw new IllegalStateException("Concurrency fixture subscription was not found");
                    }
                }
            }

            Future<Attempt> firstResult = workers.submit(() -> capture(first));
            Future<Attempt> secondResult = workers.submit(() -> capture(second));

            awaitBothProductionQueriesBlocked();
            lockHolder.commit();
            return List.of(firstResult.get(60, TimeUnit.SECONDS), secondResult.get(60, TimeUnit.SECONDS));
        } finally {
            workers.shutdownNow();
            if (!workers.awaitTermination(10, TimeUnit.SECONDS)) {
                throw new AssertionError("Entitlement concurrency worker threads did not terminate");
            }
        }
    }

    private void awaitBothProductionQueriesBlocked() throws Exception {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(30);
        long observed = 0;
        while (System.nanoTime() < deadline) {
            Long count = jdbc.queryForObject("""
                    SELECT count(*)
                    FROM pg_stat_activity
                    WHERE datname = current_database()
                      AND state = 'active'
                      AND wait_event_type = 'Lock'
                      AND query ILIKE '%customer_subscriptions%'
                    """, Long.class);
            observed = count == null ? 0 : count;
            if (observed == 2) {
                return;
            }
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException("Interrupted while awaiting production subscription lock queries");
            }
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(10));
        }
        throw new TimeoutException("Expected two production customer_subscriptions lock queries to wait; observed "
                + observed);
    }

    private Attempt capture(ThrowingCall call) {
        try {
            return call.call();
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            return new Attempt(null, interrupted);
        } catch (Exception failure) {
            return new Attempt(null, failure);
        }
    }

    private Attempt consume(Fixture fixture, UUID serviceRequestId) {
        try {
            CreateConsumptionRequest request = new CreateConsumptionRequest();
            request.setCustomerSubscriptionId(fixture.subscriptionId());
            request.setServiceId(fixture.serviceId());
            request.setServiceRequestId(serviceRequestId);
            request.setQuantity(1);
            return new Attempt(consumptionService.consume(request), null);
        } catch (Exception failure) {
            return new Attempt(null, failure);
        }
    }

    private Fixture createFixture(boolean includeServiceRequest) {
        UUID userId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID planId = UUID.randomUUID();
        UUID planVersionId = UUID.randomUUID();
        UUID entitlementId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        UUID subscriptionId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();
        UUID serviceRequestId = includeServiceRequest ? UUID.randomUUID() : null;
        LocalDate today = LocalDate.now();

        jdbc.update("INSERT INTO users (user_id, full_name, email, password_hash, status) "
                        + "VALUES (?, ?, ?, ?, 'ACTIVE')",
                userId, "Entitlement Concurrency Test", "entitlement-race-" + userId + "@example.test", "test-hash");
        jdbc.update("INSERT INTO customers (customer_id, user_id, customer_type, status) "
                        + "VALUES (?, ?, 'INDIVIDUAL', 'ACTIVE')",
                customerId, userId);
        jdbc.update("INSERT INTO subscription_plans (subscription_plan_id, plan_code, plan_name, status) "
                        + "VALUES (?, ?, ?, 'ACTIVE')",
                planId, "ENT_RACE_" + userId.toString().replace("-", "").substring(0, 12).toUpperCase(),
                "Entitlement Concurrency Test");
        jdbc.update("INSERT INTO subscription_plan_versions "
                        + "(plan_version_id, subscription_plan_id, version_number, price, currency_code, "
                        + "effective_from, status, benefits_json) "
                        + "VALUES (?, ?, 1, 0.00, 'INR', CURRENT_TIMESTAMP, 'ACTIVE', '{}'::jsonb)",
                planVersionId, planId);
        jdbc.update("INSERT INTO services (service_id, service_code, service_name, category, base_price, active_flag) "
                        + "VALUES (?, ?, 'Entitlement Race Service', 'GENERAL_ASSISTANCE', 0.00, TRUE)",
                serviceId, "ENT_RACE_" + userId.toString().replace("-", "").substring(0, 12).toUpperCase());
        jdbc.update("INSERT INTO subscription_plan_entitlements "
                        + "(entitlement_id, plan_version_id, service_id, quantity, period_type, "
                        + "carry_forward_allowed, status) VALUES (?, ?, ?, 1, 'TERM', FALSE, 'ACTIVE')",
                entitlementId, planVersionId, serviceId);
        jdbc.update("INSERT INTO customer_subscriptions "
                        + "(customer_subscription_id, customer_id, plan_version_id, status, start_date, end_date, auto_renew) "
                        + "VALUES (?, ?, ?, 'ACTIVE', ?, ?, FALSE)",
                subscriptionId, customerId, planVersionId, Date.valueOf(today), Date.valueOf(today.plusDays(30)));

        if (includeServiceRequest) {
            jdbc.update("INSERT INTO properties (property_id, customer_id, title, property_type) "
                            + "VALUES (?, ?, 'Entitlement Race Property', 'HOUSE')",
                    propertyId, customerId);
            jdbc.update("INSERT INTO service_requests "
                            + "(service_request_id, customer_id, property_id, service_id, request_type, "
                            + "priority, status, requested_at) "
                            + "VALUES (?, ?, ?, ?, 'GENERAL_ASSISTANCE', 'MEDIUM', 'NEW', CURRENT_TIMESTAMP)",
                    serviceRequestId, customerId, propertyId, serviceId);
        }
        return new Fixture(subscriptionId, serviceId, serviceRequestId);
    }

    private long count(String sql, Object... parameters) {
        Long result = jdbc.queryForObject(sql, Long.class, parameters);
        return result == null ? 0 : result;
    }

    private record Fixture(UUID subscriptionId, UUID serviceId, UUID serviceRequestId) { }
    private record Attempt(ConsumptionResponse response, Exception failure) {
        boolean succeeded() { return failure == null; }
    }
    @FunctionalInterface private interface ThrowingCall { Attempt call() throws Exception; }

    @AfterAll
    static void restoreDefaultTimeZone() {
        TimeZone.setDefault(ORIGINAL_DEFAULT_TIME_ZONE);
    }
}
