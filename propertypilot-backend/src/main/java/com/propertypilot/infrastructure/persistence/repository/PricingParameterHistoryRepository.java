package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.PricingParameterHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PricingParameterHistoryRepository
        extends JpaRepository<
                PricingParameterHistoryEntity,
                UUID> {
}