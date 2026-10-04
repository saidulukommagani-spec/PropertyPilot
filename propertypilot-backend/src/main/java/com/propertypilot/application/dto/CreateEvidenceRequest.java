package com.propertypilot.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateEvidenceRequest {

    @NotNull(message = "Visit ID is required")
    private UUID visitId;

    @NotNull(message = "Evidence Type ID is required")
    private UUID evidenceTypeId;

    @NotBlank(message = "File URL is required")
    private String fileUrl;

    @NotBlank(message = "Media Type is required")
    private String mediaType;

    private String checksumSha256;

    public UUID getVisitId() {
        return visitId;
    }

    public void setVisitId(UUID visitId) {
        this.visitId = visitId;
    }

    public UUID getEvidenceTypeId() {
        return evidenceTypeId;
    }

    public void setEvidenceTypeId(
            UUID evidenceTypeId) {
        this.evidenceTypeId = evidenceTypeId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(
            String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(
            String mediaType) {
        this.mediaType = mediaType;
    }

    public String getChecksumSha256() {
        return checksumSha256;
    }

    public void setChecksumSha256(
            String checksumSha256) {
        this.checksumSha256 = checksumSha256;
    }
}