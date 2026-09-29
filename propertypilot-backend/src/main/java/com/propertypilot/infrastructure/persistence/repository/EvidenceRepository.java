package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.EvidenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EvidenceRepository
        extends JpaRepository<EvidenceEntity, UUID> {

    List<EvidenceEntity>
    findByVisit_VisitId(UUID visitId);

    List<EvidenceEntity>
findByEvidenceType_Code(
        String code);
}