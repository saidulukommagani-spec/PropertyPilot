package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubscriptionPlanVersionRepository
        extends JpaRepository<
                SubscriptionPlanVersionEntity,
                UUID> {
}