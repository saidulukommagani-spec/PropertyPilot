package com.propertypilot.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class DocumentChecklistResponse {

    private UUID propertyId;

    private String propertyCategory;

    private Integer completionPercentage;

    private Integer totalDocuments;

    private Integer uploadedDocuments;

    private List<DocumentChecklistItemResponse> documents;
}