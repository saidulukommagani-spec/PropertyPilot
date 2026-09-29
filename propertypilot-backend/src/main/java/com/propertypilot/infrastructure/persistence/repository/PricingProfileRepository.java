package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.PricingProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PricingProfileRepository
        extends JpaRepository<PricingProfileEntity, UUID> {

    Optional<PricingProfileEntity>
    findByProfileCode(String profileCode);
}