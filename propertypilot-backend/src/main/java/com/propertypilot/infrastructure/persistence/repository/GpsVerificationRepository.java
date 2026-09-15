package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.GpsVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GpsVerificationRepository
        extends JpaRepository<GpsVerificationEntity, UUID> {

    List<GpsVerificationEntity>
    findByVisit_VisitId(UUID visitId);

    List<GpsVerificationEntity>
    findByValidationStatus(String status);
    Optional<GpsVerificationEntity>
findTopByVisit_VisitIdOrderByCapturedAtDesc(
        UUID visitId);
}