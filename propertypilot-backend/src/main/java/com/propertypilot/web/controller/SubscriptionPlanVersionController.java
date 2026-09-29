package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateSubscriptionPlanVersionRequest;
import com.propertypilot.application.dto.SubscriptionPlanVersionResponse;
import com.propertypilot.application.dto.UpdateSubscriptionPlanVersionRequest;
import com.propertypilot.application.service.SubscriptionPlanVersionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscription-plan-versions")
public class SubscriptionPlanVersionController {

    private final SubscriptionPlanVersionService
            subscriptionPlanVersionService;

    public SubscriptionPlanVersionController(
            SubscriptionPlanVersionService
                    subscriptionPlanVersionService) {

        this.subscriptionPlanVersionService =
                subscriptionPlanVersionService;
    }

    @PostMapping
    public SubscriptionPlanVersionResponse createPlanVersion(
            @RequestBody
            CreateSubscriptionPlanVersionRequest request) {

        return subscriptionPlanVersionService
                .createPlanVersion(request);
    }

    @GetMapping("/{planVersionId}")
    public SubscriptionPlanVersionResponse getPlanVersion(
            @PathVariable UUID planVersionId) {

        return subscriptionPlanVersionService
                .getPlanVersion(planVersionId);
    }

    @GetMapping
    public List<SubscriptionPlanVersionResponse>
    getAllPlanVersions() {

        return subscriptionPlanVersionService
                .getAllPlanVersions();
    }

    @PutMapping("/{planVersionId}")
    public SubscriptionPlanVersionResponse updatePlanVersion(
            @PathVariable UUID planVersionId,
            @RequestBody
            UpdateSubscriptionPlanVersionRequest request) {

        return subscriptionPlanVersionService
                .updatePlanVersion(
                        planVersionId,
                        request);
    }
}