package com.propertypilot.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DocumentVersionResponse {

    private UUID versionId;

    private UUID documentId;

    private Integer versionNumber;

    private String fileName;

    private String mimeType;

    private Long fileSize;

    private Boolean currentVersion;
}