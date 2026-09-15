package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.VisitEvidenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VisitEvidenceRepository
        extends JpaRepository<VisitEvidenceEntity, UUID> {

    List<VisitEvidenceEntity>
    findByVisit_VisitId(UUID visitId);

}