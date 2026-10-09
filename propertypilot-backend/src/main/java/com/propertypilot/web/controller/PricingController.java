package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreatePricingEstimateRequest;
import com.propertypilot.application.dto.PricingEstimateResponse;
import com.propertypilot.application.service.PricingEngineService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/pricing")
@Tag(
        name = "Pricing",
        description = "Pricing calculation APIs"
)
public class PricingController {

    private final PricingEngineService pricingEngineService;

    public PricingController(
            PricingEngineService pricingEngineService) {
        this.pricingEngineService = pricingEngineService;
    }

    @PostMapping("/calculate")
    @Operation(
            summary = "Calculate Service Pricing",
            description =
                    "Calculates standard price, customer price, discounts and savings."
    )
    @PreAuthorize("hasAnyRole('ADMIN','OPERATIONS')")
    public ResponseEntity<PricingEstimateResponse>
    calculatePricing(

            @Valid
            @RequestBody
            CreatePricingEstimateRequest request) {

        PricingEstimateResponse response = pricingEngineService.calculateEstimate(request);

        return ResponseEntity.ok(
                response);
    }
}
