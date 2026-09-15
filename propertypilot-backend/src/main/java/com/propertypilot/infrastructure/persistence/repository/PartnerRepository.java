package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.PartnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PartnerRepository extends JpaRepository<PartnerEntity, UUID> {

    Optional<PartnerEntity> findByUser_UserId(UUID userId);

}