package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreatePaymentRequest;
import com.propertypilot.application.dto.PaymentResponse;
import com.propertypilot.application.service.PaymentService;
import com.propertypilot.domain.enums.InvoiceStatus;
import com.propertypilot.domain.enums.PaymentStatus;
import com.propertypilot.infrastructure.persistence.entity.InvoiceEntity;
import com.propertypilot.infrastructure.persistence.entity.PaymentEntity;
import com.propertypilot.infrastructure.persistence.repository.InvoiceRepository;
import com.propertypilot.infrastructure.persistence.repository.PaymentRepository;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;
import com.propertypilot.web.exception.BusinessException;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import com.propertypilot.security.SecurityService;
import com.propertypilot.security.PaymentSettlementVerifier;
import com.propertypilot.application.dto.PaymentSettlementEvent;
import org.springframework.dao.DataIntegrityViolationException;

@Service
@Transactional
public class PaymentServiceImpl
        implements PaymentService {

    private final SecurityService securityService;

    private final PaymentRepository
            paymentRepository;

    private final InvoiceRepository
            invoiceRepository;
    private final PaymentSettlementVerifier settlementVerifier;
    private final CustomerSubscriptionRepository customerSubscriptionRepository;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            InvoiceRepository invoiceRepository,
            SecurityService securityService,
            PaymentSettlementVerifier settlementVerifier,
            CustomerSubscriptionRepository customerSubscriptionRepository) {

        this.paymentRepository =
                paymentRepository;

        this.invoiceRepository =
                invoiceRepository;
        this.securityService = securityService;
        this.settlementVerifier = settlementVerifier;
        this.customerSubscriptionRepository = customerSubscriptionRepository;
    }

    @Override
    public PaymentResponse recordPayment(
            UUID invoiceId,
            CreatePaymentRequest request) {

        InvoiceEntity invoice =
                invoiceRepository
                        .findByIdForUpdate(invoiceId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Invoice not found"));
        securityService.validateInvoiceAccess(invoice);

        if (invoice.getStatus() != InvoiceStatus.SENT
                && invoice.getStatus() != InvoiceStatus.PARTIALLY_PAID
                && invoice.getStatus() != InvoiceStatus.OVERDUE) {
            throw new BusinessException("Invoice is not open for payment");
        }
        if (request.getProvider() == null || request.getProvider().isBlank()
                || request.getProviderPaymentId() == null || request.getProviderPaymentId().isBlank()) {
            throw new BusinessException("Provider and provider payment id are required");
        }
        if (paymentRepository.findByProviderAndProviderPaymentId(request.getProvider(), request.getProviderPaymentId()).isPresent()) {
            throw new BusinessException("Provider payment id has already been registered");
        }
        BigDecimal invoiceTotal = invoice.getInvoiceAmount().add(invoice.getTaxAmount());
        BigDecimal settled = paymentRepository.sumVerifiedSettledAmountByInvoiceAndCurrency(
                invoiceId, PaymentStatus.SUCCESS, invoice.getCurrencyCode());
        BigDecimal pending = paymentRepository.sumAmountByInvoiceAndStatus(invoiceId, PaymentStatus.PENDING);
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0
                || request.getAmount().add(settled).add(pending).compareTo(invoiceTotal) > 0) {
            throw new BusinessException("Payment amount exceeds the outstanding invoice balance");
        }

        PaymentEntity payment =
                new PaymentEntity();

        payment.setCustomer(
                invoice.getCustomer());

        payment.setInvoice(
                invoice);

        payment.setEntityType(
                invoice.getEntityType());

        payment.setEntityId(
                invoice.getEntityId());

        payment.setAmount(
                request.getAmount());

        payment.setCurrencyCode(
                invoice.getCurrencyCode());

        payment.setPaymentMethod(
                request.getPaymentMethod());

        payment.setProvider(
                request.getProvider());

        payment.setProviderPaymentId(
                request.getProviderPaymentId());

        payment.setPaymentDate(
                OffsetDateTime.now());

        payment.setPaymentStatus(PaymentStatus.PENDING);

        payment.setIdempotencyKey(
                UUID.randomUUID()
                        .toString());

        PaymentEntity saved;
        try {
            saved = paymentRepository.saveAndFlush(payment);
        } catch (org.springframework.dao.DataIntegrityViolationException exception) {
            throw new BusinessException("Provider payment id has already been registered");
        }

        return mapPayment(
                saved);
    }

    @Override
    public PaymentResponse confirmSettlement(String rawBody, String signature) {
        PaymentSettlementEvent event = settlementVerifier.verify(rawBody, signature);
        if (event.eventId() == null || event.eventId().isBlank()
                || event.provider() == null || event.provider().isBlank()
                || event.providerPaymentId() == null || event.providerPaymentId().isBlank()
                || event.invoiceId() == null || event.amount() == null || event.currencyCode() == null
                || event.settledAt() == null || event.status() == null) {
            throw new BusinessException("Settlement event is missing required fields");
        }
        var priorEvent = paymentRepository.findByProviderEventId(event.eventId());
        if (priorEvent.isPresent()) {
            PaymentEntity priorPayment = priorEvent.get();
            if (!matchesSettlementEvent(priorPayment, event)) {
                throw new BusinessException("Settlement event id was already used with different payment facts");
            }
            return mapPayment(priorPayment);
        }
        UUID invoiceId = paymentRepository
                .findInvoiceIdByProviderAndProviderPaymentId(event.provider(), event.providerPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Pending payment not found"));
        InvoiceEntity invoice = invoiceRepository.findByIdForUpdate(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
        PaymentEntity payment = paymentRepository
                .findByProviderAndProviderPaymentIdForUpdate(event.provider(), event.providerPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Pending payment not found"));
        if (event.eventId().equals(payment.getProviderEventId())) {
            return mapPayment(payment);
        }
        if (!event.invoiceId().equals(invoice.getInvoiceId())
                || !payment.getInvoice().getInvoiceId().equals(invoice.getInvoiceId())
                || payment.getAmount().compareTo(event.amount()) != 0
                || !payment.getCurrencyCode().trim().equalsIgnoreCase(event.currencyCode().trim())) {
            throw new BusinessException("Settlement facts do not match the registered payment");
        }
        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new BusinessException("Only pending payments can be settled");
        }
        payment.setProviderEventId(event.eventId());
        payment.setPaymentDate(event.settledAt());
        if ("SUCCEEDED".equalsIgnoreCase(event.status()) || "SUCCESS".equalsIgnoreCase(event.status())) {
            BigDecimal total = invoice.getInvoiceAmount().add(invoice.getTaxAmount());
            BigDecimal priorSettled = paymentRepository.sumVerifiedSettledAmountByInvoiceAndCurrency(
                    invoice.getInvoiceId(), PaymentStatus.SUCCESS, invoice.getCurrencyCode());
            if (priorSettled.add(payment.getAmount()).compareTo(total) > 0) {
                throw new BusinessException("Settlement exceeds the invoice balance");
            }
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setSettledAt(event.settledAt());
            BigDecimal newSettled = priorSettled.add(payment.getAmount());
            if (newSettled.compareTo(total) >= 0) {
                invoice.setStatus(InvoiceStatus.PAID);
                invoice.setPaidAt(event.settledAt());
            } else if (invoice.getDueAt() != null && invoice.getDueAt().isBefore(OffsetDateTime.now())) {
                invoice.setStatus(InvoiceStatus.OVERDUE);
            } else {
                invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
            }
        } else if ("FAILED".equalsIgnoreCase(event.status())) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setFailureReason(event.failureReason());
        } else {
            throw new BusinessException("Unsupported settlement status");
        }
        try {
            paymentRepository.saveAndFlush(payment);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException("Settlement event was already processed");
        }
        invoiceRepository.save(invoice);
            if (invoice.getStatus() == InvoiceStatus.PAID
                    && invoice.getEntityType() == com.propertypilot.domain.enums.BillingEntityType.SUBSCRIPTION
                    && invoice.getInvoiceType() == com.propertypilot.domain.enums.InvoiceType.SUBSCRIPTION_FEE) {
                customerSubscriptionRepository.findById(invoice.getEntityId()).ifPresent(subscription -> {
                    if ("PENDING".equalsIgnoreCase(subscription.getStatus())
                            && subscription.getCustomer().getCustomerId().equals(invoice.getCustomer().getCustomerId())) {
                        subscription.setStatus("ACTIVE");
                        customerSubscriptionRepository.save(subscription);
                    }
                });
            }
        return mapPayment(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(
            UUID paymentId) {

        PaymentEntity payment =
                paymentRepository
                        .findById(paymentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payment not found"));
        securityService.validateInvoiceAccess(payment.getInvoice());

        return mapPayment(
                payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        securityService.validateAdminAccess();
        return paymentRepository.findAllWithDisplayDetails().stream().map(this::mapPayment).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse>
    getPaymentsByInvoice(
            UUID invoiceId) {

        securityService.validateInvoiceAccess(invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found")));

        return paymentRepository
                .findByInvoice_InvoiceId(
                        invoiceId)
                .stream()
                .map(this::mapPayment)
                .toList();
    }

    private PaymentResponse mapPayment(
            PaymentEntity entity) {

        PaymentResponse response =
                new PaymentResponse();

        response.setPaymentId(
                entity.getPaymentId());

        response.setInvoiceId(
                entity.getInvoice()
                        .getInvoiceId());

        response.setCustomerId(
                entity.getCustomer()
                        .getCustomerId());

        response.setCustomerName(entity.getCustomer().getUser() == null
                ? null : entity.getCustomer().getUser().getFullName());
        response.setInvoiceNumber(entity.getInvoice() == null ? null : entity.getInvoice().getInvoiceNumber());

        response.setAmount(
                entity.getAmount());

        response.setCurrencyCode(entity.getCurrencyCode());

        response.setPaymentMethod(
                entity.getPaymentMethod());

        response.setPaymentStatus(
                entity.getPaymentStatus());

        response.setProvider(
                entity.getProvider());

        response.setProviderPaymentId(
                entity.getProviderPaymentId());

        response.setPaymentDate(
                entity.getPaymentDate());
        response.setSettledAt(entity.getSettledAt());

        return response;
    }

    private boolean matchesSettlementEvent(PaymentEntity payment, PaymentSettlementEvent event) {
        PaymentStatus expectedStatus;
        if ("SUCCEEDED".equalsIgnoreCase(event.status()) || "SUCCESS".equalsIgnoreCase(event.status())) {
            expectedStatus = PaymentStatus.SUCCESS;
        } else if ("FAILED".equalsIgnoreCase(event.status())) {
            expectedStatus = PaymentStatus.FAILED;
        } else {
            return false;
        }
        return payment.getProvider() != null
                && payment.getProvider().trim().equalsIgnoreCase(event.provider().trim())
                && payment.getProviderPaymentId() != null
                && payment.getProviderPaymentId().equals(event.providerPaymentId())
                && payment.getInvoice() != null
                && payment.getInvoice().getInvoiceId().equals(event.invoiceId())
                && payment.getAmount() != null
                && payment.getAmount().compareTo(event.amount()) == 0
                && payment.getCurrencyCode() != null
                && payment.getCurrencyCode().trim().equalsIgnoreCase(event.currencyCode().trim())
                && expectedStatus == payment.getPaymentStatus()
                && event.settledAt().equals(payment.getPaymentDate())
                && java.util.Objects.equals(payment.getFailureReason(), event.failureReason());
    }
}
