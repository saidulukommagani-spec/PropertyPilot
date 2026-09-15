package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.PricingDiscountApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PricingDiscountApplicationRepository
        extends JpaRepository<PricingDiscountApplicationEntity, UUID> {
}