package com.propertypilot.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.propertypilot.application.dto.ConsumptionResponse;
import com.propertypilot.application.dto.CreateConsumptionRequest;
import com.propertypilot.application.scheduler.SubscriptionExpiryScheduler;
import com.propertypilot.application.service.SubscriptionEntitlementConsumptionService;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.BusinessException;
import java.sql.Date;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.TimeZone;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.mockito.Mockito.when;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Database-backed expiry boundary and scheduler persistence checks. */
@Testcontainers
@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.flyway.enabled=true",
        "spring.task.scheduling.enabled=false",
        "propertypilot.subscription-expiry.cron=-",
        "JWT_SECRET=propertypilot-test-only-jwt-secret-value-that-is-long-enough-for-hs256"
})
class SubscriptionExpiryPersistenceIT {

    private static final LocalDate BUSINESS_TODAY = LocalDate.of(2030, 6, 15);
    private static final TimeZone ORIGINAL_DEFAULT_TIME_ZONE = TimeZone.getDefault();

    static {
        // Match the PostgreSQL 17 Testcontainers convention used by the other persistence ITs.
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("propertypilot_subscription_expiry")
            .withUsername("subscription_expiry_test_user")
            .withPassword("subscription_expiry_test_password");

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
    private SubscriptionEntitlementConsumptionService consumptionService;

    @Autowired
    private SubscriptionExpiryScheduler expiryScheduler;

    @MockitoBean
    private SecurityService securityService;

    @MockitoBean
    private Clock businessClock;

    @BeforeEach
    void useFixedBusinessDate() {
        when(businessClock.getZone()).thenReturn(ZoneOffset.UTC);
        when(businessClock.instant()).thenReturn(BUSINESS_TODAY.atStartOfDay(ZoneOffset.UTC).toInstant());
    }

    @Test
    void consumptionRejectsYesterdayAndAllowsTodayAndTomorrowEndDates() {
        LocalDate today = LocalDate.now(businessClock);
        Fixture fixture = createFixture();
        UUID yesterdaySubscription = createSubscription(fixture, today.minusDays(1));
        UUID todaySubscription = createSubscription(fixture, today);
        UUID tomorrowSubscription = createSubscription(fixture, today.plusDays(1));

        assertThatThrownBy(() -> consume(yesterdaySubscription, fixture.serviceId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Subscription is not active for the current date");

        ConsumptionResponse endingToday = consume(todaySubscription, fixture.serviceId());
        ConsumptionResponse endingTomorrow = consume(tomorrowSubscription, fixture.serviceId());

        assertThat(endingToday.getConsumedQuantity()).isEqualTo(1);
        assertThat(endingTomorrow.getConsumedQuantity()).isEqualTo(1);
        assertThat(count("SELECT count(*) FROM subscription_entitlement_consumptions "
                        + "WHERE customer_subscription_id = ? AND period_end IS NULL", yesterdaySubscription))
                .isZero();
        assertThat(count("SELECT count(*) FROM subscription_entitlement_consumptions "
                        + "WHERE customer_subscription_id = ? AND period_end IS NULL", todaySubscription))
                .isEqualTo(1);
        assertThat(count("SELECT count(*) FROM subscription_entitlement_consumptions "
                        + "WHERE customer_subscription_id = ? AND period_end IS NULL", tomorrowSubscription))
                .isEqualTo(1);
    }

    @Test
    void schedulerExpiresOnlyActiveSubscriptionsEndingBeforeTodayAndPersistsStatuses() {
        LocalDate today = LocalDate.now(businessClock);
        Fixture fixture = createFixture();
        UUID endedYesterday = createSubscription(fixture, today.minusDays(1));
        UUID endingToday = createSubscription(fixture, today);
        UUID endingTomorrow = createSubscription(fixture, today.plusDays(1));
        UUID inactiveEndedYesterday = createSubscription(fixture, today.minusDays(1), "PENDING");

        expiryScheduler.expireSubscriptions();

        assertThat(statusOf(endedYesterday)).isEqualTo("EXPIRED");
        assertThat(statusOf(endingToday)).isEqualTo("ACTIVE");
        assertThat(statusOf(endingTomorrow)).isEqualTo("ACTIVE");
        assertThat(statusOf(inactiveEndedYesterday)).isEqualTo("PENDING");
    }

    private ConsumptionResponse consume(UUID subscriptionId, UUID serviceId) {
        CreateConsumptionRequest request = new CreateConsumptionRequest();
        request.setCustomerSubscriptionId(subscriptionId);
        request.setServiceId(serviceId);
        request.setQuantity(1);
        return consumptionService.consume(request);
    }

    private Fixture createFixture() {
        UUID userId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID planId = UUID.randomUUID();
        UUID planVersionId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        UUID entitlementId = UUID.randomUUID();

        jdbc.update("INSERT INTO users (user_id, full_name, email, password_hash, status) "
                        + "VALUES (?, ?, ?, ?, 'ACTIVE')",
                userId, "Subscription Expiry Test", "subscription-expiry-" + userId + "@example.test", "test-hash");
        jdbc.update("INSERT INTO customers (customer_id, user_id, customer_type, status) "
                        + "VALUES (?, ?, 'INDIVIDUAL', 'ACTIVE')",
                customerId, userId);
        jdbc.update("INSERT INTO subscription_plans (subscription_plan_id, plan_code, plan_name, status) "
                        + "VALUES (?, ?, ?, 'ACTIVE')",
                planId, "EXPIRY_" + compact(userId), "Subscription Expiry Test");
        jdbc.update("INSERT INTO subscription_plan_versions "
                        + "(plan_version_id, subscription_plan_id, version_number, price, currency_code, "
                        + "effective_from, status, benefits_json) "
                        + "VALUES (?, ?, 1, 0.00, 'INR', CURRENT_TIMESTAMP, 'ACTIVE', '{}'::jsonb)",
                planVersionId, planId);
        jdbc.update("INSERT INTO services (service_id, service_code, service_name, category, base_price, active_flag) "
                        + "VALUES (?, ?, 'Expiry Boundary Service', 'GENERAL_ASSISTANCE', 0.00, TRUE)",
                serviceId, "EXPIRY_" + compact(userId));
        jdbc.update("INSERT INTO subscription_plan_entitlements "
                        + "(entitlement_id, plan_version_id, service_id, quantity, period_type, "
                        + "carry_forward_allowed, status) VALUES (?, ?, ?, 3, 'TERM', FALSE, 'ACTIVE')",
                entitlementId, planVersionId, serviceId);
        return new Fixture(customerId, planVersionId, serviceId);
    }

    private UUID createSubscription(Fixture fixture, LocalDate endDate) {
        return createSubscription(fixture, endDate, "ACTIVE");
    }

    private UUID createSubscription(Fixture fixture, LocalDate endDate, String status) {
        UUID subscriptionId = UUID.randomUUID();
        LocalDate startDate = endDate.minusDays(30);
        jdbc.update("INSERT INTO customer_subscriptions "
                        + "(customer_subscription_id, customer_id, plan_version_id, status, start_date, end_date, auto_renew) "
                        + "VALUES (?, ?, ?, ?, ?, ?, FALSE)",
                subscriptionId, fixture.customerId(), fixture.planVersionId(), status,
                Date.valueOf(startDate), Date.valueOf(endDate));
        return subscriptionId;
    }

    private String statusOf(UUID subscriptionId) {
        return jdbc.queryForObject("SELECT status FROM customer_subscriptions "
                + "WHERE customer_subscription_id = ?", String.class, subscriptionId);
    }

    private long count(String sql, Object... parameters) {
        Long result = jdbc.queryForObject(sql, Long.class, parameters);
        return result == null ? 0 : result;
    }

    private String compact(UUID value) {
        return value.toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private record Fixture(UUID customerId, UUID planVersionId, UUID serviceId) { }

    @AfterAll
    static void restoreDefaultTimeZone() {
        TimeZone.setDefault(ORIGINAL_DEFAULT_TIME_ZONE);
    }
}
