package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.SubscriptionLifecycleEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubscriptionLifecycleEventRepository
        extends JpaRepository<
        SubscriptionLifecycleEventEntity,
        UUID> {
}