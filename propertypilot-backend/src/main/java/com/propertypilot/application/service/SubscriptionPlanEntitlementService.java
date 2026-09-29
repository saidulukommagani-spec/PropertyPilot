package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreateSubscriptionPlanEntitlementRequest;
import com.propertypilot.application.dto.SubscriptionPlanEntitlementResponse;
import com.propertypilot.application.dto.UpdateSubscriptionPlanEntitlementRequest;

import java.util.List;
import java.util.UUID;

public interface SubscriptionPlanEntitlementService {

    SubscriptionPlanEntitlementResponse createEntitlement(
            CreateSubscriptionPlanEntitlementRequest request);

    SubscriptionPlanEntitlementResponse getEntitlement(
            UUID entitlementId);

    List<SubscriptionPlanEntitlementResponse>
    getAllEntitlements();

    SubscriptionPlanEntitlementResponse updateEntitlement(
            UUID entitlementId,
            UpdateSubscriptionPlanEntitlementRequest request);
}