package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateSubscriptionRenewalRequest;
import com.propertypilot.application.dto.SubscriptionRenewalResponse;
import com.propertypilot.application.service.SubscriptionRenewalService;
import com.propertypilot.application.service.BillingService;
import com.propertypilot.application.dto.InvoiceResponse;
import com.propertypilot.application.dto.SubscriptionRenewalInvoiceRequest;
import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanVersionEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionRenewalEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionPlanVersionRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionRenewalRepository;
import com.propertypilot.infrastructure.persistence.repository.PaymentRepository;
import com.propertypilot.domain.enums.PaymentStatus;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import com.propertypilot.security.SecurityService;

@Service
@Transactional
public class SubscriptionRenewalServiceImpl
        implements SubscriptionRenewalService {

    private final SubscriptionRenewalRepository
            renewalRepository;

    private final CustomerSubscriptionRepository
            customerSubscriptionRepository;

    private final SubscriptionPlanVersionRepository
            planVersionRepository;
    private final SecurityService securityService;
    private final PaymentRepository paymentRepository;
    private final BillingService billingService;

    public SubscriptionRenewalServiceImpl(
            SubscriptionRenewalRepository renewalRepository,
            CustomerSubscriptionRepository customerSubscriptionRepository,
            SubscriptionPlanVersionRepository planVersionRepository,
            SecurityService securityService,
            PaymentRepository paymentRepository,
            BillingService billingService) {

        this.renewalRepository =
                renewalRepository;

        this.customerSubscriptionRepository =
                customerSubscriptionRepository;

        this.planVersionRepository =
                planVersionRepository;
        this.securityService = securityService;
        this.paymentRepository = paymentRepository;
        this.billingService = billingService;
    }

    @Override
    public InvoiceResponse createRenewalInvoice(SubscriptionRenewalInvoiceRequest request) {
        return billingService.createSubscriptionRenewalInvoice(
                request.customerSubscriptionId(), request.periodStart(), request.periodEnd());
    }

    @Override
    public SubscriptionRenewalResponse renewSubscription(
            CreateSubscriptionRenewalRequest request) {

        CustomerSubscriptionEntity
                existingSubscription =
                customerSubscriptionRepository
                        .findById(
                                request.getCustomerSubscriptionId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer Subscription not found"));
        securityService.validateCustomerAccess(existingSubscription.getCustomer());
        if (request.getPaymentId() == null || request.getPeriodStart() == null
                || request.getPeriodEnd() == null
                || !request.getPeriodEnd().isAfter(request.getPeriodStart())) {
            throw new com.propertypilot.web.exception.BusinessException(
                    "A confirmed payment and a valid renewal period are required");
        }
        if (!"ACTIVE".equals(existingSubscription.getStatus())
                && !"EXPIRED".equals(existingSubscription.getStatus())) {
            throw new com.propertypilot.web.exception.BusinessException(
                    "Only active or expired subscriptions can be renewed");
        }

        SubscriptionPlanVersionEntity
                latestPlanVersion =
                planVersionRepository
                        .findTopBySubscriptionPlan_SubscriptionPlanIdOrderByVersionNumberDesc(
                                existingSubscription
                                        .getPlanVersion()
                                        .getSubscriptionPlan()
                                        .getSubscriptionPlanId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Latest Plan Version not found"));

        var payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Confirmed renewal payment not found"));
        var renewalInvoice = payment.getInvoice();
        var invoiceTax = renewalInvoice == null || renewalInvoice.getTaxAmount() == null
                ? java.math.BigDecimal.ZERO : renewalInvoice.getTaxAmount();
        var invoiceAmount = renewalInvoice == null || renewalInvoice.getInvoiceAmount() == null
                ? java.math.BigDecimal.ZERO : renewalInvoice.getInvoiceAmount().add(invoiceTax);
        String paymentCurrency = payment.getCurrencyCode() == null ? "" : payment.getCurrencyCode().trim();
        String invoiceCurrency = renewalInvoice == null || renewalInvoice.getCurrencyCode() == null
                ? "" : renewalInvoice.getCurrencyCode().trim();
        if (payment.getPaymentStatus() != PaymentStatus.SUCCESS || payment.getSettledAt() == null
                || renewalInvoice == null
                || renewalInvoice.getInvoiceType() != com.propertypilot.domain.enums.InvoiceType.SUBSCRIPTION_RENEWAL
                || renewalInvoice.getEntityType() != com.propertypilot.domain.enums.BillingEntityType.SUBSCRIPTION
                || !existingSubscription.getCustomerSubscriptionId().equals(renewalInvoice.getEntityId())
                || renewalInvoice.getStatus() != com.propertypilot.domain.enums.InvoiceStatus.PAID
                || renewalInvoice.getCustomer() == null
                || !existingSubscription.getCustomer().getCustomerId().equals(
                        renewalInvoice.getCustomer().getCustomerId())
                || !request.getPeriodStart().equals(renewalInvoice.getRenewalPeriodStart())
                || !request.getPeriodEnd().equals(renewalInvoice.getRenewalPeriodEnd())
                || payment.getAmount() == null
                || payment.getAmount().compareTo(invoiceAmount) != 0
                || !paymentCurrency.equalsIgnoreCase(invoiceCurrency)) {
            throw new com.propertypilot.web.exception.BusinessException(
                    "Renewal requires verified settlement of the invoice issued for this renewal period");
        }
        if (renewalRepository.existsByPaymentId(request.getPaymentId())) {
            throw new com.propertypilot.web.exception.BusinessException("Payment was already applied to a renewal");
        }
        if (request.getRenewalAmount() == null || payment.getAmount() == null
                || payment.getAmount().compareTo(request.getRenewalAmount()) != 0
                || request.getRenewalAmount().compareTo(invoiceAmount) != 0) {
            throw new com.propertypilot.web.exception.BusinessException("Renewal amount does not match the paid renewal invoice");
        }

        CustomerSubscriptionEntity
                newSubscription =
                new CustomerSubscriptionEntity();

        newSubscription.setCustomer(
                existingSubscription.getCustomer());

        newSubscription.setPlanVersion(
                latestPlanVersion);

        newSubscription.setStatus(
                "ACTIVE");

        newSubscription.setStartDate(
                request.getPeriodStart());

        newSubscription.setEndDate(
                request.getPeriodEnd());
        if (existingSubscription.getEndDate() != null
                && !request.getPeriodStart().equals(existingSubscription.getEndDate().plusDays(1))) {
            throw new com.propertypilot.web.exception.BusinessException("Renewal period must begin after the current subscription ends");
        }

        newSubscription.setAutoRenew(
                existingSubscription.getAutoRenew());

        newSubscription =
                customerSubscriptionRepository
                        .save(newSubscription);

        SubscriptionRenewalEntity renewal =
                new SubscriptionRenewalEntity();

        renewal.setCustomerSubscription(
                existingSubscription);

        renewal.setNewCustomerSubscription(
                newSubscription);

        renewal.setPaymentId(
                request.getPaymentId());

        renewal.setRenewalDate(
                LocalDate.now());

        renewal.setPeriodStart(
                request.getPeriodStart());

        renewal.setPeriodEnd(
                request.getPeriodEnd());

        renewal.setRenewalType(
                request.getRenewalType());

        renewal.setRenewalAmount(
                request.getRenewalAmount());

        renewal.setRemarks(
                request.getRemarks());

        renewal.setStatus(
                "SUCCESS");

        try {
            renewal = renewalRepository.saveAndFlush(renewal);
        } catch (DataIntegrityViolationException exception) {
            if (hasPaymentReuseConstraint(exception)) {
                throw new com.propertypilot.web.exception.BusinessException(
                        "Payment was already applied to a renewal");
            }
            throw exception;
        }

        existingSubscription.setStatus(
                "EXPIRED");

        customerSubscriptionRepository
                .save(existingSubscription);

        return mapToResponse(
                renewal);
    }

    private boolean hasPaymentReuseConstraint(DataIntegrityViolationException exception) {
        Throwable cause = exception;
        while (cause != null) {
            if (cause instanceof org.hibernate.exception.ConstraintViolationException violation
                    && "uq_subscription_renewals_payment_id".equals(violation.getConstraintName())) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionRenewalResponse getRenewal(
            UUID subscriptionRenewalId) {

        SubscriptionRenewalEntity renewal =
                renewalRepository.findById(
                                subscriptionRenewalId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Subscription Renewal not found"));
        securityService.validateCustomerAccess(renewal.getCustomerSubscription().getCustomer());

        return mapToResponse(
                renewal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionRenewalResponse>
    getRenewalsBySubscription(
            UUID customerSubscriptionId) {

        CustomerSubscriptionEntity subscription = customerSubscriptionRepository.findById(customerSubscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer subscription not found"));
        securityService.validateCustomerAccess(subscription.getCustomer());

        return renewalRepository
                .findByCustomerSubscription_CustomerSubscriptionId(
                        customerSubscriptionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private SubscriptionRenewalResponse
    mapToResponse(
            SubscriptionRenewalEntity entity) {

        SubscriptionRenewalResponse response =
                new SubscriptionRenewalResponse();

        response.setSubscriptionRenewalId(
                entity.getSubscriptionRenewalId());

        response.setCustomerSubscriptionId(
                entity.getCustomerSubscription()
                        .getCustomerSubscriptionId());

        if (entity.getNewCustomerSubscription()
                != null) {

            response.setNewCustomerSubscriptionId(
                    entity.getNewCustomerSubscription()
                            .getCustomerSubscriptionId());
        }

        response.setPaymentId(
                entity.getPaymentId());

        response.setRenewalDate(
                entity.getRenewalDate());

        response.setPeriodStart(
                entity.getPeriodStart());

        response.setPeriodEnd(
                entity.getPeriodEnd());

        response.setRenewalType(
                entity.getRenewalType());

        response.setRenewalAmount(
                entity.getRenewalAmount());

        response.setRemarks(
                entity.getRemarks());

        response.setStatus(
                entity.getStatus());

        return response;
    }
}
