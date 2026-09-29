package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.*;
import com.propertypilot.application.service.PricingRuleService;
import com.propertypilot.infrastructure.persistence.entity.PricingRuleEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.infrastructure.persistence.repository.PricingRuleRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class PricingRuleServiceImpl
        implements PricingRuleService {

    private static final Set<String>
            ALLOWED_STATUSES =
            Set.of(
                    "DRAFT",
                    "ACTIVE",
                    "INACTIVE",
                    "RETIRED");

    private final PricingRuleRepository
            pricingRuleRepository;

    private final ServiceRepository
            serviceRepository;

    public PricingRuleServiceImpl(
            PricingRuleRepository pricingRuleRepository,
            ServiceRepository serviceRepository) {

        this.pricingRuleRepository =
                pricingRuleRepository;

        this.serviceRepository =
                serviceRepository;
    }
@Override
public PricingRuleResponse createRule(
        CreatePricingRuleRequest request) {

    validateStatus(
            request.getStatus());

    validateDates(
            request.getEffectiveFrom(),
            request.getEffectiveTo());

    ServiceEntity service =
            serviceRepository.findById(
                            request.getServiceId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Service not found"));

    if (pricingRuleRepository
            .existsByService_ServiceIdAndRuleNameAndStatus(
                    request.getServiceId(),
                    request.getRuleName(),
                    "ACTIVE")) {

        throw new IllegalArgumentException(
                "Active pricing rule already exists");
    }

    PricingRuleEntity entity =
            new PricingRuleEntity();

    entity.setService(service);
    entity.setRuleName(request.getRuleName());
    entity.setBaseAmount(request.getBaseAmount());
    entity.setCurrencyCode(request.getCurrencyCode());
    entity.setRuleDefinition(
            request.getRuleDefinition());
    entity.setEffectiveFrom(
            request.getEffectiveFrom());
    entity.setEffectiveTo(
            request.getEffectiveTo());
    entity.setStatus(
            request.getStatus());

    return buildResponse(
            pricingRuleRepository.save(entity));
}

@Override
@Transactional(readOnly = true)
public PricingRuleResponse getRule(
        UUID ruleId) {

    return buildResponse(
            pricingRuleRepository
                    .findById(ruleId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Pricing rule not found")));
}

@Override
@Transactional(readOnly = true)
public List<PricingRuleResponse>
getAllRules() {

    return pricingRuleRepository
            .findAll()
            .stream()
            .map(this::buildResponse)
            .toList();
}

@Override
@Transactional(readOnly = true)
public List<PricingRuleResponse>
getRulesByService(
        UUID serviceId) {

    return pricingRuleRepository
            .findByServiceServiceId(
                    serviceId)
            .stream()
            .map(this::buildResponse)
            .toList();
}
@Override
public PricingRuleResponse updateRule(
        UUID ruleId,
        UpdatePricingRuleRequest request) {

    PricingRuleEntity entity =
            pricingRuleRepository
                    .findById(ruleId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Pricing rule not found"));

    if (request.getStatus() != null) {

        validateStatus(
                request.getStatus());

        entity.setStatus(
                request.getStatus());
    }

    OffsetDateTime effectiveFrom =
            request.getEffectiveFrom() != null
                    ? request.getEffectiveFrom()
                    : entity.getEffectiveFrom();

    OffsetDateTime effectiveTo =
            request.getEffectiveTo() != null
                    ? request.getEffectiveTo()
                    : entity.getEffectiveTo();

    validateDates(
            effectiveFrom,
            effectiveTo);

    if (request.getRuleName() != null) {
        entity.setRuleName(
                request.getRuleName());
    }

    if (request.getBaseAmount() != null) {
        entity.setBaseAmount(
                request.getBaseAmount());
    }

    if (request.getCurrencyCode() != null) {
        entity.setCurrencyCode(
                request.getCurrencyCode());
    }

    if (request.getRuleDefinition() != null) {
        entity.setRuleDefinition(
                request.getRuleDefinition());
    }

    if (request.getEffectiveFrom() != null) {
        entity.setEffectiveFrom(
                request.getEffectiveFrom());
    }

    if (request.getEffectiveTo() != null) {
        entity.setEffectiveTo(
                request.getEffectiveTo());
    }

    return buildResponse(
            pricingRuleRepository.save(entity));
}
private PricingRuleResponse buildResponse(
        PricingRuleEntity entity) {

    PricingRuleResponse response =
            new PricingRuleResponse();

    response.setPriceRuleId(
            entity.getPriceRuleId());

    response.setServiceId(
            entity.getService()
                    .getServiceId());

    response.setServiceName(
            entity.getService()
                    .getServiceName());

    response.setRuleName(
            entity.getRuleName());

    response.setBaseAmount(
            entity.getBaseAmount());

    response.setCurrencyCode(
            entity.getCurrencyCode());

    response.setRuleDefinition(
            entity.getRuleDefinition());

    response.setEffectiveFrom(
            entity.getEffectiveFrom());

    response.setEffectiveTo(
            entity.getEffectiveTo());

    response.setStatus(
            entity.getStatus());

    response.setVersion(
            entity.getVersion());

    return response;
}

private void validateStatus(
        String status) {

    if (!ALLOWED_STATUSES
            .contains(status)) {

        throw new IllegalArgumentException(
                "Invalid status");
    }
}

private void validateDates(
        OffsetDateTime effectiveFrom,
        OffsetDateTime effectiveTo) {

    if (effectiveTo != null
            && effectiveTo.isBefore(
            effectiveFrom)) {

        throw new IllegalArgumentException(
                "Effective To must be after Effective From");
    }
}



}