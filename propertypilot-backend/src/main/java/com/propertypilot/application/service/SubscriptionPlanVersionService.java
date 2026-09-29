package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreateSubscriptionPlanVersionRequest;
import com.propertypilot.application.dto.SubscriptionPlanVersionResponse;
import com.propertypilot.application.dto.UpdateSubscriptionPlanVersionRequest;

import java.util.List;
import java.util.UUID;

public interface SubscriptionPlanVersionService {

    SubscriptionPlanVersionResponse createPlanVersion(
            CreateSubscriptionPlanVersionRequest request);

    SubscriptionPlanVersionResponse getPlanVersion(
            UUID planVersionId);

    List<SubscriptionPlanVersionResponse> getAllPlanVersions();

    SubscriptionPlanVersionResponse updatePlanVersion(
            UUID planVersionId,
            UpdateSubscriptionPlanVersionRequest request);
}