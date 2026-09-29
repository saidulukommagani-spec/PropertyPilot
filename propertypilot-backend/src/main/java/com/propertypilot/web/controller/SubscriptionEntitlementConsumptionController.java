package com.propertypilot.web.controller;

import com.propertypilot.application.dto.ConsumptionResponse;
import com.propertypilot.application.dto.CreateConsumptionRequest;
import com.propertypilot.application.dto.SubscriptionUsageResponse;
import com.propertypilot.application.service.SubscriptionEntitlementConsumptionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscription-consumptions")
public class SubscriptionEntitlementConsumptionController {

    private final SubscriptionEntitlementConsumptionService
            consumptionService;

    public SubscriptionEntitlementConsumptionController(
            SubscriptionEntitlementConsumptionService consumptionService) {

        this.consumptionService =
                consumptionService;
    }

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