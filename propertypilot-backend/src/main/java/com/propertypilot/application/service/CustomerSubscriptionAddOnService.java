package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreateCustomerSubscriptionAddOnRequest;
import com.propertypilot.application.dto.CustomerSubscriptionAddOnResponse;
import com.propertypilot.application.dto.SubscriptionAddOnCatalogResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerSubscriptionAddOnService {

    List<SubscriptionAddOnCatalogResponse> getAvailableAddOns();

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
