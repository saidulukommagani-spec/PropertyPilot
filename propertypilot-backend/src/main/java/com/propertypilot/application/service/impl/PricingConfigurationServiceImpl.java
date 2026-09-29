package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.PricingParameterResponse;
import com.propertypilot.application.dto.UpdatePricingParameterRequest;
import com.propertypilot.application.service.PricingConfigurationService;
import com.propertypilot.infrastructure.persistence.entity.PricingProfileParameterEntity;
import com.propertypilot.infrastructure.persistence.repository.PricingProfileParameterRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.propertypilot.application.service.PricingHistoryService;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class PricingConfigurationServiceImpl
        implements PricingConfigurationService {

   private final PricingProfileParameterRepository
        parameterRepository;

private final PricingHistoryService
        pricingHistoryService;

public PricingConfigurationServiceImpl(
        PricingProfileParameterRepository parameterRepository,
        PricingHistoryService pricingHistoryService) {

    this.parameterRepository =
            parameterRepository;

    this.pricingHistoryService =
            pricingHistoryService;
}

    @Override
    @Transactional(readOnly = true)
    public List<PricingParameterResponse>
    getProfileParameters(
            String profileCode) {

        return parameterRepository
                .findByPricingProfileProfileCode(profileCode)
                .stream()
                .filter(parameter ->
                        parameter.getPricingProfile()
                                .getProfileCode()
                                .equalsIgnoreCase(profileCode))
                .map(this::buildResponse)
                .toList();
    }

    @Override
public PricingParameterResponse
updateParameter(
        UUID parameterId,
        UpdatePricingParameterRequest request) {

    PricingProfileParameterEntity entity =
            parameterRepository
                    .findById(parameterId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Pricing parameter not found"));

    String oldValue =
            entity.getParameterValue();

    String newValue =
            request.getParameterValue();

    pricingHistoryService.recordChange(
            entity,
            oldValue,
            newValue,
            null, // logged-in admin user id later
            request.getRemarks());

    entity.setParameterValue(
            newValue);

    return buildResponse(
            parameterRepository.save(entity));
}

@Override
@Transactional(readOnly = true)
public String getParameterValue(
        String profileCode,
        String parameterCode) {

    PricingProfileParameterEntity entity =
            parameterRepository
                    .findByPricingProfileProfileCodeAndParameterCode(
                            profileCode,
                            parameterCode)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Pricing parameter not found"));

    return entity.getParameterValue();
}

    private PricingParameterResponse
    buildResponse(
            PricingProfileParameterEntity entity) {

        PricingParameterResponse response =
                new PricingParameterResponse();

        response.setPricingParameterId(
                entity.getPricingParameterId());

        response.setProfileCode(
                entity.getPricingProfile()
                        .getProfileCode());

        response.setParameterCode(
                entity.getParameterCode());

        response.setParameterName(
                entity.getParameterName());

        response.setParameterValue(
                entity.getParameterValue());

        response.setParameterType(
                entity.getParameterType());

        response.setActiveFlag(
                entity.getActiveFlag());

        response.setVersion(
                entity.getVersion());

        return response;
    }
   
@Override
@Transactional(readOnly = true)
public BigDecimal getDecimalParameter(
        String profileCode,
        String parameterCode) {

    String value =
            getParameterValue(
                    profileCode,
                    parameterCode);

    return new BigDecimal(value);
}
}