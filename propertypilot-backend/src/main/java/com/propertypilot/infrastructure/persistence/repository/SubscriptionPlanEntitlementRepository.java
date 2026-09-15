package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanEntitlementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubscriptionPlanEntitlementRepository
        extends JpaRepository<
                SubscriptionPlanEntitlementEntity,
                UUID> {
}