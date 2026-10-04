package com.propertypilot.web.controller;

import com.propertypilot.application.dto.ConsumptionResponse;
import com.propertypilot.application.dto.CreateConsumptionRequest;
import com.propertypilot.application.dto.SubscriptionUsageResponse;
import com.propertypilot.application.service.SubscriptionEntitlementConsumptionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscription-consumptions")
@Tag(
        name = "Subscription Entitlement Consumption",
        description =
                "APIs for entitlement consumption and usage tracking"
)
public class SubscriptionEntitlementConsumptionController {

    private final SubscriptionEntitlementConsumptionService
            consumptionService;

    public SubscriptionEntitlementConsumptionController(
            SubscriptionEntitlementConsumptionService
                    consumptionService) {

        this.consumptionService =
                consumptionService;
    }

    @Operation(
            summary = "Consume entitlement",
            description =
                    "Consumes subscription entitlement for a service"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Consumption successful"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed")
    })
    @PostMapping
    public ResponseEntity<ConsumptionResponse>
    consume(
            @Valid
            @RequestBody
            CreateConsumptionRequest request) {

        return ResponseEntity.ok(
                consumptionService.consume(
                        request));
    }

    @Operation(
            summary = "Get usage",
            description =
                    "Returns entitlement usage for subscription"
    )
    @GetMapping("/{customerSubscriptionId}/usage")
    public ResponseEntity<
            List<SubscriptionUsageResponse>>
    getUsage(
            @PathVariable
            UUID customerSubscriptionId) {

        return ResponseEntity.ok(
                consumptionService.getUsage(
                        customerSubscriptionId));
    }

    @Operation(
            summary = "Get remaining entitlement",
            description =
                    "Returns remaining entitlement quantities"
    )
    @GetMapping("/{customerSubscriptionId}/remaining")
    public ResponseEntity<
            List<SubscriptionUsageResponse>>
    getRemaining(
            @PathVariable
            UUID customerSubscriptionId) {

        return ResponseEntity.ok(
                consumptionService.getRemaining(
                        customerSubscriptionId));
    }
}