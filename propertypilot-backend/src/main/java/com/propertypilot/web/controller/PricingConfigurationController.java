package com.propertypilot.web.controller;

import com.propertypilot.application.dto.PricingParameterResponse;
import com.propertypilot.application.dto.UpdatePricingParameterRequest;
import com.propertypilot.application.dto.PricingCouponRequest;
import com.propertypilot.application.dto.ClusterDiscountRuleRequest;
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
import com.propertypilot.infrastructure.persistence.entity.CouponEntity;
import com.propertypilot.infrastructure.persistence.entity.ClusterRuleEntity;

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

    @PutMapping("/profiles/{profileCode}/active")
    public ResponseEntity<Void> setProfileActive(
            @PathVariable String profileCode,
            @RequestParam boolean active) {
        pricingConfigurationService.setProfileActive(profileCode, active);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/coupons")
    public ResponseEntity<CouponEntity> createCoupon(@Valid @RequestBody PricingCouponRequest request) {
        return ResponseEntity.status(201).body(pricingConfigurationService.createCoupon(request));
    }

    @PutMapping("/coupons/{couponId}")
    public ResponseEntity<CouponEntity> updateCoupon(@PathVariable UUID couponId,
            @Valid @RequestBody PricingCouponRequest request) {
        return ResponseEntity.ok(pricingConfigurationService.updateCoupon(couponId, request));
    }

    @PostMapping("/cluster-rules")
    public ResponseEntity<ClusterRuleEntity> createClusterRule(@Valid @RequestBody ClusterDiscountRuleRequest request) {
        return ResponseEntity.status(201).body(pricingConfigurationService.createClusterRule(request));
    }

    @PutMapping("/cluster-rules/{ruleId}")
    public ResponseEntity<ClusterRuleEntity> updateClusterRule(@PathVariable UUID ruleId,
            @Valid @RequestBody ClusterDiscountRuleRequest request) {
        return ResponseEntity.ok(pricingConfigurationService.updateClusterRule(ruleId, request));
    }
}
