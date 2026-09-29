package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreatePricingEstimateRequest;
import com.propertypilot.application.dto.PricingBreakdownResponse;
import com.propertypilot.application.dto.PricingCalculationResponse;
import com.propertypilot.application.service.PricingCalculatorService;
import com.propertypilot.application.service.PricingRuleEngineService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PricingCalculatorServiceImpl
        implements PricingCalculatorService {

    private final PricingRuleEngineService
            pricingRuleEngineService;

    public PricingCalculatorServiceImpl(
            PricingRuleEngineService pricingRuleEngineService) {

        this.pricingRuleEngineService =
                pricingRuleEngineService;
    }

    @Override
    public PricingCalculationResponse calculate(
            CreatePricingEstimateRequest request) {

        PricingBreakdownResponse standardBreakdown =
                pricingRuleEngineService
                        .calculateBreakdown(
                                request,
                                "STANDARD");

        PricingBreakdownResponse customerBreakdown =
                pricingRuleEngineService
                        .calculateBreakdown(
                                request,
                                "CUSTOMER");

        BigDecimal standardPrice =
                standardBreakdown.getTotalAmount();

        BigDecimal customerPrice =
                customerBreakdown.getTotalAmount();

        BigDecimal savings =
                standardPrice.subtract(
                        customerPrice);

        PricingCalculationResponse response =
                new PricingCalculationResponse();

        response.setStandardPrice(
                standardPrice);

        response.setCustomerPrice(
                customerPrice);

        response.setSavings(
                savings);

        response.setSavingsMessage(
                "You saved ₹"
                        + savings.setScale(
                        2,
                        RoundingMode.HALF_UP)
                        + " through PropertyPilot pricing.");

        return response;
    }
}