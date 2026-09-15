package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository
        extends JpaRepository<Document, UUID> {


    List<Document> findByProperty_PropertyId(
            UUID propertyId);
}
