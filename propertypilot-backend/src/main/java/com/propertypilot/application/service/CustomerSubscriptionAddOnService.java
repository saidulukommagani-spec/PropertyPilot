package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreateCustomerSubscriptionAddOnRequest;
import com.propertypilot.application.dto.CustomerSubscriptionAddOnResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerSubscriptionAddOnService {

    CustomerSubscriptionAddOnResponse createAddOn(
            UUID customerSubscriptionId,
            CreateCustomerSubscriptionAddOnRequest request);

    CustomerSubscriptionAddOnResponse getAddOn(
            UUID addOnId);

    List<CustomerSubscriptionAddOnResponse>
    getSubscriptionAddOns(
            UUID customerSubscriptionId);

    void deleteAddOn(
            UUID addOnId);
}