package com.propertypilot.application.service;

import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.InvoiceStatus;
import com.propertypilot.domain.enums.InvoiceType;
import com.propertypilot.domain.enums.PaymentStatus;
import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import com.propertypilot.infrastructure.persistence.entity.InvoiceEntity;
import com.propertypilot.infrastructure.persistence.entity.PaymentEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionRenewalEntity;
import com.propertypilot.infrastructure.persistence.repository.InvoiceRepository;
import com.propertypilot.infrastructure.persistence.repository.PaymentRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionRenewalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Confirms paid subscription terms against verified settlement records before
 * allowing the subscription to grant pricing discounts or service entitlements.
 */
@Service
@Transactional(readOnly = true)
public class SubscriptionPaymentVerificationService {
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final SubscriptionRenewalRepository renewalRepository;

    public SubscriptionPaymentVerificationService(
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository,
            SubscriptionRenewalRepository renewalRepository) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.renewalRepository = renewalRepository;
    }

    public boolean isPaymentVerified(CustomerSubscriptionEntity subscription) {
        if (subscription == null || subscription.getPlanVersion() == null) return false;
        BigDecimal planPrice = subscription.getPlanVersion().getPrice();
        if (planPrice == null || planPrice.signum() <= 0) return true;
        if (subscription.getCustomer() == null
                || subscription.getCustomer().getCustomerId() == null
                || subscription.getStartDate() == null
                || subscription.getEndDate() == null) return false;

        UUID subscriptionId = subscription.getCustomerSubscriptionId();
        UUID customerId = subscription.getCustomer().getCustomerId();
        List<InvoiceEntity> purchaseInvoices = invoiceRepository
                .findByEntityTypeAndEntityIdAndInvoiceType(
                        BillingEntityType.SUBSCRIPTION, subscriptionId, InvoiceType.SUBSCRIPTION_FEE);
        for (InvoiceEntity invoice : purchaseInvoices) {
            if (isPaidInvoiceForSubscription(invoice, subscriptionId, customerId, InvoiceType.SUBSCRIPTION_FEE)
                    && hasVerifiedFullPayment(invoice)) return true;
        }

        List<SubscriptionRenewalEntity> renewals = renewalRepository
                .findByNewCustomerSubscription_CustomerSubscriptionId(subscriptionId);
        for (SubscriptionRenewalEntity renewal : renewals) {
            if (!"SUCCESS".equalsIgnoreCase(renewal.getStatus())
                    || renewal.getPaymentId() == null
                    || !subscription.getStartDate().equals(renewal.getPeriodStart())
                    || !subscription.getEndDate().equals(renewal.getPeriodEnd())
                    || renewal.getCustomerSubscription() == null
                    || renewal.getCustomerSubscription().getCustomer() == null
                    || !customerId.equals(renewal.getCustomerSubscription().getCustomer().getCustomerId())) {
                continue;
            }

            PaymentEntity payment = paymentRepository.findById(renewal.getPaymentId()).orElse(null);
            if (payment == null || payment.getPaymentStatus() != PaymentStatus.SUCCESS
                    || payment.getSettledAt() == null || payment.getInvoice() == null) continue;

            InvoiceEntity invoice = payment.getInvoice();
            BigDecimal invoiceTotal = invoiceTotal(invoice);
            if (isPaidInvoiceForSubscription(invoice,
                    renewal.getCustomerSubscription().getCustomerSubscriptionId(),
                    customerId, InvoiceType.SUBSCRIPTION_RENEWAL)
                    && subscription.getStartDate().equals(invoice.getRenewalPeriodStart())
                    && subscription.getEndDate().equals(invoice.getRenewalPeriodEnd())
                    && sameCurrency(payment.getCurrencyCode(), invoice.getCurrencyCode())
                    && payment.getAmount() != null
                    && payment.getAmount().compareTo(invoiceTotal) == 0
                    && renewal.getRenewalAmount() != null
                    && renewal.getRenewalAmount().compareTo(invoiceTotal) == 0) return true;
        }
        return false;
    }

    private boolean isPaidInvoiceForSubscription(
            InvoiceEntity invoice,
            UUID subscriptionId,
            UUID customerId,
            InvoiceType invoiceType) {
        return invoice.getStatus() == InvoiceStatus.PAID
                && invoice.getEntityType() == BillingEntityType.SUBSCRIPTION
                && subscriptionId.equals(invoice.getEntityId())
                && invoice.getInvoiceType() == invoiceType
                && invoice.getCustomer() != null
                && customerId.equals(invoice.getCustomer().getCustomerId());
    }

    private boolean hasVerifiedFullPayment(InvoiceEntity invoice) {
        if (invoice.getCurrencyCode() == null || invoice.getCurrencyCode().isBlank()) return false;
        BigDecimal verifiedAmount = paymentRepository.sumVerifiedSettledAmountByInvoiceAndCurrency(
                invoice.getInvoiceId(), PaymentStatus.SUCCESS, invoice.getCurrencyCode());
        return verifiedAmount != null && verifiedAmount.compareTo(invoiceTotal(invoice)) >= 0;
    }

    private BigDecimal invoiceTotal(InvoiceEntity invoice) {
        BigDecimal amount = invoice.getInvoiceAmount() == null ? BigDecimal.ZERO : invoice.getInvoiceAmount();
        BigDecimal tax = invoice.getTaxAmount() == null ? BigDecimal.ZERO : invoice.getTaxAmount();
        return amount.add(tax);
    }

    private boolean sameCurrency(String left, String right) {
        return left != null && right != null && left.trim().equalsIgnoreCase(right.trim());
    }
}
