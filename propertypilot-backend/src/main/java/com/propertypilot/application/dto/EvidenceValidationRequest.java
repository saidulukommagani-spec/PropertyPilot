package com.propertypilot.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EvidenceValidationRequest(
        @NotBlank @Pattern(regexp = "APPROVED|REJECTED|REUPLOAD_REQUESTED") String status,
        @NotBlank @Size(max = 2000) String comment) { }
