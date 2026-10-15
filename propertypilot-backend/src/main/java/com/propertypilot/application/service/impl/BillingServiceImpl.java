package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.InvoiceResponse;
import com.propertypilot.application.service.BillingService;
import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.InvoiceStatus;
import com.propertypilot.domain.enums.InvoiceType;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.InvoiceEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerRepository;
import com.propertypilot.infrastructure.persistence.repository.InvoiceRepository;
import com.propertypilot.infrastructure.persistence.repository.DealRepository;
import com.propertypilot.infrastructure.persistence.repository.PaymentRepository;
import com.propertypilot.infrastructure.persistence.entity.DealEntity;
import com.propertypilot.infrastructure.persistence.entity.PricingEstimateEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionPlanVersionRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import com.propertypilot.security.SecurityService;

@Service
@Transactional
public class BillingServiceImpl
        implements BillingService {

    private final SecurityService securityService;

    private final InvoiceRepository invoiceRepository;

    private final CustomerRepository customerRepository;
    private final DealRepository dealRepository;
    private final PaymentRepository settledPaymentLookup;
    private final CustomerSubscriptionRepository customerSubscriptionRepository;
    private final com.propertypilot.infrastructure.persistence.repository.SubscriptionPlanVersionRepository planVersionRepository;

    public BillingServiceImpl(
            SecurityService securityService,
            InvoiceRepository invoiceRepository,
            CustomerRepository customerRepository,
            DealRepository dealRepository,
            PaymentRepository settledPaymentLookup,
            CustomerSubscriptionRepository customerSubscriptionRepository,
            SubscriptionPlanVersionRepository planVersionRepository) {
        this.securityService = securityService;
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
        this.dealRepository = dealRepository;
        this.settledPaymentLookup = settledPaymentLookup;
        this.customerSubscriptionRepository = customerSubscriptionRepository;
        this.planVersionRepository = planVersionRepository;
    }

   @Override
public InvoiceResponse  createInvoice(
        BillingEntityType entityType,
        UUID entityId,
        UUID customerId,
        InvoiceType invoiceType,
        BigDecimal amount,
        String description) {

        return createInvoice(entityType, entityId, customerId, invoiceType, amount, description, "INR");
    }

    @Override
    public InvoiceResponse createInvoice(
            BillingEntityType entityType,
            UUID entityId,
            UUID customerId,
            InvoiceType invoiceType,
            BigDecimal amount,
            String description,
            String currencyCode) {

        CustomerEntity customer =
                customerRepository
                        .findById(customerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found"));

        InvoiceEntity invoice =
                new InvoiceEntity();

        invoice.setCustomer(customer);
        DealEntity deal = entityType == BillingEntityType.DEAL
                ? dealRepository.findById(entityId).orElseThrow(() -> new ResourceNotFoundException("Deal not found"))
                : null;
        invoice.setDeal(deal);
        invoice.setEntityType(entityType);
        invoice.setEntityId(entityId);

invoice.setInvoiceType(
        invoiceType);

        invoice.setInvoiceAmount(
                amount);

        invoice.setTaxAmount(
                BigDecimal.ZERO);

    invoice.setStatus(InvoiceStatus.DRAFT);

        invoice.setDescription(
                description);

        invoice.setCurrencyCode(normalizeCurrencyCode(currencyCode));

        invoice.setInvoiceNumber(
                generateInvoiceNumber());

        InvoiceEntity saved =
                invoiceRepository.save(
                        invoice);

      return mapInvoice(saved);
    }

    private String normalizeCurrencyCode(String currencyCode) {
        String normalized = currencyCode == null || currencyCode.isBlank()
                ? "INR" : currencyCode.trim().toUpperCase(java.util.Locale.ROOT);
        try {
            java.util.Currency.getInstance(normalized);
        } catch (IllegalArgumentException exception) {
            throw new com.propertypilot.web.exception.BusinessException("Invoice currency must be a valid ISO 4217 code");
        }
        return normalized;
    }

    @Override
    public InvoiceResponse createInvoiceFromCompletedServiceRequest(ServiceRequestEntity request) {
        if (request == null || request.getServiceRequestId() == null
                || request.getCustomer() == null || request.getPricingEstimate() == null
                || request.getService() == null || !"COMPLETED".equals(request.getStatus())) {
            throw new com.propertypilot.web.exception.BusinessException(
                    "A completed, priced service request is required for service billing");
        }
        if (!Boolean.TRUE.equals(request.getService().getBillable())
                || request.getAmount() == null || request.getAmount().signum() <= 0) {
            throw new com.propertypilot.web.exception.BusinessException("This service request is not billable");
        }
        if (invoiceRepository.existsByEntityTypeAndEntityId(
                BillingEntityType.SERVICE_REQUEST, request.getServiceRequestId())) {
            return invoiceRepository.findByEntityTypeAndEntityId(
                            BillingEntityType.SERVICE_REQUEST, request.getServiceRequestId())
                    .stream().findFirst().map(this::mapInvoice)
                    .orElseThrow(() -> new IllegalStateException("Service invoice existence check was inconsistent"));
        }

        PricingEstimateEntity estimate = request.getPricingEstimate();
        BigDecimal total = estimate.getTotalAmount();
        BigDecimal tax = estimate.getTaxAmount() == null ? BigDecimal.ZERO : estimate.getTaxAmount();
        if (total == null || total.signum() <= 0 || tax.signum() < 0 || tax.compareTo(total) > 0) {
            throw new com.propertypilot.web.exception.BusinessException("Accepted quote totals are invalid for billing");
        }

        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setCustomer(request.getCustomer());
        invoice.setDeal(null);
        invoice.setEntityType(BillingEntityType.SERVICE_REQUEST);
        invoice.setEntityId(request.getServiceRequestId());
        invoice.setInvoiceType(InvoiceType.SERVICE_CHARGE);
        invoice.setInvoiceAmount(total.subtract(tax));
        invoice.setTaxAmount(tax);
        invoice.setCurrencyCode(estimate.getCurrencyCode());
        invoice.setStatus(InvoiceStatus.DRAFT);
        invoice.setDescription(request.getService().getServiceName() + " service");
        invoice.setInvoiceNumber(generateInvoiceNumber());
        return mapInvoice(invoiceRepository.save(invoice));
    }

    @Override
    public InvoiceResponse createSubscriptionRenewalInvoice(
            UUID customerSubscriptionId, LocalDate periodStart, LocalDate periodEnd) {
        if (periodStart == null || periodEnd == null || periodEnd.isBefore(periodStart)) {
            throw new com.propertypilot.web.exception.BusinessException("A valid subscription renewal period is required");
        }
        CustomerSubscriptionEntity subscription = customerSubscriptionRepository
                .findByIdForUpdate(customerSubscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer subscription not found"));
        securityService.validateCustomerAccess(subscription.getCustomer());
        if (!"ACTIVE".equalsIgnoreCase(subscription.getStatus())
                && !"EXPIRED".equalsIgnoreCase(subscription.getStatus())) {
            throw new com.propertypilot.web.exception.BusinessException("Only active or expired subscriptions can be renewed");
        }
        if (subscription.getEndDate() == null
                || !periodStart.equals(subscription.getEndDate().plusDays(1))) {
            throw new com.propertypilot.web.exception.BusinessException("Renewal period must begin after the current subscription ends");
        }
        if (subscription.getStartDate() == null || periodEnd.isBefore(periodStart)) {
            throw new com.propertypilot.web.exception.BusinessException("A valid renewal period is required");
        }
        long currentTermDays = ChronoUnit.DAYS.between(subscription.getStartDate(), subscription.getEndDate()) + 1;
        long renewalTermDays = ChronoUnit.DAYS.between(periodStart, periodEnd) + 1;
        if (currentTermDays <= 0 || renewalTermDays != currentTermDays) {
            throw new com.propertypilot.web.exception.BusinessException(
                    "Renewal period must have the same duration as the current subscription term");
        }

        UUID planId = subscription.getPlanVersion().getSubscriptionPlan().getSubscriptionPlanId();
        var planVersion = planVersionRepository
                .findTopBySubscriptionPlan_SubscriptionPlanIdOrderByVersionNumberDesc(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Latest plan version not found"));
        if (planVersion.getPrice() == null || planVersion.getPrice().signum() <= 0) {
            throw new com.propertypilot.web.exception.BusinessException("The current subscription plan does not have a payable renewal price");
        }

        List<InvoiceEntity> priorRenewalInvoices = invoiceRepository.findByEntityTypeAndEntityIdAndInvoiceType(
                BillingEntityType.SUBSCRIPTION, customerSubscriptionId, InvoiceType.SUBSCRIPTION_RENEWAL);
        for (InvoiceEntity prior : priorRenewalInvoices) {
            if (prior.getStatus() == InvoiceStatus.CANCELLED) continue;
            if (periodStart.equals(prior.getRenewalPeriodStart()) && periodEnd.equals(prior.getRenewalPeriodEnd())) {
                return mapInvoice(prior);
            }
            throw new com.propertypilot.web.exception.BusinessException(
                    "An existing subscription renewal invoice must be paid or cancelled before creating another period");
        }

        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setCustomer(subscription.getCustomer());
        invoice.setEntityType(BillingEntityType.SUBSCRIPTION);
        invoice.setEntityId(customerSubscriptionId);
        invoice.setInvoiceType(InvoiceType.SUBSCRIPTION_RENEWAL);
        invoice.setInvoiceAmount(planVersion.getPrice());
        invoice.setTaxAmount(BigDecimal.ZERO);
        invoice.setCurrencyCode(planVersion.getCurrencyCode() == null || planVersion.getCurrencyCode().isBlank()
                ? "INR" : planVersion.getCurrencyCode().trim().toUpperCase(java.util.Locale.ROOT));
        invoice.setStatus(InvoiceStatus.DRAFT);
        invoice.setDescription("Subscription renewal");
        invoice.setRenewalPeriodStart(periodStart);
        invoice.setRenewalPeriodEnd(periodEnd);
        invoice.setInvoiceNumber(generateInvoiceNumber());
        return mapInvoice(invoiceRepository.save(invoice));
    }

    @Override
    public InvoiceResponse sendInvoice(UUID invoiceId) {
        InvoiceEntity invoice = invoiceRepository.findByIdForUpdate(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
        securityService.validateOperationsAccess();
        if (invoice.getStatus() != InvoiceStatus.DRAFT) throw new com.propertypilot.web.exception.BusinessException("Only draft invoices can be sent");
        OffsetDateTime issuedAt = OffsetDateTime.now();
        invoice.setIssuedAt(issuedAt);
        invoice.setDueAt(issuedAt.plusDays(30));
        invoice.setStatus(InvoiceStatus.SENT);
        return mapInvoice(invoiceRepository.save(invoice));
    }

    @Override
    public InvoiceResponse cancelInvoice(UUID invoiceId) {
        InvoiceEntity invoice = invoiceRepository.findByIdForUpdate(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
        securityService.validateOperationsAccess();
        if (invoice.getStatus() != InvoiceStatus.DRAFT && invoice.getStatus() != InvoiceStatus.SENT
                && invoice.getStatus() != InvoiceStatus.PARTIALLY_PAID && invoice.getStatus() != InvoiceStatus.OVERDUE) {
            throw new com.propertypilot.web.exception.BusinessException("Invoice cannot be cancelled from its current state");
        }
        if (settledPaymentLookup.existsByInvoice_InvoiceIdAndPaymentStatus(
                invoiceId, com.propertypilot.domain.enums.PaymentStatus.PENDING)) {
            throw new com.propertypilot.web.exception.BusinessException(
                    "An invoice with a pending payment cannot be cancelled");
        }
        if (paymentRepositoryHasSettled(invoiceId)) throw new com.propertypilot.web.exception.BusinessException("An invoice with settled payments cannot be cancelled");
        invoice.setStatus(InvoiceStatus.CANCELLED);
        return mapInvoice(invoiceRepository.save(invoice));
    }

    private boolean paymentRepositoryHasSettled(UUID invoiceId) {
        return settledPaymentLookup.existsByInvoice_InvoiceIdAndPaymentStatus(invoiceId, com.propertypilot.domain.enums.PaymentStatus.SUCCESS);
    }

    @org.springframework.scheduling.annotation.Scheduled(cron = "0 10 0 * * *")
    @Transactional
    public void markOverdueInvoices() {
        for (InvoiceEntity invoice : invoiceRepository.findByStatusInAndDueAtBeforeForUpdate(
                List.of(InvoiceStatus.SENT, InvoiceStatus.PARTIALLY_PAID), OffsetDateTime.now())) {
            invoice.setStatus(InvoiceStatus.OVERDUE);
        }
    }

    private String generateInvoiceNumber() {
        return "PP-INV-" + invoiceRepository.nextInvoiceNumberSequence();
    }

  @Override
@Transactional(readOnly = true)
public InvoiceResponse getInvoice(
        UUID invoiceId) {

    InvoiceEntity invoice =
            invoiceRepository
                    .findById(invoiceId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Invoice not found"));
    securityService.validateInvoiceAccess(invoice);

    return mapInvoice(invoice);
}

@Override
@Transactional(readOnly = true)
public List<InvoiceResponse>
getAllInvoices() {

    securityService.validateOperationsAccess();

    return invoiceRepository
            .findAll()
            .stream()
            .map(this::mapInvoice)
            .toList();
}

@Override
@Transactional(readOnly = true)
public List<InvoiceResponse>
getInvoicesByCustomer(
        UUID customerId) {

    securityService.validateCustomerAccess(customerId);

    return invoiceRepository
            .findByCustomerCustomerId(
                    customerId)
            .stream()
            .map(this::mapInvoice)
            .toList();
}

@Override
@Transactional(readOnly = true)
public List<InvoiceResponse>
getInvoicesByEntity(
        BillingEntityType entityType,
        UUID entityId) {
    List<InvoiceEntity> invoices = invoiceRepository
            .findByEntityTypeAndEntityId(
                    entityType, entityId);
    invoices.forEach(securityService::validateInvoiceAccess);
    return invoices.stream()
            .map(this::mapInvoice)
            .toList();
}

private InvoiceResponse mapInvoice(
        InvoiceEntity entity) {

    InvoiceResponse response =
            new InvoiceResponse();

    response.setInvoiceId(
            entity.getInvoiceId());

    response.setCustomerId(
            entity.getCustomer()
                    .getCustomerId());
    if (entity.getCustomer().getUser() != null) {
        response.setCustomerName(entity.getCustomer().getUser().getFullName());
    }

    response.setEntityType(
            entity.getEntityType());

    response.setEntityId(
            entity.getEntityId());

    response.setInvoiceType(
            entity.getInvoiceType());

    response.setInvoiceNumber(
            entity.getInvoiceNumber());

    response.setInvoiceAmount(
            entity.getInvoiceAmount());

    response.setTaxAmount(
            entity.getTaxAmount());

    response.setCurrencyCode(entity.getCurrencyCode());
    response.setRenewalPeriodStart(entity.getRenewalPeriodStart());
    response.setRenewalPeriodEnd(entity.getRenewalPeriodEnd());

    response.setStatus(
            entity.getStatus());

    response.setDescription(
            entity.getDescription());

    response.setIssuedAt(
            entity.getIssuedAt());

    response.setDueAt(
            entity.getDueAt());

    response.setPaidAt(
            entity.getPaidAt());

    return response;
}

}
