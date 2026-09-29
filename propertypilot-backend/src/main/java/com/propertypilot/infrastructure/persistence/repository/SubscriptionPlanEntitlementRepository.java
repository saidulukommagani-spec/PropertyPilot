package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanEntitlementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionPlanEntitlementRepository
        extends JpaRepository<
                SubscriptionPlanEntitlementEntity,
                UUID> {

    List<SubscriptionPlanEntitlementEntity>
    findByPlanVersion_PlanVersionId(
            UUID planVersionId);

    Optional<SubscriptionPlanEntitlementEntity>
    findByPlanVersion_PlanVersionIdAndService_ServiceId(
            UUID planVersionId,
            UUID serviceId);
}