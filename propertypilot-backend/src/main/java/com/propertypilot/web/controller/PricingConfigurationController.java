package com.propertypilot.web.controller;

import com.propertypilot.application.dto.PricingParameterResponse;
import com.propertypilot.application.dto.UpdatePricingParameterRequest;
import com.propertypilot.application.service.PricingConfigurationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/pricing")
@PreAuthorize("hasRole('ADMIN')")
@Tag(
        name = "Pricing Configuration",
        description =
                "Administrative APIs for pricing profile management"
)
public class PricingConfigurationController {

    private final PricingConfigurationService
            pricingConfigurationService;

    public PricingConfigurationController(
            PricingConfigurationService
                    pricingConfigurationService) {

        this.pricingConfigurationService =
                pricingConfigurationService;
    }

    @Operation(
            summary =
                    "Get pricing profile parameters",
            description =
                    "Returns all pricing parameters configured for a pricing profile"
    )
    @GetMapping("/profiles/{profileCode}/parameters")
    public ResponseEntity<
            List<PricingParameterResponse>>
    getProfileParameters(

            @Parameter(
                    description =
                            "Pricing profile code"
            )
            @PathVariable
            @NotBlank
            String profileCode) {

        return ResponseEntity.ok(
                pricingConfigurationService
                        .getProfileParameters(
                                profileCode));
    }

    @Operation(
            summary =
                    "Update pricing parameter",
            description =
                    "Updates a pricing parameter value"
    )
    @PutMapping("/parameters/{parameterId}")
    public ResponseEntity<
            PricingParameterResponse>
    updateParameter(

            @Parameter(
                    description =
                            "Pricing Parameter Id"
            )
            @PathVariable
            UUID parameterId,

            @Valid
            @RequestBody
            UpdatePricingParameterRequest request) {

        return ResponseEntity.ok(
                pricingConfigurationService
                        .updateParameter(
                                parameterId,
                                request));
    }
}