package com.propertypilot.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DocumentTypeResponse {

    private UUID documentTypeId;

    private String propertyCategory;

    private String documentCode;

    private String documentName;

    private Boolean mandatory;
}