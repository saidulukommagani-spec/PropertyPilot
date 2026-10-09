package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.InvoiceResponse;
import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.InvoiceStatus;
import com.propertypilot.domain.enums.InvoiceType;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.InvoiceEntity;
import com.propertypilot.infrastructure.persistence.entity.DealEntity;
import com.propertypilot.infrastructure.persistence.entity.PricingEstimateEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerRepository;
import com.propertypilot.infrastructure.persistence.repository.DealRepository;
import com.propertypilot.infrastructure.persistence.repository.InvoiceRepository;
import com.propertypilot.infrastructure.persistence.repository.PaymentRepository;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionPlanVersionRepository;
import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanVersionEntity;
import com.propertypilot.security.PaymentSettlementVerifier;
import com.propertypilot.web.exception.ResourceNotFoundException;
import com.propertypilot.web.exception.BusinessException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.propertypilot.security.SecurityService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class BillingServiceImplTest {
    @Mock private SecurityService securityService;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private DealRepository dealRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock private CustomerSubscriptionRepository customerSubscriptionRepository;

    @Mock private SubscriptionPlanVersionRepository planVersionRepository;

    @Mock
    private PaymentSettlementVerifier settlementVerifier;

    @InjectMocks
    private BillingServiceImpl billingService;

    @BeforeEach
    void stubInvoiceNumberSequence() {
        AtomicLong nextNumber = new AtomicLong(1_800_000_000_000L);
        lenient().when(invoiceRepository.nextInvoiceNumberSequence())
                .thenAnswer(invocation -> nextNumber.incrementAndGet());
    }

    private InvoiceEntity buildInvoice() {

        CustomerEntity customer =
                new CustomerEntity();

        customer.setCustomerId(
                UUID.randomUUID());

        InvoiceEntity invoice =
                new InvoiceEntity();

        invoice.setInvoiceId(
                UUID.randomUUID());

        invoice.setCustomer(customer);

        invoice.setEntityType(
                BillingEntityType.DEAL);

        invoice.setEntityId(
                UUID.randomUUID());

        invoice.setInvoiceType(
                InvoiceType.BUYER_COMMISSION);

        invoice.setInvoiceNumber(
                "PP-INV-001");

        invoice.setInvoiceAmount(
                BigDecimal.valueOf(1000));

        invoice.setTaxAmount(
                BigDecimal.ZERO);

        invoice.setCurrencyCode("INR");

        invoice.setStatus(
                InvoiceStatus.SENT);

        invoice.setDescription(
                "Test Invoice");

        invoice.setIssuedAt(
                OffsetDateTime.now());

        return invoice;
    }

    @Test
    void overdueSchedulerLocksOpenInvoiceRowsBeforeTransitioningThem() {
        InvoiceEntity invoice = buildInvoice();
        invoice.setStatus(InvoiceStatus.SENT);
        when(invoiceRepository.findByStatusInAndDueAtBeforeForUpdate(
                eq(List.of(InvoiceStatus.SENT, InvoiceStatus.PARTIALLY_PAID)), any(OffsetDateTime.class)))
                .thenReturn(List.of(invoice));

        billingService.markOverdueInvoices();

        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.OVERDUE);
        verify(invoiceRepository).findByStatusInAndDueAtBeforeForUpdate(
                eq(List.of(InvoiceStatus.SENT, InvoiceStatus.PARTIALLY_PAID)), any(OffsetDateTime.class));
    }

    @Test
    void createInvoice_ShouldCreateInvoice() {

        UUID customerId =
                UUID.randomUUID();

        CustomerEntity customer =
                new CustomerEntity();

        customer.setCustomerId(
                customerId);

        UUID dealId = UUID.randomUUID();
        DealEntity deal = new DealEntity();
        deal.setDealId(dealId);

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(dealRepository.findById(dealId))
                .thenReturn(Optional.of(deal));

        when(invoiceRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InvoiceResponse response =
                billingService.createInvoice(
                        BillingEntityType.DEAL,
                        dealId,
                        customerId,
                        InvoiceType.BUYER_COMMISSION,
                        BigDecimal.valueOf(1000),
                        "Commission Invoice");

        assertThat(response)
                .isNotNull();
        assertThat(response.getCurrencyCode()).isEqualTo("INR");
        assertThat(response.getInvoiceNumber()).isEqualTo("PP-INV-1800000000001");

        verify(invoiceRepository)
                .save(any());
        verify(invoiceRepository).nextInvoiceNumberSequence();
    }

    @Test
    void createInvoice_ShouldThrow_WhenCustomerNotFound() {

        UUID customerId =
                UUID.randomUUID();

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                billingService.createInvoice(
                        BillingEntityType.DEAL,
                        UUID.randomUUID(),
                        customerId,
                        InvoiceType.BUYER_COMMISSION,
                        BigDecimal.valueOf(1000),
                        "Commission"))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }

    @Test
    void createInvoice_WithCurrencyCode_PreservesTheProvidedPlanCurrency() {
        UUID customerId = UUID.randomUUID();
        UUID subscriptionId = UUID.randomUUID();
        CustomerEntity customer = new CustomerEntity();
        customer.setCustomerId(customerId);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(invoiceRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        InvoiceResponse response = billingService.createInvoice(
                BillingEntityType.SUBSCRIPTION,
                subscriptionId,
                customerId,
                InvoiceType.SUBSCRIPTION_FEE,
                new BigDecimal("25.00"),
                "Subscription purchase invoice",
                "usd");

        assertThat(response.getCurrencyCode()).isEqualTo("USD");
        org.mockito.ArgumentCaptor<InvoiceEntity> invoiceCaptor =
                org.mockito.ArgumentCaptor.forClass(InvoiceEntity.class);
        verify(invoiceRepository).save(invoiceCaptor.capture());
        assertThat(invoiceCaptor.getValue().getCurrencyCode()).isEqualTo("USD");
    }

    @Test
    void createInvoice_WithInvalidCurrencyCode_RejectsInvoiceCreation() {
        UUID customerId = UUID.randomUUID();
        CustomerEntity customer = new CustomerEntity();
        customer.setCustomerId(customerId);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> billingService.createInvoice(
                BillingEntityType.SUBSCRIPTION,
                UUID.randomUUID(),
                customerId,
                InvoiceType.SUBSCRIPTION_FEE,
                new BigDecimal("25.00"),
                "Subscription purchase invoice",
                "ZZZ"))
                .isInstanceOf(com.propertypilot.web.exception.BusinessException.class)
                .hasMessage("Invoice currency must be a valid ISO 4217 code");
        verify(invoiceRepository, never()).save(any());
    }

    @Test
    void createInvoiceFromCompletedServiceRequest_PreservesQuoteTaxAndRequestLink() {
        UUID requestId = UUID.randomUUID();
        CustomerEntity customer = new CustomerEntity();
        customer.setCustomerId(UUID.randomUUID());
        ServiceEntity service = new ServiceEntity();
        service.setServiceName("Property inspection");
        service.setBillable(true);
        PricingEstimateEntity estimate = new PricingEstimateEntity();
        estimate.setTotalAmount(new BigDecimal("118.00"));
        estimate.setTaxAmount(new BigDecimal("18.00"));
        estimate.setCurrencyCode("INR");
        ServiceRequestEntity request = new ServiceRequestEntity();
        request.setServiceRequestId(requestId);
        request.setCustomer(customer);
        request.setService(service);
        request.setPricingEstimate(estimate);
        request.setAmount(new BigDecimal("118.00"));
        request.setStatus("COMPLETED");
        when(invoiceRepository.existsByEntityTypeAndEntityId(BillingEntityType.SERVICE_REQUEST, requestId))
                .thenReturn(false);
        when(invoiceRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        InvoiceResponse response = billingService.createInvoiceFromCompletedServiceRequest(request);

        assertThat(response.getEntityType()).isEqualTo(BillingEntityType.SERVICE_REQUEST);
        assertThat(response.getEntityId()).isEqualTo(requestId);
        assertThat(response.getInvoiceType()).isEqualTo(InvoiceType.SERVICE_CHARGE);
        assertThat(response.getInvoiceAmount()).isEqualByComparingTo("100.00");
        assertThat(response.getTaxAmount()).isEqualByComparingTo("18.00");
        assertThat(response.getStatus()).isEqualTo(InvoiceStatus.DRAFT);
        assertThat(response.getDescription()).isEqualTo("Property inspection service");
        assertThat(response.getDescription()).doesNotContain(requestId.toString());
        verify(dealRepository, never()).save(any());
    }

    @Test
    void createInvoiceFromCompletedServiceRequest_ReturnsExistingInvoiceOnRetry() {
        ServiceRequestEntity request = new ServiceRequestEntity();
        UUID requestId = UUID.randomUUID();
        request.setServiceRequestId(requestId);
        request.setStatus("COMPLETED");
        CustomerEntity customer = new CustomerEntity();
        customer.setCustomerId(UUID.randomUUID());
        request.setCustomer(customer);
        ServiceEntity service = new ServiceEntity();
        service.setBillable(true);
        request.setService(service);
        PricingEstimateEntity estimate = new PricingEstimateEntity();
        estimate.setTotalAmount(new BigDecimal("100.00"));
        request.setPricingEstimate(estimate);
        request.setAmount(new BigDecimal("100.00"));
        InvoiceEntity existing = buildInvoice();
        existing.setEntityType(BillingEntityType.SERVICE_REQUEST);
        existing.setEntityId(requestId);
        when(invoiceRepository.existsByEntityTypeAndEntityId(BillingEntityType.SERVICE_REQUEST, requestId))
                .thenReturn(true);
        when(invoiceRepository.findByEntityTypeAndEntityId(BillingEntityType.SERVICE_REQUEST, requestId))
                .thenReturn(List.of(existing));

        InvoiceResponse response = billingService.createInvoiceFromCompletedServiceRequest(request);

        assertThat(response.getInvoiceId()).isEqualTo(existing.getInvoiceId());
        verify(invoiceRepository, never()).save(any());
    }

    @Test
    void createSubscriptionRenewalInvoice_createsDatedInvoiceAtCurrentPlanPrice() {
        UUID subscriptionId = UUID.randomUUID();
        LocalDate periodStart = java.time.LocalDate.of(2027, 1, 1);
        LocalDate periodEnd = java.time.LocalDate.of(2027, 12, 31);
        CustomerEntity customer = new CustomerEntity();
        customer.setCustomerId(UUID.randomUUID());
        SubscriptionPlanEntity plan = new SubscriptionPlanEntity();
        plan.setSubscriptionPlanId(UUID.randomUUID());
        SubscriptionPlanVersionEntity current = new SubscriptionPlanVersionEntity();
        current.setSubscriptionPlan(plan);
        current.setPrice(new BigDecimal("1500.00"));
        current.setCurrencyCode("INR");
        CustomerSubscriptionEntity subscription = new CustomerSubscriptionEntity();
        subscription.setCustomerSubscriptionId(subscriptionId);
        subscription.setCustomer(customer);
        subscription.setPlanVersion(current);
        subscription.setEndDate(periodStart.minusDays(1));
        subscription.setStartDate(periodStart.minusYears(1));
        subscription.setStatus("ACTIVE");
        SubscriptionPlanVersionEntity latest = new SubscriptionPlanVersionEntity();
        latest.setPrice(new BigDecimal("1750.00"));
        latest.setCurrencyCode("USD");

        when(customerSubscriptionRepository.findByIdForUpdate(subscriptionId)).thenReturn(Optional.of(subscription));
        when(planVersionRepository.findTopBySubscriptionPlan_SubscriptionPlanIdOrderByVersionNumberDesc(plan.getSubscriptionPlanId()))
                .thenReturn(Optional.of(latest));
        when(invoiceRepository.findByEntityTypeAndEntityIdAndInvoiceType(
                BillingEntityType.SUBSCRIPTION, subscriptionId, InvoiceType.SUBSCRIPTION_RENEWAL)).thenReturn(List.of());
        when(invoiceRepository.save(any(InvoiceEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InvoiceResponse response = billingService.createSubscriptionRenewalInvoice(subscriptionId, periodStart, periodEnd);

        assertThat(response.getInvoiceType()).isEqualTo(InvoiceType.SUBSCRIPTION_RENEWAL);
        assertThat(response.getInvoiceAmount()).isEqualByComparingTo("1750.00");
        assertThat(response.getCurrencyCode()).isEqualTo("USD");
        assertThat(response.getRenewalPeriodStart()).isEqualTo(periodStart);
        assertThat(response.getRenewalPeriodEnd()).isEqualTo(periodEnd);
        assertThat(response.getStatus()).isEqualTo(InvoiceStatus.DRAFT);
    }

    @Test
    void createSubscriptionRenewalInvoice_isIdempotentForTheSameOpenPeriod() {
        UUID subscriptionId = UUID.randomUUID();
        var periodStart = java.time.LocalDate.of(2027, 1, 1);
        var periodEnd = java.time.LocalDate.of(2027, 12, 31);
        CustomerEntity customer = new CustomerEntity();
        customer.setCustomerId(UUID.randomUUID());
        CustomerSubscriptionEntity subscription = new CustomerSubscriptionEntity();
        subscription.setCustomerSubscriptionId(subscriptionId);
        subscription.setCustomer(customer);
        subscription.setEndDate(periodStart.minusDays(1));
        subscription.setStartDate(periodStart.minusYears(1));
        subscription.setStatus("ACTIVE");
        SubscriptionPlanEntity plan = new SubscriptionPlanEntity();
        plan.setSubscriptionPlanId(UUID.randomUUID());
        SubscriptionPlanVersionEntity current = new SubscriptionPlanVersionEntity();
        current.setSubscriptionPlan(plan);
        subscription.setPlanVersion(current);
        SubscriptionPlanVersionEntity latest = new SubscriptionPlanVersionEntity();
        latest.setPrice(new BigDecimal("1500.00"));
        InvoiceEntity existing = buildInvoice();
        existing.setInvoiceType(InvoiceType.SUBSCRIPTION_RENEWAL);
        existing.setEntityType(BillingEntityType.SUBSCRIPTION);
        existing.setEntityId(subscriptionId);
        existing.setRenewalPeriodStart(periodStart);
        existing.setRenewalPeriodEnd(periodEnd);

        when(customerSubscriptionRepository.findByIdForUpdate(subscriptionId)).thenReturn(Optional.of(subscription));
        when(planVersionRepository.findTopBySubscriptionPlan_SubscriptionPlanIdOrderByVersionNumberDesc(plan.getSubscriptionPlanId()))
                .thenReturn(Optional.of(latest));
        when(invoiceRepository.findByEntityTypeAndEntityIdAndInvoiceType(
                BillingEntityType.SUBSCRIPTION, subscriptionId, InvoiceType.SUBSCRIPTION_RENEWAL))
                .thenReturn(List.of(existing));

        InvoiceResponse response = billingService.createSubscriptionRenewalInvoice(subscriptionId, periodStart, periodEnd);

        assertThat(response.getInvoiceId()).isEqualTo(existing.getInvoiceId());
        verify(invoiceRepository, never()).save(any(InvoiceEntity.class));
    }

    @Test
    void getInvoice_ShouldReturnInvoice() {

        UUID invoiceId =
                UUID.randomUUID();

        InvoiceEntity invoice =
                buildInvoice();

        when(invoiceRepository.findById(invoiceId))
                .thenReturn(Optional.of(invoice));

        InvoiceResponse response =
                billingService.getInvoice(
                        invoiceId);

        assertThat(response)
                .isNotNull();
    }

    @Test
    void getInvoice_ShouldThrow_WhenNotFound() {

        UUID invoiceId =
                UUID.randomUUID();

        when(invoiceRepository.findById(invoiceId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                billingService.getInvoice(
                        invoiceId))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }

    @Test
    void getAllInvoices_ShouldReturnList() {

        when(invoiceRepository.findAll())
                .thenReturn(
                        List.of(buildInvoice()));

        List<InvoiceResponse> result =
                billingService.getAllInvoices();

        assertThat(result)
                .hasSize(1);
    }

    @Test
    void getInvoicesByCustomer_ShouldReturnList() {

        UUID customerId =
                UUID.randomUUID();

        when(invoiceRepository
                .findByCustomerCustomerId(
                        customerId))
                .thenReturn(
                        List.of(buildInvoice()));

        List<InvoiceResponse> result =
                billingService
                        .getInvoicesByCustomer(
                                customerId);

        assertThat(result)
                .hasSize(1);
    }

    @Test
    void getInvoicesByEntity_ShouldReturnList() {

        UUID entityId =
                UUID.randomUUID();

        when(invoiceRepository
                .findByEntityTypeAndEntityId(
                        BillingEntityType.DEAL,
                        entityId))
                .thenReturn(
                        List.of(buildInvoice()));

        List<InvoiceResponse> result =
                billingService
                        .getInvoicesByEntity(
                                BillingEntityType.DEAL,
                                entityId);

        assertThat(result)
                .hasSize(1);
    }

    @Test
    void cancelInvoice_RejectsPendingPaymentToPreventLateSettlementReopeningInvoice() {
        InvoiceEntity invoice = buildInvoice();
        invoice.setStatus(InvoiceStatus.SENT);
        when(invoiceRepository.findByIdForUpdate(invoice.getInvoiceId())).thenReturn(Optional.of(invoice));
        when(paymentRepository.existsByInvoice_InvoiceIdAndPaymentStatus(
                invoice.getInvoiceId(), com.propertypilot.domain.enums.PaymentStatus.PENDING)).thenReturn(true);

        assertThatThrownBy(() -> billingService.cancelInvoice(invoice.getInvoiceId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("An invoice with a pending payment cannot be cancelled");

        verify(invoiceRepository, never()).save(any(InvoiceEntity.class));
        verify(paymentRepository, never()).existsByInvoice_InvoiceIdAndPaymentStatus(
                invoice.getInvoiceId(), com.propertypilot.domain.enums.PaymentStatus.SUCCESS);
    }

    @Test
    void cancelInvoice_AllowsCancellationWhenNoPaymentIsPendingOrSettled() {
        InvoiceEntity invoice = buildInvoice();
        invoice.setStatus(InvoiceStatus.SENT);
        when(invoiceRepository.findByIdForUpdate(invoice.getInvoiceId())).thenReturn(Optional.of(invoice));
        when(paymentRepository.existsByInvoice_InvoiceIdAndPaymentStatus(
                invoice.getInvoiceId(), com.propertypilot.domain.enums.PaymentStatus.PENDING)).thenReturn(false);
        when(paymentRepository.existsByInvoice_InvoiceIdAndPaymentStatus(
                invoice.getInvoiceId(), com.propertypilot.domain.enums.PaymentStatus.SUCCESS)).thenReturn(false);
        when(invoiceRepository.save(invoice)).thenReturn(invoice);

        InvoiceResponse response = billingService.cancelInvoice(invoice.getInvoiceId());

        assertThat(response.getStatus()).isEqualTo(InvoiceStatus.CANCELLED);
        verify(invoiceRepository).save(invoice);
    }
}
