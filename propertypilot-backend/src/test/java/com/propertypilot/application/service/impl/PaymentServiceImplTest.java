package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreatePaymentRequest;
import com.propertypilot.application.dto.PaymentSettlementEvent;
import com.propertypilot.domain.enums.InvoiceStatus;
import com.propertypilot.domain.enums.PaymentMethod;
import com.propertypilot.domain.enums.PaymentStatus;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.InvoiceEntity;
import com.propertypilot.infrastructure.persistence.entity.PaymentEntity;
import com.propertypilot.infrastructure.persistence.entity.UserEntity;
import com.propertypilot.infrastructure.persistence.repository.InvoiceRepository;
import com.propertypilot.infrastructure.persistence.repository.PaymentRepository;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;
import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import com.propertypilot.security.PaymentSettlementVerifier;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private InvoiceRepository invoiceRepository;
    @Mock private SecurityService securityService;
    @Mock private PaymentSettlementVerifier settlementVerifier;
    @Mock private CustomerSubscriptionRepository customerSubscriptionRepository;

    private PaymentServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PaymentServiceImpl(paymentRepository, invoiceRepository, securityService, settlementVerifier, customerSubscriptionRepository);
    }

    @Test
    void recordPaymentCreatesPendingPaymentWithoutSettlingInvoice() {
        InvoiceEntity invoice = openInvoice();
        CreatePaymentRequest request = paymentRequest(new BigDecimal("1000.00"));
        PaymentEntity saved = pendingPayment(invoice, request);
        when(invoiceRepository.findByIdForUpdate(invoice.getInvoiceId())).thenReturn(Optional.of(invoice));
        when(paymentRepository.findByProviderAndProviderPaymentId("PROVIDER", "TX-1")).thenReturn(Optional.empty());
        when(paymentRepository.sumVerifiedSettledAmountByInvoiceAndCurrency(invoice.getInvoiceId(), PaymentStatus.SUCCESS, "INR")).thenReturn(BigDecimal.ZERO);
        when(paymentRepository.sumAmountByInvoiceAndStatus(invoice.getInvoiceId(), PaymentStatus.PENDING)).thenReturn(BigDecimal.ZERO);
        when(paymentRepository.saveAndFlush(any(PaymentEntity.class))).thenReturn(saved);

        var response = service.recordPayment(invoice.getInvoiceId(), request);

        assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(response.getCurrencyCode()).isEqualTo("INR");
        assertThat(response.getInvoiceNumber()).isEqualTo(invoice.getInvoiceNumber());
        assertThat(response.getCustomerName()).isEqualTo(invoice.getCustomer().getUser().getFullName());
        verify(paymentRepository).sumVerifiedSettledAmountByInvoiceAndCurrency(invoice.getInvoiceId(), PaymentStatus.SUCCESS, "INR");
        verify(paymentRepository, never()).sumAmountByInvoiceAndStatus(invoice.getInvoiceId(), PaymentStatus.SUCCESS);
        verify(invoiceRepository, never()).save(any(InvoiceEntity.class));
    }

    @Test
    void getAllPaymentsRequiresOperationsAccessAndIncludesBusinessLabels() {
        InvoiceEntity invoice = openInvoice();
        PaymentEntity payment = pendingPayment(invoice, paymentRequest(new BigDecimal("1000.00")));
        when(paymentRepository.findAllWithDisplayDetails()).thenReturn(java.util.List.of(payment));

        var rows = service.getAllPayments();

        verify(securityService).validateAdminAccess();
        assertThat(rows).singleElement().satisfies(row -> {
            assertThat(row.getInvoiceNumber()).isEqualTo("INV-TEST-001");
            assertThat(row.getCustomerName()).isEqualTo("Payment Test Customer");
            assertThat(row.getCurrencyCode()).isEqualTo("INR");
        });
    }

    @Test
    void recordPaymentRejectsDuplicateProviderTransaction() {
        InvoiceEntity invoice = openInvoice();
        CreatePaymentRequest request = paymentRequest(new BigDecimal("1000.00"));
        when(invoiceRepository.findByIdForUpdate(invoice.getInvoiceId())).thenReturn(Optional.of(invoice));
        when(paymentRepository.findByProviderAndProviderPaymentId("PROVIDER", "TX-1"))
                .thenReturn(Optional.of(new PaymentEntity()));

        assertThatThrownBy(() -> service.recordPayment(invoice.getInvoiceId(), request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Provider payment id has already been registered");
    }

    @Test
    void recordPaymentTranslatesConcurrentProviderReferenceCollision() {
        InvoiceEntity invoice = openInvoice();
        CreatePaymentRequest request = paymentRequest(new BigDecimal("100.00"));
        when(invoiceRepository.findByIdForUpdate(invoice.getInvoiceId())).thenReturn(Optional.of(invoice));
        when(paymentRepository.findByProviderAndProviderPaymentId("PROVIDER", "TX-1")).thenReturn(Optional.empty());
        when(paymentRepository.sumVerifiedSettledAmountByInvoiceAndCurrency(invoice.getInvoiceId(), PaymentStatus.SUCCESS, "INR"))
                .thenReturn(BigDecimal.ZERO);
        when(paymentRepository.sumAmountByInvoiceAndStatus(invoice.getInvoiceId(), PaymentStatus.PENDING))
                .thenReturn(BigDecimal.ZERO);
        when(paymentRepository.saveAndFlush(any(PaymentEntity.class)))
                .thenThrow(new DataIntegrityViolationException("provider reference already exists"));

        assertThatThrownBy(() -> service.recordPayment(invoice.getInvoiceId(), request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Provider payment id has already been registered");
        verify(invoiceRepository, never()).save(any(InvoiceEntity.class));
    }

    @Test
    void settlementRequiresVerifiedEventAndThenSettlesInvoice() {
        InvoiceEntity invoice = openInvoice();
        PaymentEntity payment = pendingPayment(invoice, paymentRequest(new BigDecimal("1000.00")));
        OffsetDateTime settledAt = OffsetDateTime.now();
        PaymentSettlementEvent event = new PaymentSettlementEvent(
                "event-1", "PROVIDER", "TX-1", invoice.getInvoiceId(), new BigDecimal("1000.00"),
                "INR", "SUCCEEDED", settledAt, null);
        when(settlementVerifier.verify("signed-body", "signature")).thenReturn(event);
        when(paymentRepository.findByProviderEventId("event-1")).thenReturn(Optional.empty());
        when(paymentRepository.findInvoiceIdByProviderAndProviderPaymentId("PROVIDER", "TX-1"))
                .thenReturn(Optional.of(invoice.getInvoiceId()));
        when(invoiceRepository.findByIdForUpdate(invoice.getInvoiceId())).thenReturn(Optional.of(invoice));
        when(paymentRepository.findByProviderAndProviderPaymentIdForUpdate("PROVIDER", "TX-1"))
                .thenReturn(Optional.of(payment));
        when(paymentRepository.sumVerifiedSettledAmountByInvoiceAndCurrency(invoice.getInvoiceId(), PaymentStatus.SUCCESS, "INR")).thenReturn(BigDecimal.ZERO);
        when(paymentRepository.saveAndFlush(payment)).thenReturn(payment);

        var response = service.confirmSettlement("signed-body", "signature");

        assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.PAID);
        assertThat(invoice.getPaidAt()).isEqualTo(settledAt);
        verify(paymentRepository).sumVerifiedSettledAmountByInvoiceAndCurrency(invoice.getInvoiceId(), PaymentStatus.SUCCESS, "INR");
        verify(paymentRepository, never()).sumAmountByInvoiceAndStatus(invoice.getInvoiceId(), PaymentStatus.SUCCESS);
        verify(invoiceRepository).save(invoice);
    }

    @Test
    void partialSettlementKeepsPastDueInvoiceOverdue() {
        InvoiceEntity invoice = openInvoice();
        invoice.setStatus(InvoiceStatus.OVERDUE);
        invoice.setDueAt(OffsetDateTime.now().minusDays(1));
        CreatePaymentRequest request = paymentRequest(new BigDecimal("100.00"));
        PaymentEntity payment = pendingPayment(invoice, request);
        OffsetDateTime settledAt = OffsetDateTime.now();
        PaymentSettlementEvent event = new PaymentSettlementEvent(
                "overdue-partial-event", "PROVIDER", "TX-1", invoice.getInvoiceId(), request.getAmount(),
                "INR", "SUCCEEDED", settledAt, null);
        when(settlementVerifier.verify("signed-body", "signature")).thenReturn(event);
        when(paymentRepository.findByProviderEventId(event.eventId())).thenReturn(Optional.empty());
        when(paymentRepository.findInvoiceIdByProviderAndProviderPaymentId("PROVIDER", "TX-1"))
                .thenReturn(Optional.of(invoice.getInvoiceId()));
        when(invoiceRepository.findByIdForUpdate(invoice.getInvoiceId())).thenReturn(Optional.of(invoice));
        when(paymentRepository.findByProviderAndProviderPaymentIdForUpdate("PROVIDER", "TX-1"))
                .thenReturn(Optional.of(payment));
        when(paymentRepository.sumVerifiedSettledAmountByInvoiceAndCurrency(invoice.getInvoiceId(), PaymentStatus.SUCCESS, "INR"))
                .thenReturn(BigDecimal.ZERO);
        when(paymentRepository.saveAndFlush(payment)).thenReturn(payment);

        service.confirmSettlement("signed-body", "signature");

        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.OVERDUE);
        assertThat(invoice.getPaidAt()).isNull();
        verify(invoiceRepository).save(invoice);
    }

    @Test
    void partialSettlementBeforeDueDateLeavesInvoicePartiallyPaid() {
        InvoiceEntity invoice = openInvoice();
        invoice.setDueAt(OffsetDateTime.now().plusDays(1));
        CreatePaymentRequest request = paymentRequest(new BigDecimal("100.00"));
        PaymentEntity payment = pendingPayment(invoice, request);
        OffsetDateTime settledAt = OffsetDateTime.now();
        PaymentSettlementEvent event = new PaymentSettlementEvent(
                "partial-event", "PROVIDER", "TX-1", invoice.getInvoiceId(), request.getAmount(),
                "INR", "SUCCEEDED", settledAt, null);
        when(settlementVerifier.verify("signed-body", "signature")).thenReturn(event);
        when(paymentRepository.findByProviderEventId(event.eventId())).thenReturn(Optional.empty());
        when(paymentRepository.findInvoiceIdByProviderAndProviderPaymentId("PROVIDER", "TX-1"))
                .thenReturn(Optional.of(invoice.getInvoiceId()));
        when(invoiceRepository.findByIdForUpdate(invoice.getInvoiceId())).thenReturn(Optional.of(invoice));
        when(paymentRepository.findByProviderAndProviderPaymentIdForUpdate("PROVIDER", "TX-1"))
                .thenReturn(Optional.of(payment));
        when(paymentRepository.sumVerifiedSettledAmountByInvoiceAndCurrency(invoice.getInvoiceId(), PaymentStatus.SUCCESS, "INR"))
                .thenReturn(BigDecimal.ZERO);
        when(paymentRepository.saveAndFlush(payment)).thenReturn(payment);

        service.confirmSettlement("signed-body", "signature");

        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.PARTIALLY_PAID);
        assertThat(invoice.getPaidAt()).isNull();
    }

    @Test
    void repeatedSettlementEventIsIdempotentAfterPaymentLock() {
        InvoiceEntity invoice = openInvoice();
        PaymentEntity payment = pendingPayment(invoice, paymentRequest(new BigDecimal("1000.00")));
        payment.setProviderEventId("event-duplicate");
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        PaymentSettlementEvent event = new PaymentSettlementEvent(
                "event-duplicate", "PROVIDER", "TX-1", invoice.getInvoiceId(), new BigDecimal("1000.00"),
                "INR", "SUCCEEDED", OffsetDateTime.now(), null);
        when(settlementVerifier.verify("signed-body", "signature")).thenReturn(event);
        when(paymentRepository.findByProviderEventId(event.eventId())).thenReturn(Optional.empty());
        when(paymentRepository.findInvoiceIdByProviderAndProviderPaymentId("PROVIDER", "TX-1"))
                .thenReturn(Optional.of(invoice.getInvoiceId()));
        when(invoiceRepository.findByIdForUpdate(invoice.getInvoiceId())).thenReturn(Optional.of(invoice));
        when(paymentRepository.findByProviderAndProviderPaymentIdForUpdate("PROVIDER", "TX-1"))
                .thenReturn(Optional.of(payment));

        var response = service.confirmSettlement("signed-body", "signature");

        assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
        verify(paymentRepository, never()).saveAndFlush(any(PaymentEntity.class));
        verify(invoiceRepository, never()).save(any(InvoiceEntity.class));
    }

    @Test
    void repeatedSettlementEventWithSameFactsIsIdempotent() {
        InvoiceEntity invoice = openInvoice();
        OffsetDateTime settledAt = OffsetDateTime.now();
        PaymentEntity payment = pendingPayment(invoice, paymentRequest(new BigDecimal("1000.00")));
        payment.setProviderEventId("event-retry");
        payment.setPaymentDate(settledAt);
        payment.setSettledAt(settledAt);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        PaymentSettlementEvent event = new PaymentSettlementEvent(
                "event-retry", "PROVIDER", "TX-1", invoice.getInvoiceId(), new BigDecimal("1000.00"),
                "INR", "SUCCEEDED", settledAt, null);
        when(settlementVerifier.verify("signed-body", "signature")).thenReturn(event);
        when(paymentRepository.findByProviderEventId("event-retry")).thenReturn(Optional.of(payment));

        var response = service.confirmSettlement("signed-body", "signature");

        assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
        verify(invoiceRepository, never()).findByIdForUpdate(any());
        verify(paymentRepository, never()).saveAndFlush(any());
    }

    @Test
    void settlementEventIdCollisionWithDifferentFactsIsRejected() {
        InvoiceEntity invoice = openInvoice();
        OffsetDateTime settledAt = OffsetDateTime.now();
        PaymentEntity payment = pendingPayment(invoice, paymentRequest(new BigDecimal("1000.00")));
        payment.setProviderEventId("event-collision");
        payment.setPaymentDate(settledAt);
        payment.setSettledAt(settledAt);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        PaymentSettlementEvent collision = new PaymentSettlementEvent(
                "event-collision", "PROVIDER", "OTHER-TX", invoice.getInvoiceId(), new BigDecimal("1000.00"),
                "INR", "SUCCEEDED", settledAt, null);
        when(settlementVerifier.verify("signed-body", "signature")).thenReturn(collision);
        when(paymentRepository.findByProviderEventId("event-collision")).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> service.confirmSettlement("signed-body", "signature"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Settlement event id was already used with different payment facts");
        verify(invoiceRepository, never()).findByIdForUpdate(any());
        verify(paymentRepository, never()).saveAndFlush(any());
    }

    @Test
    void fullySettledSubscriptionInvoiceActivatesPendingSubscription() {
        InvoiceEntity invoice = openInvoice();
        UUID subscriptionId = UUID.randomUUID();
        invoice.setEntityType(com.propertypilot.domain.enums.BillingEntityType.SUBSCRIPTION);
        invoice.setInvoiceType(com.propertypilot.domain.enums.InvoiceType.SUBSCRIPTION_FEE);
        invoice.setEntityId(subscriptionId);
        PaymentEntity payment = pendingPayment(invoice, paymentRequest(new BigDecimal("1000.00")));
        CustomerSubscriptionEntity subscription = new CustomerSubscriptionEntity();
        subscription.setCustomer(invoice.getCustomer());
        subscription.setStatus("PENDING");
        OffsetDateTime settledAt = OffsetDateTime.now();
        PaymentSettlementEvent event = new PaymentSettlementEvent(
                "subscription-event", "PROVIDER", "TX-1", invoice.getInvoiceId(), new BigDecimal("1000.00"),
                "INR", "SUCCEEDED", settledAt, null);
        when(settlementVerifier.verify("signed-body", "signature")).thenReturn(event);
        when(paymentRepository.findByProviderEventId(event.eventId())).thenReturn(Optional.empty());
        when(paymentRepository.findInvoiceIdByProviderAndProviderPaymentId("PROVIDER", "TX-1"))
                .thenReturn(Optional.of(invoice.getInvoiceId()));
        when(invoiceRepository.findByIdForUpdate(invoice.getInvoiceId())).thenReturn(Optional.of(invoice));
        when(paymentRepository.findByProviderAndProviderPaymentIdForUpdate("PROVIDER", "TX-1"))
                .thenReturn(Optional.of(payment));
        when(paymentRepository.sumVerifiedSettledAmountByInvoiceAndCurrency(invoice.getInvoiceId(), PaymentStatus.SUCCESS, "INR")).thenReturn(BigDecimal.ZERO);
        when(paymentRepository.saveAndFlush(payment)).thenReturn(payment);
        when(customerSubscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        service.confirmSettlement("signed-body", "signature");

        assertThat(subscription.getStatus()).isEqualTo("ACTIVE");
        verify(customerSubscriptionRepository).save(subscription);
    }

    @Test
    void paidSubscriptionRenewalInvoiceDoesNotActivatePendingPurchase() {
        InvoiceEntity invoice = openInvoice();
        UUID subscriptionId = UUID.randomUUID();
        invoice.setEntityType(com.propertypilot.domain.enums.BillingEntityType.SUBSCRIPTION);
        invoice.setInvoiceType(com.propertypilot.domain.enums.InvoiceType.SUBSCRIPTION_RENEWAL);
        invoice.setEntityId(subscriptionId);
        PaymentEntity payment = pendingPayment(invoice, paymentRequest(new BigDecimal("1000.00")));
        CustomerSubscriptionEntity subscription = new CustomerSubscriptionEntity();
        subscription.setCustomer(invoice.getCustomer());
        subscription.setStatus("PENDING");
        OffsetDateTime settledAt = OffsetDateTime.now();
        PaymentSettlementEvent event = new PaymentSettlementEvent(
                "subscription-renewal-event", "PROVIDER", "TX-1", invoice.getInvoiceId(), new BigDecimal("1000.00"),
                "INR", "SUCCEEDED", settledAt, null);
        when(settlementVerifier.verify("signed-body", "signature")).thenReturn(event);
        when(paymentRepository.findByProviderEventId(event.eventId())).thenReturn(Optional.empty());
        when(paymentRepository.findInvoiceIdByProviderAndProviderPaymentId("PROVIDER", "TX-1"))
                .thenReturn(Optional.of(invoice.getInvoiceId()));
        when(invoiceRepository.findByIdForUpdate(invoice.getInvoiceId())).thenReturn(Optional.of(invoice));
        when(paymentRepository.findByProviderAndProviderPaymentIdForUpdate("PROVIDER", "TX-1"))
                .thenReturn(Optional.of(payment));
        when(paymentRepository.sumVerifiedSettledAmountByInvoiceAndCurrency(invoice.getInvoiceId(), PaymentStatus.SUCCESS, "INR"))
                .thenReturn(BigDecimal.ZERO);
        when(paymentRepository.saveAndFlush(payment)).thenReturn(payment);
        service.confirmSettlement("signed-body", "signature");

        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.PAID);
        assertThat(subscription.getStatus()).isEqualTo("PENDING");
        verifyNoInteractions(customerSubscriptionRepository);
    }

    private static InvoiceEntity openInvoice() {
        CustomerEntity customer = new CustomerEntity();
        customer.setCustomerId(UUID.randomUUID());
        UserEntity user = new UserEntity();
        user.setFullName("Payment Test Customer");
        customer.setUser(user);
        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setInvoiceId(UUID.randomUUID());
        invoice.setInvoiceNumber("INV-TEST-001");
        invoice.setCustomer(customer);
        invoice.setStatus(InvoiceStatus.SENT);
        invoice.setInvoiceAmount(new BigDecimal("1000.00"));
        invoice.setTaxAmount(BigDecimal.ZERO);
        invoice.setCurrencyCode("INR");
        return invoice;
    }

    private static CreatePaymentRequest paymentRequest(BigDecimal amount) {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setAmount(amount);
        request.setPaymentMethod(PaymentMethod.UPI);
        request.setProvider("PROVIDER");
        request.setProviderPaymentId("TX-1");
        return request;
    }

    private static PaymentEntity pendingPayment(InvoiceEntity invoice, CreatePaymentRequest request) {
        PaymentEntity payment = new PaymentEntity();
        payment.setPaymentId(UUID.randomUUID());
        payment.setInvoice(invoice);
        payment.setCustomer(invoice.getCustomer());
        payment.setAmount(request.getAmount());
        payment.setCurrencyCode("INR");
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setProvider(request.getProvider());
        payment.setProviderPaymentId(request.getProviderPaymentId());
        payment.setPaymentDate(OffsetDateTime.now());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        return payment;
    }
}
