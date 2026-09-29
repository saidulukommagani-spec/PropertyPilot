package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreateSubscriptionBenefitRequest;
import com.propertypilot.application.dto.SubscriptionBenefitResponse;
import com.propertypilot.application.dto.UpdateSubscriptionBenefitRequest;

import java.util.List;
import java.util.UUID;

public interface SubscriptionBenefitService {

    SubscriptionBenefitResponse createBenefit(
            CreateSubscriptionBenefitRequest request);

    SubscriptionBenefitResponse getBenefit(
            UUID benefitId);

    List<SubscriptionBenefitResponse> getAllBenefits();

    SubscriptionBenefitResponse updateBenefit(
            UUID benefitId,
            UpdateSubscriptionBenefitRequest request);
}