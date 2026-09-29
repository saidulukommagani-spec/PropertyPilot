package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionPlanVersionRepository
        extends JpaRepository<
                SubscriptionPlanVersionEntity,
                UUID> {

    List<SubscriptionPlanVersionEntity>
    findByStatus(
            String status);

    boolean existsByPlanVersionId(
            UUID planVersionId);

    Optional<SubscriptionPlanVersionEntity>
    findBySubscriptionPlan_SubscriptionPlanIdAndStatus(
            UUID subscriptionPlanId,
            String status);

    Optional<SubscriptionPlanVersionEntity>
    findTopBySubscriptionPlan_SubscriptionPlanIdOrderByVersionNumberDesc(
            UUID subscriptionPlanId);
}