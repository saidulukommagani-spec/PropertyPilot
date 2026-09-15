package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.DocumentTypeResponse;
import com.propertypilot.application.service.DocumentTypeService;
import com.propertypilot.infrastructure.persistence.repository.DocumentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentTypeServiceImpl
        implements DocumentTypeService {

    private final DocumentTypeRepository
            documentTypeRepository;

    @Override
    public List<DocumentTypeResponse> getByPropertyCategory(
            String propertyCategory) {

        return documentTypeRepository
                .findByPropertyCategoryAndActiveTrue(
                        propertyCategory.toUpperCase())
                .stream()
                .map(documentType -> {

                    DocumentTypeResponse response =
                            new DocumentTypeResponse();

                    response.setDocumentTypeId(
                            documentType.getDocumentTypeId());

                    response.setPropertyCategory(
                            documentType.getPropertyCategory());

                    response.setDocumentCode(
                            documentType.getDocumentCode());

                    response.setDocumentName(
                            documentType.getDocumentName());

                    response.setMandatory(
                            documentType.getMandatory());

                    return response;
                })
                .toList();
    }
}