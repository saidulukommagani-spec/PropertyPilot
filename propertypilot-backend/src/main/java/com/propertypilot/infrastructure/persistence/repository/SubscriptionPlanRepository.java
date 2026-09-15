package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionPlanRepository
        extends JpaRepository<
                SubscriptionPlanEntity,
                UUID> {

    Optional<SubscriptionPlanEntity>
    findByPlanCode(
            String planCode);

    List<SubscriptionPlanEntity>
    findByStatus(
            String status);

    boolean existsByPlanCode(
            String planCode);
}