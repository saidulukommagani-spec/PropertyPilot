package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreateSubscriptionRenewalRequest;
import com.propertypilot.application.dto.SubscriptionRenewalResponse;

import java.util.List;
import java.util.UUID;

public interface SubscriptionRenewalService {

    SubscriptionRenewalResponse renewSubscription(
            CreateSubscriptionRenewalRequest request);

    SubscriptionRenewalResponse getRenewal(
            UUID subscriptionRenewalId);

    List<SubscriptionRenewalResponse>
    getRenewalsBySubscription(
            UUID customerSubscriptionId);
}