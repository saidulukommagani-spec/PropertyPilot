package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.ServiceEligibilityRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServiceEligibilityRuleRepository
        extends JpaRepository<ServiceEligibilityRuleEntity, UUID> {
}