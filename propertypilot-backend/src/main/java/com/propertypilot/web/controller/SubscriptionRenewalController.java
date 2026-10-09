package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateSubscriptionRenewalRequest;
import com.propertypilot.application.dto.SubscriptionRenewalResponse;
import com.propertypilot.application.dto.InvoiceResponse;
import com.propertypilot.application.dto.SubscriptionRenewalInvoiceRequest;
import com.propertypilot.application.service.SubscriptionRenewalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscription-renewals")
@PreAuthorize("hasAnyRole('ADMIN','OPERATIONS','CUSTOMER')")
@Tag(
        name = "Subscription Renewal Management",
        description = "APIs for subscription renewals"
)
public class SubscriptionRenewalController {

    private final SubscriptionRenewalService
            renewalService;

    public SubscriptionRenewalController(
            SubscriptionRenewalService renewalService) {

        this.renewalService =
                renewalService;
    }

    @PostMapping("/invoice")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create or retrieve the invoice for a subscription renewal period")
    public InvoiceResponse createRenewalInvoice(
            @Valid @RequestBody SubscriptionRenewalInvoiceRequest request) {
        return renewalService.createRenewalInvoice(request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Renew subscription",
            description =
                    "Creates a new subscription renewal"
    )
    public SubscriptionRenewalResponse
    renewSubscription(
            @Valid
            @RequestBody
            CreateSubscriptionRenewalRequest request) {

        return renewalService
                .renewSubscription(request);
    }

    @GetMapping("/{subscriptionRenewalId}")
    @Operation(
            summary = "Get renewal",
            description =
                    "Returns renewal by identifier"
    )
    public SubscriptionRenewalResponse
    getRenewal(
            @PathVariable
            UUID subscriptionRenewalId) {

        return renewalService
                .getRenewal(
                        subscriptionRenewalId);
    }

    @GetMapping(
            "/subscription/{customerSubscriptionId}")
    @Operation(
            summary = "Get renewals by subscription",
            description =
                    "Returns all renewals for a customer subscription"
    )
    public List<SubscriptionRenewalResponse>
    getRenewalsBySubscription(
            @PathVariable
            UUID customerSubscriptionId) {

        return renewalService
                .getRenewalsBySubscription(
                        customerSubscriptionId);
    }
}
