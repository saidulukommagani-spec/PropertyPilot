package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreatePricingEstimateRequest;
import com.propertypilot.application.dto.PricingEstimateResponse;
import com.propertypilot.application.dto.PricingEstimateRequirementsResponse;

import java.util.UUID;

public interface PricingEngineService {

    PricingEstimateResponse calculateEstimate(
            CreatePricingEstimateRequest request);

    PricingEstimateResponse calculateCustomerEstimate(CreatePricingEstimateRequest request);

    PricingEstimateRequirementsResponse getCustomerEstimateRequirements(UUID propertyId, UUID serviceId);
}
