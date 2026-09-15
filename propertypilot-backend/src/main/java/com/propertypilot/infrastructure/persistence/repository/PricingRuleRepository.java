package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.PricingRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PricingRuleRepository
        extends JpaRepository<PricingRuleEntity, UUID> {

    List<PricingRuleEntity>
    findByStatus(String status);

    List<PricingRuleEntity>
    findByServiceServiceId(UUID serviceId);

    List<PricingRuleEntity>
    findByServiceServiceIdAndStatus(
            UUID serviceId,
            String status);
}