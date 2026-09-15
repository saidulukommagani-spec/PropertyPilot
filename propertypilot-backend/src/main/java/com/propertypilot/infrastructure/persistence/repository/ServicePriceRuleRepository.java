package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.ServicePriceRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServicePriceRuleRepository
        extends JpaRepository<ServicePriceRuleEntity, UUID> {
}