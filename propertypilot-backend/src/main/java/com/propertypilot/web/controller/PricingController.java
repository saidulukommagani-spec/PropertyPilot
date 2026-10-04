package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreatePricingEstimateRequest;
import com.propertypilot.application.dto.PricingCalculationResponse;
import com.propertypilot.application.service.PricingCalculatorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pricing")
@Tag(
        name = "Pricing",
        description = "Pricing calculation APIs"
)
public class PricingController {

    private final PricingCalculatorService
            pricingCalculatorService;

    public PricingController(
            PricingCalculatorService pricingCalculatorService) {

        this.pricingCalculatorService =
                pricingCalculatorService;
    }

    @PostMapping("/calculate")
    @Operation(
            summary = "Calculate Service Pricing",
            description =
                    "Calculates standard price, customer price, discounts and savings."
    )
    public ResponseEntity<PricingCalculationResponse>
    calculatePricing(

            @Valid
            @RequestBody
            CreatePricingEstimateRequest request) {

        PricingCalculationResponse response =
                pricingCalculatorService
                        .calculate(request);

        return ResponseEntity.ok(
                response);
    }
}