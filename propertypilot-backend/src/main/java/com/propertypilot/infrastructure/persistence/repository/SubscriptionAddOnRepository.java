package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.SubscriptionAddOnEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubscriptionAddOnRepository
        extends JpaRepository<
                SubscriptionAddOnEntity,
                UUID> {
}