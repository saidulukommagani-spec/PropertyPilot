package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.SubscriptionPauseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubscriptionPauseRepository
        extends JpaRepository<
        SubscriptionPauseEntity,
        UUID> {
}