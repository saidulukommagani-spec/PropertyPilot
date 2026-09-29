package com.propertypilot.application.service;

import com.propertypilot.application.dto.SubscriptionNotificationResponse;

import java.util.List;
import java.util.UUID;

public interface SubscriptionNotificationService {

    List<SubscriptionNotificationResponse>
    getNotifications(
            UUID customerSubscriptionId);
}