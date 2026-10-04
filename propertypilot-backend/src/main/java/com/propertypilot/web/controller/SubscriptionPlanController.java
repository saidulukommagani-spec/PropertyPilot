package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateSubscriptionPlanRequest;
import com.propertypilot.application.dto.SubscriptionPlanResponse;
import com.propertypilot.application.dto.UpdateSubscriptionPlanRequest;
import com.propertypilot.application.service.SubscriptionPlanService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscription-plans")
@Tag(
        name = "Subscription Plan Management",
        description = "APIs for managing subscription plans"
)
public class SubscriptionPlanController {

    private final SubscriptionPlanService
            subscriptionPlanService;

    public SubscriptionPlanController(
            SubscriptionPlanService
                    subscriptionPlanService) {

        this.subscriptionPlanService =
                subscriptionPlanService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create subscription plan",
            description =
                    "Creates a new subscription plan"
    )
    public SubscriptionPlanResponse createPlan(
            @Valid
            @RequestBody
            CreateSubscriptionPlanRequest request) {

        return subscriptionPlanService
                .createPlan(request);
    }

    @GetMapping("/{planId}")
    @Operation(
            summary = "Get subscription plan",
            description =
                    "Returns subscription plan by id"
    )
    public SubscriptionPlanResponse getPlan(
            @PathVariable UUID planId) {

        return subscriptionPlanService
                .getPlan(planId);
    }

    @GetMapping
    @Operation(
            summary = "Get all subscription plans",
            description =
                    "Returns all subscription plans"
    )
    public List<SubscriptionPlanResponse>
    getAllPlans() {

        return subscriptionPlanService
                .getAllPlans();
    }

    @PutMapping("/{planId}")
    @Operation(
            summary = "Update subscription plan",
            description =
                    "Updates an existing subscription plan"
    )
    public SubscriptionPlanResponse updatePlan(
            @PathVariable UUID planId,
            @Valid
            @RequestBody
            UpdateSubscriptionPlanRequest request) {

        return subscriptionPlanService
                .updatePlan(
                        planId,
                        request);
    }
}