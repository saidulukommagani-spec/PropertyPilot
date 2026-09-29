package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreatePricingEstimateRequest;
import com.propertypilot.application.dto.PricingEstimateResponse;

public interface PricingEngineService {

    PricingEstimateResponse calculateEstimate(
            CreatePricingEstimateRequest request);
}