package com.propertypilot.application.service.impl;



import com.propertypilot.infrastructure.persistence.entity.Document;
import com.propertypilot.infrastructure.persistence.entity.DocumentType;
import com.propertypilot.infrastructure.persistence.entity.DocumentVersion;
import com.propertypilot.infrastructure.persistence.entity.Property;


import com.propertypilot.infrastructure.persistence.repository.DocumentRepository;
import com.propertypilot.infrastructure.persistence.repository.DocumentServiceCatalogRepository;
import com.propertypilot.infrastructure.persistence.repository.DocumentTypeRepository;
import com.propertypilot.infrastructure.persistence.repository.DocumentVersionRepository;
import com.propertypilot.infrastructure.persistence.repository.PropertyRepository;
import com.propertypilot.security.SecurityService;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceImplTest {
    @Mock DocumentRepository documentRepository;
    @Mock DocumentServiceCatalogRepository documentServiceCatalogRepository;
    @Mock DocumentTypeRepository documentTypeRepository;
    @Mock DocumentVersionRepository documentVersionRepository;
    @Mock PropertyRepository propertyRepository;
    @Mock SecurityService securityService;
    @InjectMocks DocumentServiceImpl service;

    @Test
    void checklistCountsUploadedTypesOnceAndExcludesArchivedAndEmptyDocuments() {
        Property property = property();
        Document uploaded = document("SALE_DEED", "ACTIVE");
        Document duplicate = document("SALE_DEED", "ACTIVE");
        Document empty = document("APPROVED_LAYOUT_PLAN", "ACTIVE");
        Document archived = document("GIFT_DEED", "ARCHIVED");
        when(documentRepository.findByProperty_PropertyId(property.getPropertyId()))
                .thenReturn(List.of(uploaded, duplicate, empty, archived));
        for (Document document : List.of(uploaded, duplicate)) {
            when(documentVersionRepository.findByDocument_DocumentIdAndCurrentVersionTrue(document.getDocumentId()))
                    .thenReturn(Optional.of(new DocumentVersion()));
        }
        when(documentTypeRepository.findByPropertyCategoryInAndActiveTrue(List.of("COMMON", "PLOT")))
                .thenReturn(List.of(type("SALE_DEED"), type("APPROVED_LAYOUT_PLAN"), type("GIFT_DEED")));

        var response = service.getDocumentChecklist(property.getPropertyId());

        assertThat(response.getPropertyId()).isEqualTo(property.getPropertyId());
        assertThat(response.getPropertyCategory()).isEqualTo("PLOT");
        assertThat(response.getTotalDocuments()).isEqualTo(3);
        assertThat(response.getUploadedDocuments()).isEqualTo(1);
        assertThat(response.getCompletionPercentage()).isEqualTo(33);
        assertThat(response.getDocuments()).extracting("documentStatus")
                .containsExactly("UPLOADED", "MISSING", "MISSING");
        verify(securityService).validatePropertyOwnership(property);
        verify(documentVersionRepository, never())
                .findByDocument_DocumentIdAndCurrentVersionTrue(archived.getDocumentId());
    }

    @Test
    void emptyChecklistHasZeroCompletion() {
        Property property = property();
        var response = service.getDocumentChecklist(property.getPropertyId());
        assertThat(response.getDocuments()).isEmpty();
        assertThat(response.getCompletionPercentage()).isZero();
        assertThat(response.getUploadedDocuments()).isZero();
    }

    @Test
    void checklistRejectsUnauthorizedAccessBeforeReadingDocuments() {
        Property property = property();
        doThrow(new AccessDeniedException("Forbidden")).when(securityService).validatePropertyOwnership(property);
        assertThatThrownBy(() -> service.getDocumentChecklist(property.getPropertyId()))
                .isInstanceOf(AccessDeniedException.class);
        verifyNoInteractions(documentRepository, documentTypeRepository, documentVersionRepository);
    }

    private Property property() {
        Property property = new Property();
        property.setPropertyId(UUID.randomUUID());
        property.setPropertyType("plot");
        when(propertyRepository.findById(property.getPropertyId())).thenReturn(Optional.of(property));
        return property;
    }

    private Document document(String code, String status) {
        Document document = new Document();
        document.setDocumentId(UUID.randomUUID());
        document.setDocumentType(code);
        document.setStatus(status);
        return document;
    }

    private DocumentType type(String code) {
        DocumentType type = new DocumentType();
        type.setDocumentCode(code);
        type.setDocumentName(code);
        type.setMandatory(true);
        return type;
    }
}
