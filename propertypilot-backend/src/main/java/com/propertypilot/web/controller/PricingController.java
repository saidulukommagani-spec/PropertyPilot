package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreatePricingEstimateRequest;
import com.propertypilot.application.dto.PricingCalculationResponse;
import com.propertypilot.application.service.PricingCalculatorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pricing")
public class PricingController {

    private final PricingCalculatorService
            pricingCalculatorService;

    public PricingController(
            PricingCalculatorService pricingCalculatorService) {

        this.pricingCalculatorService =
                pricingCalculatorService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<PricingCalculationResponse>
    calculatePricing(
            @Valid
            @RequestBody
            CreatePricingEstimateRequest request) {

        PricingCalculationResponse response =
                pricingCalculatorService
                        .calculate(request);

        return ResponseEntity.ok(response);
    }
}