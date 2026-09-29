package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.SubscriptionNotificationResponse;
import com.propertypilot.application.service.SubscriptionNotificationService;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionNotificationEntity;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionNotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SubscriptionNotificationServiceImpl
        implements SubscriptionNotificationService {

    private final SubscriptionNotificationRepository
            notificationRepository;

    public SubscriptionNotificationServiceImpl(
            SubscriptionNotificationRepository notificationRepository) {

        this.notificationRepository =
                notificationRepository;
    }

    @Override
    public List<SubscriptionNotificationResponse>
    getNotifications(
            UUID customerSubscriptionId) {

        return notificationRepository
                .findByCustomerSubscription_CustomerSubscriptionId(
                        customerSubscriptionId)
                .stream()
                .map(this::map)
                .toList();
    }

    private SubscriptionNotificationResponse map(
            SubscriptionNotificationEntity entity) {

        SubscriptionNotificationResponse response =
                new SubscriptionNotificationResponse();

        response.setNotificationId(
                entity.getNotificationId());

        response.setCustomerSubscriptionId(
                entity.getCustomerSubscription()
                        .getCustomerSubscriptionId());

        response.setNotificationType(
                entity.getNotificationType());

        response.setMessage(
                entity.getMessage());

        response.setStatus(
                entity.getStatus());

        response.setSentDate(
                entity.getSentDate());

        return response;
    }
}