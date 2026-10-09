package com.propertypilot.application.service;

import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.InvoiceStatus;
import com.propertypilot.domain.enums.InvoiceType;
import com.propertypilot.domain.enums.PaymentStatus;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import com.propertypilot.infrastructure.persistence.entity.InvoiceEntity;
import com.propertypilot.infrastructure.persistence.entity.PaymentEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanVersionEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionRenewalEntity;
import com.propertypilot.infrastructure.persistence.repository.InvoiceRepository;
import com.propertypilot.infrastructure.persistence.repository.PaymentRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionRenewalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionPaymentVerificationServiceTest {
    @Mock private InvoiceRepository invoiceRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private SubscriptionRenewalRepository renewalRepository;

    private SubscriptionPaymentVerificationService service;
    private CustomerEntity customer;
    private CustomerSubscriptionEntity subscription;
    private UUID subscriptionId;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        service = new SubscriptionPaymentVerificationService(
                invoiceRepository, paymentRepository, renewalRepository);
        customerId = UUID.randomUUID();
        customer = new CustomerEntity();
        customer.setCustomerId(customerId);
        subscriptionId = UUID.randomUUID();
        SubscriptionPlanVersionEntity plan = new SubscriptionPlanVersionEntity();
        plan.setPrice(new BigDecimal("1499.00"));
        subscription = new CustomerSubscriptionEntity();
        subscription.setCustomerSubscriptionId(subscriptionId);
        subscription.setCustomer(customer);
        subscription.setPlanVersion(plan);
        subscription.setStartDate(LocalDate.of(2026, 10, 1));
        subscription.setEndDate(LocalDate.of(2027, 9, 30));
    }

    @Test
    void freeSubscriptionRequiresNoPayment() {
        subscription.getPlanVersion().setPrice(BigDecimal.ZERO);

        assertTrue(service.isPaymentVerified(subscription));
        verifyNoInteractions(invoiceRepository, paymentRepository, renewalRepository);
    }

    @Test
    void purchaseRequiresFullVerifiedSettlementInMatchingCurrency() {
        InvoiceEntity invoice = purchaseInvoice(new BigDecimal("1499.00"), new BigDecimal("100.00"));
        when(invoiceRepository.findByEntityTypeAndEntityIdAndInvoiceType(
                BillingEntityType.SUBSCRIPTION, subscriptionId, InvoiceType.SUBSCRIPTION_FEE))
                .thenReturn(List.of(invoice));
        when(paymentRepository.sumVerifiedSettledAmountByInvoiceAndCurrency(
                invoice.getInvoiceId(), PaymentStatus.SUCCESS, "INR"))
                .thenReturn(new BigDecimal("1599.00"));

        assertTrue(service.isPaymentVerified(subscription));
        verifyNoInteractions(renewalRepository);
    }

    @Test
    void paidInvoiceWithoutVerifiedSettlementDoesNotGrantBenefits() {
        InvoiceEntity invoice = purchaseInvoice(new BigDecimal("1499.00"), BigDecimal.ZERO);
        when(invoiceRepository.findByEntityTypeAndEntityIdAndInvoiceType(
                BillingEntityType.SUBSCRIPTION, subscriptionId, InvoiceType.SUBSCRIPTION_FEE))
                .thenReturn(List.of(invoice));
        when(paymentRepository.sumVerifiedSettledAmountByInvoiceAndCurrency(
                invoice.getInvoiceId(), PaymentStatus.SUCCESS, "INR"))
                .thenReturn(BigDecimal.ZERO);
        when(renewalRepository.findByNewCustomerSubscription_CustomerSubscriptionId(subscriptionId))
                .thenReturn(List.of());

        assertFalse(service.isPaymentVerified(subscription));
    }

    @Test
    void renewalBenefitsRequireMatchingVerifiedRenewalInvoiceAndPayment() {
        CustomerSubscriptionEntity original = new CustomerSubscriptionEntity();
        original.setCustomerSubscriptionId(UUID.randomUUID());
        original.setCustomer(customer);

        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setInvoiceId(UUID.randomUUID());
        invoice.setCustomer(customer);
        invoice.setEntityType(BillingEntityType.SUBSCRIPTION);
        invoice.setEntityId(original.getCustomerSubscriptionId());
        invoice.setInvoiceType(InvoiceType.SUBSCRIPTION_RENEWAL);
        invoice.setInvoiceAmount(new BigDecimal("1499.00"));
        invoice.setTaxAmount(BigDecimal.ZERO);
        invoice.setCurrencyCode("INR");
        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setRenewalPeriodStart(subscription.getStartDate());
        invoice.setRenewalPeriodEnd(subscription.getEndDate());

        UUID paymentId = UUID.randomUUID();
        PaymentEntity payment = new PaymentEntity();
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setSettledAt(OffsetDateTime.now());
        payment.setInvoice(invoice);
        payment.setAmount(new BigDecimal("1499.00"));
        payment.setCurrencyCode("INR");

        SubscriptionRenewalEntity renewal = new SubscriptionRenewalEntity();
        renewal.setStatus("SUCCESS");
        renewal.setPaymentId(paymentId);
        renewal.setCustomerSubscription(original);
        renewal.setNewCustomerSubscription(subscription);
        renewal.setPeriodStart(subscription.getStartDate());
        renewal.setPeriodEnd(subscription.getEndDate());
        renewal.setRenewalAmount(new BigDecimal("1499.00"));

        when(invoiceRepository.findByEntityTypeAndEntityIdAndInvoiceType(
                BillingEntityType.SUBSCRIPTION, subscriptionId, InvoiceType.SUBSCRIPTION_FEE))
                .thenReturn(List.of());
        when(renewalRepository.findByNewCustomerSubscription_CustomerSubscriptionId(subscriptionId))
                .thenReturn(List.of(renewal));
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        assertTrue(service.isPaymentVerified(subscription));
    }

    private InvoiceEntity purchaseInvoice(BigDecimal amount, BigDecimal tax) {
        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setInvoiceId(UUID.randomUUID());
        invoice.setCustomer(customer);
        invoice.setEntityType(BillingEntityType.SUBSCRIPTION);
        invoice.setEntityId(subscriptionId);
        invoice.setInvoiceType(InvoiceType.SUBSCRIPTION_FEE);
        invoice.setInvoiceAmount(amount);
        invoice.setTaxAmount(tax);
        invoice.setCurrencyCode("INR");
        invoice.setStatus(InvoiceStatus.PAID);
        return invoice;
    }
}
