package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreatePricingRuleRequest;
import com.propertypilot.application.dto.PricingRuleResponse;
import com.propertypilot.application.dto.UpdatePricingRuleRequest;
import com.propertypilot.application.service.PricingRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/pricing-rules")
@Tag(
        name = "Pricing Rule Management",
        description = "APIs for managing pricing rules"
)
public class PricingRuleController {

    private final PricingRuleService pricingRuleService;

    public PricingRuleController(
            PricingRuleService pricingRuleService) {

        this.pricingRuleService =
                pricingRuleService;
    }

    @Operation(
            summary = "Create pricing rule",
            description = "Creates a new pricing rule"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pricing rule created successfully"
            )
    })
    @PostMapping
    public ResponseEntity<PricingRuleResponse>
    createRule(
            @Valid
            @RequestBody
            CreatePricingRuleRequest request) {

        return ResponseEntity.ok(
                pricingRuleService
                        .createRule(request));
    }

    @Operation(
            summary = "Get all pricing rules",
            description = "Returns all pricing rules"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pricing rules retrieved successfully"
            )
    })
    @GetMapping
    public ResponseEntity<
            List<PricingRuleResponse>>
    getAllRules() {

        return ResponseEntity.ok(
                pricingRuleService
                        .getAllRules());
    }

    @Operation(
            summary = "Get pricing rule",
            description = "Returns pricing rule by ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pricing rule retrieved successfully"
            )
    })
    @GetMapping("/{ruleId}")
    public ResponseEntity<PricingRuleResponse>
    getRule(
            @Parameter(
                    description = "Pricing Rule ID",
                    required = true
            )
            @PathVariable UUID ruleId) {

        return ResponseEntity.ok(
                pricingRuleService
                        .getRule(ruleId));
    }

    @Operation(
            summary = "Get pricing rules by service",
            description = "Returns pricing rules for a service"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pricing rules retrieved successfully"
            )
    })
    @GetMapping("/service/{serviceId}")
    public ResponseEntity<
            List<PricingRuleResponse>>
    getRulesByService(

            @Parameter(
                    description = "Service ID",
                    required = true
            )
            @PathVariable UUID serviceId) {

        return ResponseEntity.ok(
                pricingRuleService
                        .getRulesByService(
                                serviceId));
    }

    @Operation(
            summary = "Update pricing rule",
            description = "Updates an existing pricing rule"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pricing rule updated successfully"
            )
    })
    @PutMapping("/{ruleId}")
    public ResponseEntity<PricingRuleResponse>
    updateRule(

            @Parameter(
                    description = "Pricing Rule ID",
                    required = true
            )
            @PathVariable UUID ruleId,

            @Valid
            @RequestBody
            UpdatePricingRuleRequest request) {

        return ResponseEntity.ok(
                pricingRuleService
                        .updateRule(
                                ruleId,
                                request));
    }
}
