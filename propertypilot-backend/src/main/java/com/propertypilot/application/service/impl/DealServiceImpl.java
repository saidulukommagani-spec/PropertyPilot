package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.*;
import com.propertypilot.application.service.DealService;
import com.propertypilot.application.service.PricingConfigurationService;
import com.propertypilot.domain.constants.PricingParameterCodes;
import com.propertypilot.domain.enums.*;
import com.propertypilot.infrastructure.persistence.entity.*;
import com.propertypilot.infrastructure.persistence.repository.*;
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

private final SecurityService securityService;

private final BillingService billingService;

private final PricingConfigurationService pricingConfigurationService;

    public DealServiceImpl(
            DealRepository dealRepository,
            DealParticipantRepository participantRepository,
            CustomerRepository customerRepository,
            PropertyRepository propertyRepository,
                        LeadRepository leadRepository,
        SecurityService securityService,
BillingService billingService,
PricingConfigurationService pricingConfigurationService) {

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
        this.securityService = securityService;

        this.billingService = billingService;

        this.pricingConfigurationService = pricingConfigurationService;

    }

    @Override
    public DealResponse createDeal(
            CreateDealRequest request) {
securityService.validateAdminAccess();
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

        LeadEntity lead = null;

        if (request.getLeadId() != null) {

            lead =
                    leadRepository
                            .findById(
                                    request.getLeadId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Lead not found"));
        }

        DealEntity entity =
                new DealEntity();

        entity.setProperty(
                property);

        entity.setSeller(
                seller);

        entity.setLead(
                lead);

        entity.setDealType(
                DealType.valueOf(
                        request.getDealType()));

        entity.setDealStatus(
                DealStatus.NEW);

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
    public DealResponse getDeal(
            UUID dealId) {
               
        return mapResponse(
                dealRepository
                        .findById(dealId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
    
                                        "Deal not found")));
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

        response.setPropertyId(
                entity.getProperty()
                        .getPropertyId());

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

    deal.setDealStatus(
            DealStatus.valueOf(
                    request.getDealStatus()));

    if (request.getRemarks() != null) {
        deal.setRemarks(
                request.getRemarks());
    }

    DealEntity saved =
            dealRepository.save(deal);

    return mapDeal(saved);
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
                    .findById(dealId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Deal not found"));

    deal.setFinalDealAmount(
            request.getFinalDealAmount());

    deal.setRegistrationDate(
            request.getRegistrationDate());

    deal.setCompletedAt(
            OffsetDateTime.now());

    deal.setDealStatus(
            DealStatus.COMPLETED);

            generateDealInvoices(deal);

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

    response.setPropertyId(
            entity.getProperty()
                    .getPropertyId());

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
            entity.getDealSource()
                    .name());

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
private void generateDealInvoices(
        DealEntity deal) {

    if (deal.getSellerCommissionAmount() != null
            && deal.getSellerCommissionAmount()
                    .compareTo(BigDecimal.ZERO) > 0) {

        billingService.createInvoice(
                BillingEntityType.DEAL,
                deal.getDealId(),
                deal.getSeller()
                        .getCustomerId(),
                InvoiceType.SELLER_COMMISSION,
                deal.getSellerCommissionAmount(),
                "Seller commission for deal "
                        + deal.getDealId());
    }

    if (deal.getSelectedBuyerId() != null
            && deal.getBuyerCommissionAmount() != null
            && deal.getBuyerCommissionAmount()
                    .compareTo(BigDecimal.ZERO) > 0) {

        billingService.createInvoice(
                BillingEntityType.DEAL,
                deal.getDealId(),
                deal.getSelectedBuyerId(),
                InvoiceType.BUYER_COMMISSION,
                deal.getBuyerCommissionAmount(),
                "Buyer commission for deal "
                        + deal.getDealId());
    }
}

@Override
public DealResponse completeDeal(
        UUID dealId,
        BigDecimal finalDealAmount) {

    DealEntity deal =
            dealRepository
                    .findById(dealId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Deal not found"));

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

    dealRepository.save(
            deal);

    createCommissionInvoices(
            deal);

    return mapDeal(
            deal);
}
private void createCommissionInvoices(
        DealEntity deal) {

    if (deal.getBuyerCommissionAmount() != null
            && deal.getBuyerCommissionAmount()
            .compareTo(BigDecimal.ZERO) > 0) {

        billingService.createInvoice(
                BillingEntityType.DEAL,
                deal.getDealId(),
                deal.getSelectedBuyerId(),
                InvoiceType.BUYER_COMMISSION,
                deal.getBuyerCommissionAmount(),
                "Buyer commission invoice");
    }

    if (deal.getSellerCommissionAmount() != null
            && deal.getSellerCommissionAmount()
            .compareTo(BigDecimal.ZERO) > 0) {

        billingService.createInvoice(
                BillingEntityType.DEAL,
                deal.getDealId(),
                deal.getSeller()
                        .getCustomerId(),
                InvoiceType.SELLER_COMMISSION,
                deal.getSellerCommissionAmount(),
                "Seller commission invoice");
    }
}


}