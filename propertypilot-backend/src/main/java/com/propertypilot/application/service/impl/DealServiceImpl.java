package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.*;
import com.propertypilot.application.service.DealService;
import com.propertypilot.application.service.PricingConfigurationService;
import com.propertypilot.domain.constants.PricingParameterCodes;
import com.propertypilot.domain.enums.*;
import com.propertypilot.infrastructure.persistence.entity.*;
import com.propertypilot.infrastructure.persistence.repository.*;
import com.propertypilot.infrastructure.persistence.entity.LeadActivityEntity;
import com.propertypilot.web.exception.BusinessException;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.propertypilot.security.SecurityService;
import com.propertypilot.application.service.BillingService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;



@Service
@Transactional
public class DealServiceImpl
        implements DealService {

    private final DealRepository dealRepository;

    private final DealParticipantRepository
            participantRepository;

    private final CustomerRepository
            customerRepository;

    private final PropertyRepository
            propertyRepository;

    private final LeadRepository
            leadRepository;

    private final LeadActivityRepository leadActivityRepository;

private final SecurityService securityService;

private final BillingService billingService;

private final PricingConfigurationService pricingConfigurationService;
private final InvoiceRepository invoiceRepository;

    public DealServiceImpl(
            DealRepository dealRepository,
            DealParticipantRepository participantRepository,
            CustomerRepository customerRepository,
            PropertyRepository propertyRepository,
                        LeadRepository leadRepository,
            LeadActivityRepository leadActivityRepository,
        SecurityService securityService,
BillingService billingService,
PricingConfigurationService pricingConfigurationService,
InvoiceRepository invoiceRepository) {

        this.dealRepository =
                dealRepository;

        this.participantRepository =
                participantRepository;

        this.customerRepository =
                customerRepository;

        this.propertyRepository =
                propertyRepository;

        this.leadRepository =
                leadRepository;
        this.leadActivityRepository = leadActivityRepository;
        this.securityService = securityService;

        this.billingService = billingService;

        this.pricingConfigurationService = pricingConfigurationService;
        this.invoiceRepository = invoiceRepository;

    }

    @Override
    public DealResponse createDeal(
            CreateDealRequest request) {
        securityService.validateAdminAccess();
        if (request == null || request.getPropertyId() == null || request.getSellerCustomerId() == null
                || request.getDealType() == null || request.getDealType().isBlank()) {
            throw new BusinessException("Property, seller, and deal type are required");
        }
        if (request.getExpectedAmount() == null || request.getExpectedAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Expected amount must be greater than zero");
        }
        validateCommission(request.getSellerCommissionPercent(), "Seller");
        validateCommission(request.getBuyerCommissionPercent(), "Buyer");
        if (request.getLeadId() != null) {
            throw new BusinessException("Create lead-linked deals through the lead conversion workflow");
        }
        Property property =
                propertyRepository
                        .findById(
                                request.getPropertyId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Property not found"));

        CustomerEntity seller =
                customerRepository
                        .findById(
                                request.getSellerCustomerId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Seller not found"));

        if (property.getCustomer() == null
                || !java.util.Objects.equals(property.getCustomer().getCustomerId(), seller.getCustomerId())) {
            throw new BusinessException("The selected seller must own the selected property");
        }

        LeadEntity lead = null;

        DealEntity entity =
                new DealEntity();

        entity.setProperty(
                property);

        entity.setSeller(
                seller);

        entity.setLead(
                lead);

        try {
            entity.setDealType(DealType.valueOf(request.getDealType()));
        } catch (IllegalArgumentException exception) {
            throw new BusinessException("Unsupported deal type");
        }

        entity.setDealStatus(
                DealStatus.NEW);
if (request.getDealSource() != null) {

    entity.setDealSource(
            DealSource.valueOf(
                    request.getDealSource()));
}

entity.setExclusiveListing(
        request.getExclusiveListing());
        entity.setExpectedPrice(
                request.getExpectedAmount());

        entity.setBuyerCommissionPercent(
                request.getBuyerCommissionPercent());

        entity.setSellerCommissionPercent(
                request.getSellerCommissionPercent());

        entity.setRemarks(
                request.getRemarks());

        entity.setCreatedAt(
                OffsetDateTime.now());

        DealEntity saved =
                dealRepository.save(
                        entity);

        return mapResponse(
                saved);
    }

    @Override
    public DealResponse convertLeadToDeal(UUID leadId, LeadDealConversionRequest request) {
        securityService.validateAdminAccess();
        LeadEntity lead = leadRepository.findByLeadId(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found"));

        String duplicateReason = request.duplicateOpportunityReason() == null
                ? null : request.duplicateOpportunityReason().trim();
        boolean alreadyConverted = lead.getStatus() == LeadStatus.DEAL_CONVERTED
                || lead.getStatus() == LeadStatus.CONVERTED;
        if (lead.getStatus() != LeadStatus.QUALIFIED
                && lead.getStatus() != LeadStatus.READY_FOR_DEAL
                && !alreadyConverted) {
            throw new BusinessException("Lead must be qualified or marked READY_FOR_DEAL before conversion");
        }
        if (alreadyConverted && isBlank(duplicateReason)) {
            throw new BusinessException("Provide an authorized reason to create another deal from an already converted lead");
        }
        if (lead.getCustomer() == null) {
            throw new BusinessException("A customer must be linked to the lead before conversion");
        }

        DealType dealType;
        try {
            dealType = DealType.valueOf(request.dealType());
        } catch (IllegalArgumentException exception) {
            throw new BusinessException("Unsupported deal type");
        }
        if (dealType == DealType.SERVICE_CONTRACT || dealType == DealType.SUBSCRIPTION_RENEWAL) {
            throw new BusinessException("Lead conversion supports property transaction deals only");
        }
        if (request.expectedAmount() == null || request.expectedAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Expected amount must be greater than zero");
        }
        validateCommission(request.sellerCommissionPercent(), "Seller");
        validateCommission(request.buyerCommissionPercent(), "Buyer");

        Property property = null;
        if (request.propertyId() != null) {
            property = propertyRepository.findById(request.propertyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
            if (!property.getCustomer().getCustomerId().equals(lead.getCustomer().getCustomerId())) {
                throw new BusinessException("Selected property must belong to the lead customer");
            }
        } else if (isBlank(lead.getPropertyType()) || isBlank(lead.getLocation())) {
            throw new BusinessException("Select a customer property or provide prospective property type and location on the lead");
        }

        List<DealStatus> activeStatuses = List.of(
                DealStatus.NEW, DealStatus.BUYER_SEARCH_IN_PROGRESS, DealStatus.BUYER_SHORTLISTED,
                DealStatus.OFFER_RECEIVED, DealStatus.NEGOTIATION_IN_PROGRESS, DealStatus.BUYER_SELECTED,
                DealStatus.SITE_VISIT_SCHEDULED, DealStatus.DOCUMENT_VERIFICATION,
                DealStatus.AGREEMENT_PENDING, DealStatus.REGISTRATION_SCHEDULED);
        boolean duplicate = dealRepository.existsByLead_LeadIdAndDealTypeAndDealStatusIn(
                leadId, dealType, activeStatuses);
        if (duplicate && isBlank(duplicateReason)) {
            throw new BusinessException("An active deal of this type already exists; provide an authorized duplicate opportunity reason");
        }

        // Direct conversion records the readiness decision as a distinct, durable audit event.
        if (lead.getStatus() == LeadStatus.QUALIFIED) {
            lead.setStatus(LeadStatus.READY_FOR_DEAL);
            recordLeadActivity(lead, "STATUS_CHANGE", "Lead marked ready for deal",
                    "Readiness confirmed as part of direct conversion.");
        }

        DealEntity deal = new DealEntity();
        deal.setLead(lead);
        deal.setSeller(lead.getCustomer());
        deal.setProperty(property);
        deal.setDealType(dealType);
        deal.setDealStatus(DealStatus.NEW);
        deal.setDealSource(DealSource.LEAD);
        deal.setExpectedPrice(request.expectedAmount());
        deal.setSellerCommissionPercent(request.sellerCommissionPercent());
        deal.setBuyerCommissionPercent(request.buyerCommissionPercent());
        deal.setRemarks(appendDuplicateReason(request.remarks(), duplicateReason));
        DealEntity saved = dealRepository.save(deal);

        lead.setStatus(LeadStatus.DEAL_CONVERTED);
        if (lead.getConvertedAt() == null) {
            lead.setConvertedAt(OffsetDateTime.now());
        }
        leadRepository.save(lead);
        String auditDetails = "Deal " + saved.getDealId() + " created as " + dealType.name()
                + (duplicateReason == null ? "" : ". Additional opportunity reason: " + duplicateReason);
        recordLeadActivity(lead, "CONVERSION", "Lead converted to deal", auditDetails);
        return mapResponse(saved);
    }

    @Override
    public List<DealResponse> getDealsByLead(UUID leadId) {
        LeadEntity lead = leadRepository.findByLeadId(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found"));
        securityService.validateLeadOwnership(lead);
        return dealRepository.findByLead_LeadIdOrderByCreatedAtDesc(leadId)
                .stream().map(this::mapResponse).toList();
    }

    private void recordLeadActivity(LeadEntity lead, String type, String subject, String details) {
        LeadActivityEntity activity = new LeadActivityEntity();
        activity.setLead(lead);
        activity.setActivityType(type);
        activity.setSubject(subject);
        activity.setDetails(details);
        activity.setVisibility("INTERNAL");
        activity.setOccurredAt(OffsetDateTime.now());
        activity.setPerformedBy(securityService.getCurrentUser());
        leadActivityRepository.save(activity);
    }

    private void validateCommission(BigDecimal percent, String party) {
        if (percent != null && (percent.compareTo(BigDecimal.ZERO) < 0
                || percent.compareTo(BigDecimal.valueOf(100)) > 0)) {
            throw new BusinessException(party + " commission percent must be between 0 and 100");
        }
    }

    private String appendDuplicateReason(String remarks, String reason) {
        if (isBlank(reason)) return remarks;
        return (isBlank(remarks) ? "" : remarks.trim() + "\n\n")
                + "Additional opportunity authorization: " + reason;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    @Override
    public DealResponse getDeal(
            UUID dealId) {
        DealEntity deal = dealRepository
                        .findById(dealId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Deal not found"));
        securityService.validateDealAccess(deal);
        return mapResponse(deal);
    }
    

    @Override
    public List<DealResponse>
    getAllDeals() {
        securityService.validateAdminAccess();

        return dealRepository
                .findAll()
                .stream()
                .map(this::mapResponse)
                .toList();
    }

    @Override
    public DealParticipantResponse
    addParticipant(
            UUID dealId,
            AddDealParticipantRequest request) {
                securityService.validateAdminAccess();

        DealEntity deal =
                dealRepository
                        .findById(dealId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Deal not found"));
        if (deal.getDealStatus() == DealStatus.COMPLETED || deal.getDealStatus() == DealStatus.CANCELLED) {
            throw new BusinessException("Participants cannot be added to a completed or cancelled deal");
        }
        
        CustomerEntity customer =
                customerRepository
                        .findById(
                                request.getCustomerId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found"));

        DealParticipantEntity participant =
                new DealParticipantEntity();

        participant.setDeal(
                deal);

        participant.setCustomer(
                customer);

        participant.setParticipantRole(
                ParticipantRole.valueOf(
                        request.getParticipantRole()));

        participant.setParticipantStatus(
                ParticipantStatus.ACTIVE);

        participant.setContactVisibility(
                ContactVisibility.valueOf(
                        request.getContactVisibility()));

        participant.setOfferedAmount(
                request.getOfferedAmount());

        participant.setOfferDate(
                OffsetDateTime.now());

        participant.setSelectedFlag(
                false);

        participant.setRemarks(
                request.getRemarks());

        participant =
                participantRepository
                        .save(participant);

        return mapParticipant(
                participant);
    }

    @Override
    public List<DealParticipantResponse>
    getParticipants(
            UUID dealId) {

        DealEntity deal = dealRepository.findById(dealId)
                .orElseThrow(() -> new ResourceNotFoundException("Deal not found"));
        securityService.validateDealAccess(deal);

        return participantRepository
                .findByDeal_DealId(
                        dealId)
                .stream()
                .map(this::mapParticipant)
                .toList();
    }

    @Override
    public DealParticipantResponse
    selectBuyer(
            UUID dealId,
            UUID participantId) {
securityService.validateAdminAccess();
        DealEntity deal =
                dealRepository
                        .findById(dealId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Deal not found"));
        if (deal.getDealStatus() != DealStatus.NEGOTIATION_IN_PROGRESS) {
            throw new BusinessException("A buyer can be selected only during negotiation");
        }

        List<DealParticipantEntity>
                participants =
                participantRepository
                        .findByDeal_DealId(
                                dealId);

        for (DealParticipantEntity p
                : participants) {

            p.setSelectedFlag(false);

            if (p.getParticipantStatus()
                    == ParticipantStatus.SELECTED) {

                p.setParticipantStatus(
                        ParticipantStatus.ACTIVE);
            }

            participantRepository.save(p);
        }

        DealParticipantEntity selected =
                participantRepository
                        .findById(participantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Participant not found"));


        if (!selected.getDeal()
        .getDealId()
        .equals(dealId)) {

    throw new BusinessException(
            "Participant does not belong to this deal");
}
        selected.setSelectedFlag(true);

        selected.setParticipantStatus(
                ParticipantStatus.SELECTED);

        participantRepository.save(
                selected);

      deal.setSelectedBuyerId(
        selected.getCustomer()
                .getCustomerId());

        deal.setDealStatus(
                DealStatus.BUYER_SELECTED);

        dealRepository.save(
                deal);

        return mapParticipant(
                selected);
    }

    private DealResponse mapResponse(
            DealEntity entity) {

        DealResponse response =
                new DealResponse();

        response.setDealId(
                entity.getDealId());

        response.setPropertyId(entity.getProperty() == null
                ? null : entity.getProperty().getPropertyId());

        response.setSellerCustomerId(
                entity.getSeller()
                        .getCustomerId());

        response.setLeadId(
                entity.getLead() != null
                        ? entity.getLead()
                                .getLeadId()
                        : null);

        response.setDealType(
                entity.getDealType()
                        .name());

        response.setDealStatus(
                entity.getDealStatus()
                        .name());

        response.setExpectedAmount(
                entity.getExpectedPrice());

        response.setFinalAmount(
                entity.getFinalDealAmount());

        response.setSellerCommissionPercent(
                entity.getSellerCommissionPercent());

        response.setBuyerCommissionPercent(
                entity.getBuyerCommissionPercent());

        response.setCreatedAt(
                entity.getCreatedAt());

        response.setRemarks(
                entity.getRemarks());

        return response;
    }

    @Override
public DealResponse updateDealStatus(
        UUID dealId,
        UpdateDealStatusRequest request) {
securityService.validateAdminAccess();
    DealEntity deal =
            dealRepository
                    .findById(dealId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Deal not found"));

    DealStatus nextStatus;
    try {
        nextStatus = DealStatus.valueOf(request.getDealStatus());
    } catch (IllegalArgumentException | NullPointerException exception) {
        throw new BusinessException("Unsupported deal status");
    }
    if (!isValidStageTransition(deal.getDealStatus(), nextStatus)) {
        throw new BusinessException("Deal status transition is not allowed; use the dedicated complete or cancel action");
    }
    deal.setDealStatus(nextStatus);

    if (request.getRemarks() != null) {
        deal.setRemarks(
                request.getRemarks());
    }
    DealEntity saved =
            dealRepository.save(deal);

    return mapDeal(saved);
}

private boolean isValidStageTransition(DealStatus current, DealStatus next) {
    if (current == null || next == null || current == next
            || current == DealStatus.COMPLETED || current == DealStatus.CANCELLED
            || next == DealStatus.COMPLETED || next == DealStatus.CANCELLED) return false;
    return switch (current) {
        case NEW -> next == DealStatus.BUYER_SEARCH_IN_PROGRESS || next == DealStatus.BUYER_SHORTLISTED;
        case BUYER_SEARCH_IN_PROGRESS -> next == DealStatus.BUYER_SHORTLISTED || next == DealStatus.OFFER_RECEIVED;
        case BUYER_SHORTLISTED -> next == DealStatus.OFFER_RECEIVED || next == DealStatus.NEGOTIATION_IN_PROGRESS;
        case OFFER_RECEIVED -> next == DealStatus.NEGOTIATION_IN_PROGRESS;
        case NEGOTIATION_IN_PROGRESS -> next == DealStatus.BUYER_SELECTED;
        case BUYER_SELECTED -> next == DealStatus.SITE_VISIT_SCHEDULED || next == DealStatus.DOCUMENT_VERIFICATION;
        case SITE_VISIT_SCHEDULED -> next == DealStatus.DOCUMENT_VERIFICATION;
        case DOCUMENT_VERIFICATION -> next == DealStatus.AGREEMENT_PENDING;
        case AGREEMENT_PENDING -> next == DealStatus.REGISTRATION_SCHEDULED;
        case REGISTRATION_SCHEDULED, COMPLETED, CANCELLED -> false;
    };
}

    private DealParticipantResponse
    mapParticipant(
            DealParticipantEntity entity) {

        DealParticipantResponse response =
                new DealParticipantResponse();

        response.setParticipantId(
                entity.getDealParticipantId());

        response.setDealId(
                entity.getDeal()
                        .getDealId());

        response.setCustomerId(
                entity.getCustomer()
                        .getCustomerId());

        response.setCustomerName(
                entity.getCustomer()
                        .getUser()
                        .getFullName());

        response.setParticipantRole(
                entity.getParticipantRole()
                        .name());

        response.setParticipantStatus(
                entity.getParticipantStatus()
                        .name());

        response.setContactVisibility(
                entity.getContactVisibility()
                        .name());

        response.setOfferedAmount(
                entity.getOfferedAmount());

        response.setOfferDate(
                entity.getOfferDate());

        response.setSelectedFlag(
                entity.getSelectedFlag());

        response.setRejectedReason(
                entity.getRejectedReason());

        response.setRemarks(
                entity.getRemarks());

        return response;
    }
@Override
public DealResponse finalizeDeal(
        UUID dealId,
        FinalizeDealRequest request) {
securityService.validateAdminAccess();
    DealEntity deal =
            dealRepository
                    .findByIdForUpdate(dealId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Deal not found"));

    if (deal.getDealStatus() == DealStatus.COMPLETED) return mapDeal(deal);
    if (deal.getDealStatus() != DealStatus.REGISTRATION_SCHEDULED || deal.getSelectedBuyerId() == null) {
        throw new BusinessException("Deal must have a selected buyer and be registration-scheduled before completion");
    }
    if (request.getFinalDealAmount() == null || request.getFinalDealAmount().signum() <= 0) {
        throw new BusinessException("Final deal amount must be greater than zero");
    }

    deal.setFinalDealAmount(
            request.getFinalDealAmount());

    deal.setRegistrationDate(
            request.getRegistrationDate());

    deal.setCompletedAt(
            OffsetDateTime.now());

    deal.setDealStatus(
            DealStatus.COMPLETED);

    BigDecimal finalAmount =
            request.getFinalDealAmount();

    if (deal.getBuyerCommissionPercent() != null) {

        deal.setBuyerCommissionAmount(
                finalAmount.multiply(
                        deal.getBuyerCommissionPercent())
                        .divide(
                                BigDecimal.valueOf(100)));
    }

    if (deal.getSellerCommissionPercent() != null) {

        deal.setSellerCommissionAmount(
                finalAmount.multiply(
                        deal.getSellerCommissionPercent())
                        .divide(
                                BigDecimal.valueOf(100)));
    }

    if (request.getRemarks() != null) {
        deal.setRemarks(
                request.getRemarks());
    }

    DealEntity saved =
            dealRepository.save(deal);

    ensureCommissionInvoices(saved);

    return mapDeal(saved);
}

@Override
public DealResponse cancelDeal(
        UUID dealId,
        String reason) {
securityService.validateAdminAccess();
    DealEntity deal =
            dealRepository
                    .findById(dealId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Deal not found"));

    if (deal.getDealStatus() == DealStatus.COMPLETED || deal.getDealStatus() == DealStatus.CANCELLED) {
        throw new BusinessException("A completed or cancelled deal cannot be cancelled");
    }

    deal.setDealStatus(
            DealStatus.CANCELLED);

    deal.setCancelledAt(
            OffsetDateTime.now());

    deal.setRemarks(reason);

    DealEntity saved =
            dealRepository.save(deal);

    return mapDeal(saved);
}

private DealResponse mapDeal(
        DealEntity entity) {

    DealResponse response =
            new DealResponse();

    response.setDealId(
            entity.getDealId());

    response.setPropertyId(entity.getProperty() == null
            ? null : entity.getProperty().getPropertyId());

    response.setSellerCustomerId(
            entity.getSeller()
                    .getCustomerId());

    response.setLeadId(
            entity.getLead() != null
                    ? entity.getLead()
                            .getLeadId()
                    : null);

    response.setDealType(
            entity.getDealType()
                    .name());

    response.setDealStatus(
            entity.getDealStatus()
                    .name());

  response.setDealSource(
        entity.getDealSource() != null
                ? entity.getDealSource().name()
                : null);

    response.setExpectedAmount(
            entity.getExpectedPrice());

    response.setFinalAmount(
            entity.getFinalDealAmount());

    response.setBuyerCommissionPercent(
            entity.getBuyerCommissionPercent());

    response.setSellerCommissionPercent(
            entity.getSellerCommissionPercent());

    response.setBuyerCommissionAmount(
            entity.getBuyerCommissionAmount());

    response.setSellerCommissionAmount(
            entity.getSellerCommissionAmount());
if (entity.getBuyerCommissionAmount() != null
        && entity.getSellerCommissionAmount() != null) {

    response.setTotalCommissionAmount(
            entity.getBuyerCommissionAmount()
                    .add(
                            entity.getSellerCommissionAmount()));
}
    response.setSelectedBuyerCustomerId(
            entity.getSelectedBuyerId());

    response.setExclusiveListing(
            entity.getExclusiveListing());

    response.setRemarks(
            entity.getRemarks());

    response.setCreatedAt(
            entity.getCreatedAt());

    response.setClosedAt(
            entity.getCompletedAt());

    return response;
}
@Override
public DealResponse completeDeal(
        UUID dealId,
        BigDecimal finalDealAmount) {
securityService.validateAdminAccess();
    DealEntity deal =
            dealRepository
                    .findByIdForUpdate(dealId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Deal not found"));

    if (deal.getDealStatus() == DealStatus.COMPLETED) return mapDeal(deal);
    if (deal.getDealStatus() != DealStatus.REGISTRATION_SCHEDULED) {
        throw new BusinessException("Deal must be registration-scheduled before completion");
    }
    if (finalDealAmount == null || finalDealAmount.signum() <= 0) {
        throw new BusinessException("Final deal amount must be greater than zero");
    }

    if (deal.getSelectedBuyerId() == null) {

        throw new BusinessException(
                "Buyer not selected");
    }

BigDecimal buyerPercent =
        pricingConfigurationService
                .getDecimalParameter(
                        "DEFAULT",
                        PricingParameterCodes.BUYER_COMMISSION_PERCENT);

   BigDecimal sellerPercent =
        pricingConfigurationService
                .getDecimalParameter(
                        "DEFAULT",
                        PricingParameterCodes.SELLER_COMMISSION_PERCENT);

    BigDecimal buyerAmount =
            finalDealAmount
                    .multiply(buyerPercent)
                    .divide(
                            BigDecimal.valueOf(100));

    BigDecimal sellerAmount =
            finalDealAmount
                    .multiply(sellerPercent)
                    .divide(
                            BigDecimal.valueOf(100));

    deal.setFinalDealAmount(
            finalDealAmount);

    deal.setBuyerCommissionPercent(
            buyerPercent);

    deal.setSellerCommissionPercent(
            sellerPercent);

    deal.setBuyerCommissionAmount(
            buyerAmount);

    deal.setSellerCommissionAmount(
            sellerAmount);

    deal.setDealStatus(
            DealStatus.COMPLETED);

    deal.setCompletedAt(
            OffsetDateTime.now());

   DealEntity saved =
        dealRepository.save(
                deal);

    ensureCommissionInvoices(saved);

    return mapDeal(saved);
}
private void ensureCommissionInvoices(
        DealEntity deal) {

    if (deal.getBuyerCommissionAmount() != null
            && deal.getBuyerCommissionAmount()
            .compareTo(BigDecimal.ZERO) > 0) {

        createCommissionInvoiceIfMissing(deal, InvoiceType.BUYER_COMMISSION,
                deal.getSelectedBuyerId(), deal.getBuyerCommissionAmount(), "Buyer commission invoice");
    }

    if (deal.getSellerCommissionAmount() != null
            && deal.getSellerCommissionAmount()
            .compareTo(BigDecimal.ZERO) > 0) {

        createCommissionInvoiceIfMissing(deal, InvoiceType.SELLER_COMMISSION,
                deal.getSeller().getCustomerId(), deal.getSellerCommissionAmount(), "Seller commission invoice");
    }
}

private void createCommissionInvoiceIfMissing(DealEntity deal, InvoiceType type, UUID customerId,
        BigDecimal amount, String description) {
    boolean exists = invoiceRepository.findByEntityTypeAndEntityIdAndInvoiceType(
            BillingEntityType.DEAL, deal.getDealId(), type).stream().findAny().isPresent();
    if (!exists) {
        billingService.createInvoice(BillingEntityType.DEAL, deal.getDealId(), customerId, type, amount, description);
    }
}


}
