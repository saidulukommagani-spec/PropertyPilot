package com.propertypilot.application.service.impl;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.security.JwtService;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.TimeZone;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Verifies customer ownership through the real JWT, MVC, service, and PostgreSQL paths. */
@Testcontainers
@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.flyway.enabled=true",
        "propertypilot.subscription-expiry.cron=-",
        "JWT_SECRET=propertypilot-test-only-jwt-secret-value-that-is-long-enough-for-hs256"
})
@AutoConfigureMockMvc
class SubscriptionOwnershipAuthorizationIT {

    private static final TimeZone ORIGINAL_DEFAULT_TIME_ZONE = TimeZone.getDefault();

    static {
        // Match the PostgreSQL 17 integration-test convention for this Windows host.
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("propertypilot_subscription_ownership")
            .withUsername("subscription_ownership_test_user")
            .withPassword("subscription_ownership_test_password");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry properties) {
        properties.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        properties.add("spring.datasource.username", POSTGRES::getUsername);
        properties.add("spring.datasource.password", POSTGRES::getPassword);
        properties.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
    }

    @Autowired private MockMvc mockMvc;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private JwtService jwtService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void customerCannotConsumeAnotherCustomersEntitlement() throws Exception {
        Fixture fixture = createFixture(false);
        long initialQuantity = entitlementQuantity(fixture.entitlementId());

        mockMvc.perform(post("/api/subscription-consumptions")
                        .header("Authorization", bearer(fixture.customerAEmail()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of(
                                "customerSubscriptionId", fixture.subscriptionId(),
                                "serviceId", fixture.serviceId(),
                                "quantity", 1))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        org.assertj.core.api.Assertions.assertThat(consumptionCount(fixture.subscriptionId())).isZero();
        org.assertj.core.api.Assertions.assertThat(subscriptionStatus(fixture.subscriptionId())).isEqualTo("ACTIVE");
        org.assertj.core.api.Assertions.assertThat(entitlementQuantity(fixture.entitlementId()))
                .isEqualTo(initialQuantity);
        org.assertj.core.api.Assertions.assertThat(entitlementStatus(fixture.entitlementId())).isEqualTo("ACTIVE");
    }

    @Test
    void customerCannotCreateOrReadAnotherCustomersRenewal() throws Exception {
        Fixture fixture = createFixture(true);
        LocalDate renewalStart = fixture.endDate().plusDays(1);
        LocalDate renewalEnd = renewalStart.plusDays(364);
        UUID unassociatedPaymentId = UUID.randomUUID();
        long initialSubscriptionCount = subscriptionCount(fixture.customerBId());
        long initialInvoiceCount = renewalInvoiceCount(fixture.subscriptionId());
        long initialRenewalCount = renewalCount(fixture.subscriptionId());

        mockMvc.perform(post("/api/subscription-renewals/invoice")
                        .header("Authorization", bearer(fixture.customerAEmail()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of(
                                "customerSubscriptionId", fixture.subscriptionId(),
                                "periodStart", renewalStart,
                                "periodEnd", renewalEnd))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        mockMvc.perform(post("/api/subscription-renewals")
                        .header("Authorization", bearer(fixture.customerAEmail()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of(
                                "customerSubscriptionId", fixture.subscriptionId(),
                                "paymentId", unassociatedPaymentId,
                                "periodStart", renewalStart,
                                "periodEnd", renewalEnd,
                                "renewalAmount", new BigDecimal("1000.00")))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        mockMvc.perform(get("/api/subscription-renewals/subscription/{subscriptionId}", fixture.subscriptionId())
                        .header("Authorization", bearer(fixture.customerAEmail())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        org.assertj.core.api.Assertions.assertThat(subscriptionStatus(fixture.subscriptionId())).isEqualTo("ACTIVE");
        org.assertj.core.api.Assertions.assertThat(subscriptionEndDate(fixture.subscriptionId())).isEqualTo(fixture.endDate());
        org.assertj.core.api.Assertions.assertThat(subscriptionCount(fixture.customerBId()))
                .isEqualTo(initialSubscriptionCount);
        org.assertj.core.api.Assertions.assertThat(renewalInvoiceCount(fixture.subscriptionId()))
                .isEqualTo(initialInvoiceCount);
        org.assertj.core.api.Assertions.assertThat(renewalCount(fixture.subscriptionId()))
                .isEqualTo(initialRenewalCount);
        org.assertj.core.api.Assertions.assertThat(paymentAssociationCount(unassociatedPaymentId)).isZero();
    }

    private Fixture createFixture(boolean payablePlan) {
        UUID userA = UUID.randomUUID();
        UUID customerA = UUID.randomUUID();
        UUID userB = UUID.randomUUID();
        UUID customerB = UUID.randomUUID();
        UUID planId = UUID.randomUUID();
        UUID planVersionId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        UUID entitlementId = UUID.randomUUID();
        UUID subscriptionId = UUID.randomUUID();
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        String emailA = "owner-test-a-" + suffix.toLowerCase() + "@example.test";
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = startDate.plusDays(364);

        insertCustomer(userA, customerA, emailA, "Authorization Test A");
        insertCustomer(userB, customerB, "owner-test-b-" + suffix.toLowerCase() + "@example.test",
                "Authorization Test B");
        UUID customerRoleId = jdbc.queryForObject(
                "SELECT role_id FROM roles WHERE role_code = 'CUSTOMER' AND status = 'ACTIVE'", UUID.class);
        jdbc.update("INSERT INTO user_roles (user_id, role_id) VALUES (?, ?), (?, ?)",
                userA, customerRoleId, userB, customerRoleId);

        jdbc.update("INSERT INTO subscription_plans (subscription_plan_id, plan_code, plan_name, status) "
                        + "VALUES (?, ?, 'Ownership Test Plan', 'ACTIVE')",
                planId, "OWNERSHIP_" + suffix);
        jdbc.update("INSERT INTO subscription_plan_versions "
                        + "(plan_version_id, subscription_plan_id, version_number, price, currency_code, "
                        + "effective_from, status, benefits_json) "
                        + "VALUES (?, ?, 1, ?, 'INR', CURRENT_TIMESTAMP, 'ACTIVE', '{}'::jsonb)",
                planVersionId, planId, payablePlan ? new BigDecimal("1000.00") : BigDecimal.ZERO);
        jdbc.update("INSERT INTO services (service_id, service_code, service_name, category, base_price, active_flag) "
                        + "VALUES (?, ?, 'Ownership Test Service', 'GENERAL_ASSISTANCE', 0.00, TRUE)",
                serviceId, "OWNERSHIP_" + suffix);
        jdbc.update("INSERT INTO subscription_plan_entitlements "
                        + "(entitlement_id, plan_version_id, service_id, quantity, period_type, "
                        + "carry_forward_allowed, status) VALUES (?, ?, ?, 3, 'TERM', FALSE, 'ACTIVE')",
                entitlementId, planVersionId, serviceId);
        jdbc.update("INSERT INTO customer_subscriptions "
                        + "(customer_subscription_id, customer_id, plan_version_id, status, start_date, end_date, auto_renew) "
                        + "VALUES (?, ?, ?, 'ACTIVE', ?, ?, FALSE)",
                subscriptionId, customerB, planVersionId, Date.valueOf(startDate), Date.valueOf(endDate));

        return new Fixture(customerB, subscriptionId, serviceId, entitlementId, endDate, emailA);
    }

    private void insertCustomer(UUID userId, UUID customerId, String email, String fullName) {
        jdbc.update("INSERT INTO users (user_id, full_name, email, password_hash, status) "
                        + "VALUES (?, ?, ?, ?, 'ACTIVE')",
                userId, fullName, email, "test-only-hash");
        jdbc.update("INSERT INTO customers (customer_id, user_id, customer_type, status) "
                        + "VALUES (?, ?, 'INDIVIDUAL', 'ACTIVE')",
                customerId, userId);
    }

    private String bearer(String email) {
        return "Bearer " + jwtService.generateToken(email, Map.of());
    }

    private long consumptionCount(UUID subscriptionId) {
        return count("SELECT count(*) FROM subscription_entitlement_consumptions "
                + "WHERE customer_subscription_id = ?", subscriptionId);
    }

    private long renewalInvoiceCount(UUID subscriptionId) {
        return count("SELECT count(*) FROM invoices WHERE entity_type = 'SUBSCRIPTION' "
                + "AND entity_id = ? AND invoice_type = 'SUBSCRIPTION_RENEWAL'", subscriptionId);
    }

    private long renewalCount(UUID subscriptionId) {
        return count("SELECT count(*) FROM subscription_renewals WHERE customer_subscription_id = ?", subscriptionId);
    }

    private long subscriptionCount(UUID customerId) {
        return count("SELECT count(*) FROM customer_subscriptions WHERE customer_id = ?", customerId);
    }

    private long paymentAssociationCount(UUID paymentId) {
        return count("SELECT count(*) FROM subscription_renewals WHERE payment_id = ?", paymentId);
    }

    private long count(String sql, Object parameter) {
        Long result = jdbc.queryForObject(sql, Long.class, parameter);
        return result == null ? 0 : result;
    }

    private long entitlementQuantity(UUID entitlementId) {
        return jdbc.queryForObject("SELECT quantity FROM subscription_plan_entitlements WHERE entitlement_id = ?",
                Long.class, entitlementId);
    }

    private String entitlementStatus(UUID entitlementId) {
        return jdbc.queryForObject("SELECT status FROM subscription_plan_entitlements WHERE entitlement_id = ?",
                String.class, entitlementId);
    }

    private String subscriptionStatus(UUID subscriptionId) {
        return jdbc.queryForObject("SELECT status FROM customer_subscriptions WHERE customer_subscription_id = ?",
                String.class, subscriptionId);
    }

    private LocalDate subscriptionEndDate(UUID subscriptionId) {
        return jdbc.queryForObject("SELECT end_date FROM customer_subscriptions WHERE customer_subscription_id = ?",
                (result, row) -> result.getDate(1).toLocalDate(), subscriptionId);
    }

    private record Fixture(UUID customerBId, UUID subscriptionId, UUID serviceId, UUID entitlementId,
                           LocalDate endDate, String customerAEmail) { }

    @AfterAll
    static void restoreDefaultTimeZone() {
        TimeZone.setDefault(ORIGINAL_DEFAULT_TIME_ZONE);
    }
}
