package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.SubscriptionNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubscriptionNotificationRepository
        extends JpaRepository<
                SubscriptionNotificationEntity,
                UUID> {

    List<SubscriptionNotificationEntity>
    findByCustomerSubscription_CustomerSubscriptionId(
            UUID customerSubscriptionId);

    List<SubscriptionNotificationEntity>
    findByStatus(
            String status);

    List<SubscriptionNotificationEntity>
    findByNotificationType(
            String notificationType);
}