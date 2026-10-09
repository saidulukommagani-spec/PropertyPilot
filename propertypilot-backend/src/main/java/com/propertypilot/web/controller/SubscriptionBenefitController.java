package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateSubscriptionBenefitRequest;
import com.propertypilot.application.dto.SubscriptionBenefitResponse;
import com.propertypilot.application.dto.UpdateSubscriptionBenefitRequest;
import com.propertypilot.application.service.SubscriptionBenefitService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/subscription-benefits")
public class SubscriptionBenefitController {

    private final SubscriptionBenefitService
            subscriptionBenefitService;

    public SubscriptionBenefitController(
            SubscriptionBenefitService
                    subscriptionBenefitService) {

        this.subscriptionBenefitService =
                subscriptionBenefitService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public SubscriptionBenefitResponse createBenefit(
            @RequestBody
            CreateSubscriptionBenefitRequest request) {

        return subscriptionBenefitService
                .createBenefit(request);
    }

    @GetMapping("/{benefitId}")
    public SubscriptionBenefitResponse getBenefit(
            @PathVariable UUID benefitId) {

        return subscriptionBenefitService
                .getBenefit(benefitId);
    }

    @GetMapping
    public List<SubscriptionBenefitResponse>
    getAllBenefits() {

        return subscriptionBenefitService
                .getAllBenefits();
    }

    @PutMapping("/{benefitId}")
    @PreAuthorize("hasRole('ADMIN')")
    public SubscriptionBenefitResponse updateBenefit(
            @PathVariable UUID benefitId,
            @RequestBody
            UpdateSubscriptionBenefitRequest request) {

        return subscriptionBenefitService
                .updateBenefit(
                        benefitId,
                        request);
    }
}
