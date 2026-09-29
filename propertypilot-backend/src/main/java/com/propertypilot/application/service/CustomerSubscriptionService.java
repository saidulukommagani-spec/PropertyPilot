package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreateCustomerSubscriptionRequest;
import com.propertypilot.application.dto.CustomerSubscriptionResponse;
import com.propertypilot.application.dto.UpdateCustomerSubscriptionRequest;

import java.util.List;
import java.util.UUID;

public interface CustomerSubscriptionService {

    CustomerSubscriptionResponse createSubscription(
            UUID customerId,
            CreateCustomerSubscriptionRequest request);

    CustomerSubscriptionResponse getSubscription(
            UUID customerSubscriptionId);

    List<CustomerSubscriptionResponse> getCustomerSubscriptions(
            UUID customerId);

    CustomerSubscriptionResponse updateSubscription(
            UUID customerSubscriptionId,
            UpdateCustomerSubscriptionRequest request);

            List<CustomerSubscriptionResponse>
getExpiringSubscriptions(
        Integer days);
}