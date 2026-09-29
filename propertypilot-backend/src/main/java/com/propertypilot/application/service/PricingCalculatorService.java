package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreatePricingEstimateRequest;
import com.propertypilot.application.dto.PricingCalculationResponse;

public interface PricingCalculatorService {

    PricingCalculationResponse calculate(
            CreatePricingEstimateRequest request);
}