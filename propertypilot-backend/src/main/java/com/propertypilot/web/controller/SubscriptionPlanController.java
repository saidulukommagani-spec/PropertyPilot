package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateSubscriptionPlanRequest;
import com.propertypilot.application.dto.SubscriptionPlanResponse;
import com.propertypilot.application.dto.UpdateSubscriptionPlanRequest;
import com.propertypilot.application.service.SubscriptionPlanService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscription-plans")
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
    public SubscriptionPlanResponse createPlan(
            @RequestBody
            CreateSubscriptionPlanRequest request) {

        return subscriptionPlanService
                .createPlan(request);
    }

    @GetMapping("/{planId}")
    public SubscriptionPlanResponse getPlan(
            @PathVariable UUID planId) {

        return subscriptionPlanService
                .getPlan(planId);
    }

    @GetMapping
    public List<SubscriptionPlanResponse>
    getAllPlans() {

        return subscriptionPlanService
                .getAllPlans();
    }

    @PutMapping("/{planId}")
    public SubscriptionPlanResponse updatePlan(
            @PathVariable UUID planId,
            @RequestBody
            UpdateSubscriptionPlanRequest request) {

        return subscriptionPlanService
                .updatePlan(
                        planId,
                        request);
    }
}