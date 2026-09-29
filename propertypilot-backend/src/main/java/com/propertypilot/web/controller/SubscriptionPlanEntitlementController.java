package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateSubscriptionPlanEntitlementRequest;
import com.propertypilot.application.dto.SubscriptionPlanEntitlementResponse;
import com.propertypilot.application.dto.UpdateSubscriptionPlanEntitlementRequest;
import com.propertypilot.application.service.SubscriptionPlanEntitlementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscription-plan-entitlements")
public class SubscriptionPlanEntitlementController {

    private final SubscriptionPlanEntitlementService
            entitlementService;

    public SubscriptionPlanEntitlementController(
            SubscriptionPlanEntitlementService entitlementService) {

        this.entitlementService =
                entitlementService;
    }

    @PostMapping
    public SubscriptionPlanEntitlementResponse
    createEntitlement(
            @RequestBody
            CreateSubscriptionPlanEntitlementRequest request) {

        return entitlementService
                .createEntitlement(request);
    }

    @GetMapping("/{entitlementId}")
    public SubscriptionPlanEntitlementResponse
    getEntitlement(
            @PathVariable UUID entitlementId) {

        return entitlementService
                .getEntitlement(entitlementId);
    }

    @GetMapping
    public List<SubscriptionPlanEntitlementResponse>
    getAllEntitlements() {

        return entitlementService
                .getAllEntitlements();
    }

    @PutMapping("/{entitlementId}")
    public SubscriptionPlanEntitlementResponse
    updateEntitlement(
            @PathVariable UUID entitlementId,
            @RequestBody
            UpdateSubscriptionPlanEntitlementRequest request) {

        return entitlementService
                .updateEntitlement(
                        entitlementId,
                        request);
    }
}