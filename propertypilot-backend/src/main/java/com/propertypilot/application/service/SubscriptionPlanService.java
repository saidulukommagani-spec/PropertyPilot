package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreateSubscriptionPlanRequest;
import com.propertypilot.application.dto.SubscriptionPlanResponse;
import com.propertypilot.application.dto.UpdateSubscriptionPlanRequest;

import java.util.List;
import java.util.UUID;

public interface SubscriptionPlanService {

    SubscriptionPlanResponse createPlan(
            CreateSubscriptionPlanRequest request);

    SubscriptionPlanResponse getPlan(
            UUID planId);

    List<SubscriptionPlanResponse> getAllPlans();

    SubscriptionPlanResponse updatePlan(
            UUID planId,
            UpdateSubscriptionPlanRequest request);
}