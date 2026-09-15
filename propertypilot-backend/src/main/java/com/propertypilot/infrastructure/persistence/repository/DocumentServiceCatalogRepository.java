package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.DocumentServiceCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentServiceCatalogRepository
        extends JpaRepository<DocumentServiceCatalog, UUID> {

    Optional<DocumentServiceCatalog>
    findByDocumentCode(String documentCode);
}