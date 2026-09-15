package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.PricingQuoteLineItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PricingQuoteLineItemRepository
        extends JpaRepository<PricingQuoteLineItemEntity, UUID> {
}