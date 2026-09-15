package com.propertypilot.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DocumentResponse {

    private UUID documentId;

    private UUID propertyId;

    private String documentName;

    private String documentType;

    private String status;
}