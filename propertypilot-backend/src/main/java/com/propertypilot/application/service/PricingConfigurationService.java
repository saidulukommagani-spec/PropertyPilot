package com.propertypilot.application.service;

import com.propertypilot.application.dto.PricingParameterResponse;
import com.propertypilot.application.dto.UpdatePricingParameterRequest;

import java.util.List;
import java.util.UUID;

public interface PricingConfigurationService {

    List<PricingParameterResponse>
    getProfileParameters(
            String profileCode);

    PricingParameterResponse
    updateParameter(
            UUID parameterId,
            UpdatePricingParameterRequest request);

           String getParameterValue(
        String profileCode,
        String parameterCode);

java.math.BigDecimal getDecimalParameter(
        String profileCode,
        String parameterCode); 

        
}