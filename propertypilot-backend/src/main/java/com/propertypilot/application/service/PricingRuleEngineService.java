package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreatePricingEstimateRequest;
import com.propertypilot.application.dto.PricingBreakdownResponse;

public interface PricingRuleEngineService {

    PricingBreakdownResponse calculateBreakdown(
            CreatePricingEstimateRequest request,
            String profileCode);
}