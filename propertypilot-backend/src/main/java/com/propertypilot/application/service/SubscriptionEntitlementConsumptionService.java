package com.propertypilot.application.service;

import com.propertypilot.application.dto.ConsumptionResponse;
import com.propertypilot.application.dto.CreateConsumptionRequest;
import com.propertypilot.application.dto.SubscriptionUsageResponse;

import java.util.List;
import java.util.UUID;

public interface SubscriptionEntitlementConsumptionService {

    ConsumptionResponse consume(
            CreateConsumptionRequest request);

    List<SubscriptionUsageResponse> getUsage(
            UUID customerSubscriptionId);

    List<SubscriptionUsageResponse> getRemaining(
            UUID customerSubscriptionId);
}