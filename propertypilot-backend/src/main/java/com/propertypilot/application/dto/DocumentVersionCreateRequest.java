package com.propertypilot.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentVersionCreateRequest {

    @NotBlank
    private String fileName;

    @NotBlank
    private String filePath;

    private String mimeType;

    private Long fileSize;
}