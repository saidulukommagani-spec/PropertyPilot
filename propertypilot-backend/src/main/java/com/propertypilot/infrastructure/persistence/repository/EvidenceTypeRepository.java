package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.EvidenceTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvidenceTypeRepository
        extends JpaRepository<EvidenceTypeEntity, UUID> {

    Optional<EvidenceTypeEntity>
    findByCode(String code);

    List<EvidenceTypeEntity>
    findByActiveFlagTrue();

    boolean existsByCode(
            String code);
}