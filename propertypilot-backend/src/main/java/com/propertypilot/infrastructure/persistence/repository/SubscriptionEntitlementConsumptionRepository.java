package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.SubscriptionEntitlementConsumptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubscriptionEntitlementConsumptionRepository
        extends JpaRepository<
                SubscriptionEntitlementConsumptionEntity,
                UUID> {
}