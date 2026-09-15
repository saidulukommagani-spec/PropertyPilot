package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateCustomerSubscriptionAddOnRequest;
import com.propertypilot.application.dto.CustomerSubscriptionAddOnResponse;
import com.propertypilot.application.service.CustomerSubscriptionAddOnService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customer-subscriptions")
public class CustomerSubscriptionAddOnController {

    private final CustomerSubscriptionAddOnService
            customerSubscriptionAddOnService;

    public CustomerSubscriptionAddOnController(
            CustomerSubscriptionAddOnService
                    customerSubscriptionAddOnService) {

        this.customerSubscriptionAddOnService =
                customerSubscriptionAddOnService;
    }

    @PostMapping("/{customerSubscriptionId}/add-ons")
    public CustomerSubscriptionAddOnResponse
    createAddOn(
            @PathVariable UUID customerSubscriptionId,
            @RequestBody
            CreateCustomerSubscriptionAddOnRequest request) {

        return customerSubscriptionAddOnService
                .createAddOn(
                        customerSubscriptionId,
                        request);
    }

    @GetMapping("/{customerSubscriptionId}/add-ons")
    public List<CustomerSubscriptionAddOnResponse>
    getAddOns(
            @PathVariable UUID customerSubscriptionId) {

        return customerSubscriptionAddOnService
                .getSubscriptionAddOns(
                        customerSubscriptionId);
    }

    @GetMapping("/add-ons/{addOnId}")
    public CustomerSubscriptionAddOnResponse
    getAddOn(
            @PathVariable UUID addOnId) {

        return customerSubscriptionAddOnService
                .getAddOn(addOnId);
    }

    @DeleteMapping("/add-ons/{addOnId}")
    public void deleteAddOn(
            @PathVariable UUID addOnId) {

        customerSubscriptionAddOnService
                .deleteAddOn(addOnId);
    }
}