package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.CreatePaymentRequest;
import com.propertypilot.application.dto.PaymentSettlementEvent;
import com.propertypilot.application.service.impl.PaymentServiceImpl;
import com.propertypilot.config.PasswordConfig;
import com.propertypilot.config.SecurityConfig;
import com.propertypilot.config.OpenApiConfig;
import com.propertypilot.domain.enums.InvoiceStatus;
import com.propertypilot.domain.enums.PaymentMethod;
import com.propertypilot.domain.enums.PaymentStatus;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.InvoiceEntity;
import com.propertypilot.infrastructure.persistence.entity.PaymentEntity;
import com.propertypilot.infrastructure.persistence.entity.RoleEntity;
import com.propertypilot.infrastructure.persistence.entity.UserEntity;
import com.propertypilot.infrastructure.persistence.entity.UserRoleEntity;
import com.propertypilot.domain.enums.RoleStatus;
import com.propertypilot.infrastructure.persistence.repository.AgentAssignmentRepository;
import com.propertypilot.infrastructure.persistence.repository.AgentRepository;
import com.propertypilot.infrastructure.persistence.repository.CustomerRepository;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;
import com.propertypilot.infrastructure.persistence.repository.InvoiceRepository;
import com.propertypilot.infrastructure.persistence.repository.PaymentRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRoleRepository;
import com.propertypilot.security.CustomUserDetailsService;
import com.propertypilot.security.JwtAuthenticationFilter;
import com.propertypilot.security.JwtService;
import com.propertypilot.security.PaymentSettlementVerifier;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.GlobalExceptionHandler;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.webmvc.core.configuration.MultipleOpenApiSupportConfiguration;
import org.springdoc.webmvc.core.configuration.SpringDocWebMvcConfiguration;
import org.springdoc.core.properties.SpringDocConfigProperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PaymentController.class, properties = {
        "propertypilot.payment.webhook-secret=payment-http-test-secret-with-more-than-32-bytes"
})
@AutoConfigureMockMvc
@EnableConfigurationProperties(SpringDocConfigProperties.class)
@Import({SecurityConfig.class, PasswordConfig.class, JwtAuthenticationFilter.class,
        GlobalExceptionHandler.class, OpenApiConfig.class, PaymentServiceImpl.class, SecurityService.class,
        PaymentSettlementVerifier.class, SpringDocConfiguration.class, SpringDocWebMvcConfiguration.class,
        MultipleOpenApiSupportConfiguration.class})
class PaymentHttpSecurityTest {
    private static final String TEST_SECRET = "payment-http-test-secret-with-more-than-32-bytes";
    private static final String CUSTOMER_A = "customer-a@example.test";
    private static final String CUSTOMER_B = "customer-b@example.test";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private JwtService jwtService;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;
    @MockitoBean private PaymentRepository paymentRepository;
    @MockitoBean private InvoiceRepository invoiceRepository;
    @MockitoBean private CustomerSubscriptionRepository customerSubscriptionRepository;
    @MockitoBean private UserRepository userRepository;
    @MockitoBean private CustomerRepository customerRepository;
    @MockitoBean private UserRoleRepository userRoleRepository;
    @MockitoBean private AgentRepository agentRepository;
    @MockitoBean private AgentAssignmentRepository agentAssignmentRepository;

    private final UUID invoiceId = UUID.randomUUID();
    private final UUID paymentId = UUID.randomUUID();
    private final UUID customerAId = UUID.randomUUID();
    private final UUID customerBId = UUID.randomUUID();
    private final UUID userAId = UUID.randomUUID();
    private InvoiceEntity invoice;
    private PaymentEntity payment;
    private CustomerEntity customerA;
    private CustomerEntity customerB;

    @BeforeEach
    void setUp() {
        customerA = customer(customerAId);
        customerB = customer(customerBId);
        invoice = new InvoiceEntity();
        invoice.setInvoiceId(invoiceId);
        invoice.setCustomer(customerB);
        invoice.setInvoiceNumber("INV-HTTP-1");
        invoice.setInvoiceAmount(new BigDecimal("100.00"));
        invoice.setTaxAmount(new BigDecimal("0.00"));
        invoice.setCurrencyCode("INR");
        invoice.setStatus(InvoiceStatus.SENT);

        payment = new PaymentEntity();
        payment.setPaymentId(paymentId);
        payment.setCustomer(customerB);
        payment.setInvoice(invoice);
        payment.setAmount(new BigDecimal("100.00"));
        payment.setCurrencyCode("INR");
        payment.setProvider("TestPay");
        payment.setProviderPaymentId("provider-payment-1");
        payment.setPaymentMethod(PaymentMethod.UPI);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(OffsetDateTime.parse("2026-10-14T10:00:00Z"));

        UserEntity userA = new UserEntity();
        userA.setUserId(userAId);
        userA.setEmail(CUSTOMER_A);
        when(userRepository.findByEmail(CUSTOMER_A)).thenReturn(Optional.of(userA));
        when(userRoleRepository.findByUser_UserId(userAId)).thenReturn(List.of());
        when(customerRepository.findByUser_Email(CUSTOMER_A)).thenReturn(Optional.of(customerA));
    }

    @Test
    void anonymousCannotAccessProtectedPaymentEndpoints() throws Exception {
        mockMvc.perform(get("/api/payments")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/payments/{id}", paymentId)).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/payments/invoice/{id}", invoiceId)).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/payments/invoice/{id}", invoiceId)
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        verify(paymentRepository, never()).findById(any());
    }

    @Test
    @WithMockUser(username = CUSTOMER_A, roles = "CUSTOMER")
    void customerCannotUseAdminListAndCanOnlyReadOwnPaymentOrInvoice() throws Exception {
        mockMvc.perform(get("/api/payments")).andExpect(status().isForbidden());

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));

        mockMvc.perform(get("/api/payments/{id}", paymentId)).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/payments/invoice/{id}", invoiceId)).andExpect(status().isForbidden());

        invoice.setCustomer(customerA);
        payment.setCustomer(customerA);
        when(paymentRepository.findByInvoice_InvoiceId(invoiceId)).thenReturn(List.of(payment));
        mockMvc.perform(get("/api/payments/{id}", paymentId)).andExpect(status().isOk());
        mockMvc.perform(get("/api/payments/invoice/{id}", invoiceId)).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = CUSTOMER_A, roles = "CUSTOMER")
    void customerCannotCreatePaymentForAnotherCustomersInvoice() throws Exception {
        when(invoiceRepository.findByIdForUpdate(invoiceId)).thenReturn(Optional.of(invoice));
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setPaymentMethod(PaymentMethod.UPI);
        request.setProvider("TestPay");
        request.setProviderPaymentId("provider-payment-2");

        mockMvc.perform(post("/api/payments/invoice/{id}", invoiceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isForbidden());
        verify(paymentRepository, never()).saveAndFlush(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void administratorCanAccessAdminPaymentList() throws Exception {
        UUID adminUserId = UUID.randomUUID();
        UserEntity adminUser = new UserEntity();
        adminUser.setUserId(adminUserId);
        adminUser.setEmail("user");
        RoleEntity adminRole = new RoleEntity();
        adminRole.setRoleCode("ADMIN");
        adminRole.setStatus(RoleStatus.ACTIVE);
        UserRoleEntity assignment = new UserRoleEntity();
        assignment.setRole(adminRole);
        assignment.setEffectiveFrom(OffsetDateTime.now().minusMinutes(1));
        when(userRepository.findByEmail("user")).thenReturn(Optional.of(adminUser));
        when(userRoleRepository.findByUser_UserId(adminUserId)).thenReturn(List.of(assignment));
        when(paymentRepository.findAllWithDisplayDetails()).thenReturn(List.of(payment));
        mockMvc.perform(get("/api/payments")).andExpect(status().isOk());
    }

    @Test
    void publicWebhookAcceptsValidSignatureWithoutBearerToken() throws Exception {
        PaymentSettlementEvent event = event("webhook-success");
        prepareFirstSuccessfulSettlement(event);

        mockMvc.perform(post("/api/payments/settlement")
                        .contentType(MediaType.TEXT_PLAIN)
                        .header("X-PropertyPilot-Signature", sign(serialize(event)))
                        .content(serialize(event)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus").value("SUCCESS"));
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    void invalidSignatureAndMalformedSignedPayloadReturnBadRequest() throws Exception {
        String body = serialize(event("webhook-invalid"));
        mockMvc.perform(post("/api/payments/settlement").contentType(MediaType.TEXT_PLAIN)
                        .header("X-PropertyPilot-Signature", "00".repeat(32)).content(body))
                .andExpect(status().isBadRequest());

        String malformed = "{";
        mockMvc.perform(post("/api/payments/settlement").contentType(MediaType.TEXT_PLAIN)
                        .header("X-PropertyPilot-Signature", sign(malformed)).content(malformed))
                .andExpect(status().isBadRequest());
    }

    @Test
    void missingWebhookSignatureReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/payments/settlement")
                        .contentType(MediaType.TEXT_PLAIN).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void webhookExactReplaySucceedsAndConflictingEventFactsReturnBadRequest() throws Exception {
        PaymentSettlementEvent event = event("webhook-replay");
        payment.setProviderEventId(event.eventId());
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setSettledAt(event.settledAt());
        payment.setPaymentDate(event.settledAt());
        when(paymentRepository.findByProviderEventId(event.eventId())).thenReturn(Optional.of(payment));

        mockMvc.perform(post("/api/payments/settlement").contentType(MediaType.TEXT_PLAIN)
                        .header("X-PropertyPilot-Signature", sign(serialize(event))).content(serialize(event)))
                .andExpect(status().isOk());

        PaymentSettlementEvent conflict = new PaymentSettlementEvent(event.eventId(), event.provider(),
                event.providerPaymentId(), event.invoiceId(), new BigDecimal("99.00"), event.currencyCode(),
                event.status(), event.settledAt(), event.failureReason());
        mockMvc.perform(post("/api/payments/settlement").contentType(MediaType.TEXT_PLAIN)
                        .header("X-PropertyPilot-Signature", sign(serialize(conflict))).content(serialize(conflict)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void generatedOpenApiDocumentsWebhookAsPublicWithSignatureHeader() throws Exception {
        MvcResult result = mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk()).andReturn();
        var document = objectMapper.readTree(result.getResponse().getContentAsString());
        var operation = document.at("/paths/~1api~1payments~1settlement/post");
        assertThat(operation.isMissingNode()).isFalse();
        assertThat(operation.has("security")).isTrue();
        assertThat(operation.path("security").isEmpty()).isTrue();
        assertThat(operation.path("parameters").toString()).contains("X-PropertyPilot-Signature");
        assertThat(operation.path("summary").asText()).contains("signed provider event");
        var protectedOperation = document.at("/paths/~1api~1payments/get");
        assertThat(protectedOperation.isMissingNode()).isFalse();
        assertThat(document.path("security").toString()).contains("bearerAuth");
    }

    private CustomerEntity customer(UUID id) {
        CustomerEntity customer = new CustomerEntity();
        customer.setCustomerId(id);
        return customer;
    }

    private PaymentSettlementEvent event(String eventId) {
        return new PaymentSettlementEvent(eventId, "TestPay", "provider-payment-1", invoiceId,
                new BigDecimal("100.00"), "INR", "SUCCEEDED",
                OffsetDateTime.parse("2026-10-14T10:15:00Z"), null);
    }

    private void prepareFirstSuccessfulSettlement(PaymentSettlementEvent event) {
        when(paymentRepository.findByProviderEventId(event.eventId())).thenReturn(Optional.empty());
        when(paymentRepository.findInvoiceIdByProviderAndProviderPaymentId("TestPay", "provider-payment-1"))
                .thenReturn(Optional.of(invoiceId));
        when(invoiceRepository.findByIdForUpdate(invoiceId)).thenReturn(Optional.of(invoice));
        when(paymentRepository.findByProviderAndProviderPaymentIdForUpdate("TestPay", "provider-payment-1"))
                .thenReturn(Optional.of(payment));
        when(paymentRepository.sumVerifiedSettledAmountByInvoiceAndCurrency(
                invoiceId, PaymentStatus.SUCCESS, "INR")).thenReturn(BigDecimal.ZERO);
        when(paymentRepository.saveAndFlush(payment)).thenReturn(payment);
        when(invoiceRepository.save(invoice)).thenReturn(invoice);
    }

    private String serialize(PaymentSettlementEvent event) throws Exception {
        return objectMapper.writeValueAsString(event);
    }

    private String sign(String body) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(TEST_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return HexFormat.of().formatHex(mac.doFinal(body.getBytes(StandardCharsets.UTF_8)));
    }
}
