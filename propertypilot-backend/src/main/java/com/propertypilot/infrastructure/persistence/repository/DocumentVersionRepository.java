package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentVersionRepository
        extends JpaRepository<DocumentVersion, UUID> {

    List<DocumentVersion>
    findByDocument_DocumentIdOrderByVersionNumberDesc(
            UUID documentId);

    Optional<DocumentVersion>
    findByDocument_DocumentIdAndCurrentVersionTrue(
            UUID documentId);
}