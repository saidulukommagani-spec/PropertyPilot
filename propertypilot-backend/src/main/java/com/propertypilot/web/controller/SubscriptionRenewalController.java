package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateSubscriptionRenewalRequest;
import com.propertypilot.application.dto.SubscriptionRenewalResponse;
import com.propertypilot.application.service.SubscriptionRenewalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscription-renewals")
public class SubscriptionRenewalController {

    private final SubscriptionRenewalService
            renewalService;

    public SubscriptionRenewalController(
            SubscriptionRenewalService renewalService) {

        this.renewalService =
                renewalService;
    }

    @PostMapping
    public ResponseEntity<SubscriptionRenewalResponse>
    renewSubscription(
            @Valid
            @RequestBody
            CreateSubscriptionRenewalRequest request) {

        return ResponseEntity.ok(
                renewalService.renewSubscription(
                        request));
    }

    @GetMapping("/{subscriptionRenewalId}")
    public ResponseEntity<SubscriptionRenewalResponse>
    getRenewal(
            @PathVariable
            UUID subscriptionRenewalId) {

        return ResponseEntity.ok(
                renewalService.getRenewal(
                        subscriptionRenewalId));
    }

    @GetMapping("/subscription/{customerSubscriptionId}")
    public ResponseEntity<
            List<SubscriptionRenewalResponse>>
    getRenewalsBySubscription(
            @PathVariable
            UUID customerSubscriptionId) {

        return ResponseEntity.ok(
                renewalService.getRenewalsBySubscription(
                        customerSubscriptionId));
    }
}