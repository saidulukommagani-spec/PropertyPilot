package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.EvidenceValidationHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface EvidenceValidationHistoryRepository extends JpaRepository<EvidenceValidationHistoryEntity, UUID> {
    List<EvidenceValidationHistoryEntity> findByEvidence_EvidenceIdOrderByOccurredAtDesc(UUID evidenceId);
}
