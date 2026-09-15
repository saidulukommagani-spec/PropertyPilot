package com.propertypilot.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentCreateRequest {

    @NotBlank
    private String documentName;

    @NotBlank
    private String documentType;
}