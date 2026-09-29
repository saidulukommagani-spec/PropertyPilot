package com.propertypilot.web.controller;

import com.propertypilot.application.dto.PricingParameterResponse;
import com.propertypilot.application.dto.UpdatePricingParameterRequest;
import com.propertypilot.application.service.PricingConfigurationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/pricing")
@PreAuthorize("hasRole('ADMIN')")
public class PricingConfigurationController {

    private final PricingConfigurationService
            pricingConfigurationService;

    public PricingConfigurationController(
            PricingConfigurationService pricingConfigurationService) {

        this.pricingConfigurationService =
                pricingConfigurationService;
    }

    @GetMapping("/profiles/{profileCode}/parameters")
    public ResponseEntity<
            List<PricingParameterResponse>>
    getProfileParameters(
            @PathVariable String profileCode) {

        return ResponseEntity.ok(
                pricingConfigurationService
                        .getProfileParameters(
                                profileCode));
    }

    @PutMapping("/parameters/{parameterId}")
    public ResponseEntity<
            PricingParameterResponse>
    updateParameter(
            @PathVariable UUID parameterId,
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