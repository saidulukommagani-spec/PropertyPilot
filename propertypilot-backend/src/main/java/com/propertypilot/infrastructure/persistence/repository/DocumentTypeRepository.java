package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import java.util.UUID;


public interface DocumentTypeRepository
        extends JpaRepository<DocumentType, UUID> {

    List<DocumentType> findByPropertyCategoryAndActiveTrue(
            String propertyCategory);

    List<DocumentType> findByPropertyCategoryInAndActiveTrue(
            List<String> categories);

        Optional<DocumentType> findByDocumentCodeAndActiveTrue(
        String documentCode);
}