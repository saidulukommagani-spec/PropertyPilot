package com.propertypilot.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class EvidenceResponse {

    private UUID evidenceId;

    private UUID visitId;

    private UUID evidenceTypeId;

    private String evidenceTypeCode;

    private String evidenceTypeName;

    private String fileUrl;

    private String mediaType;

    private String checksumSha256;

    private OffsetDateTime capturedAt;

    public UUID getEvidenceId() {
        return evidenceId;
    }

    public void setEvidenceId(
            UUID evidenceId) {
        this.evidenceId = evidenceId;
    }

    public UUID getVisitId() {
        return visitId;
    }

    public void setVisitId(
            UUID visitId) {
        this.visitId = visitId;
    }

    public UUID getEvidenceTypeId() {
        return evidenceTypeId;
    }

    public void setEvidenceTypeId(
            UUID evidenceTypeId) {
        this.evidenceTypeId = evidenceTypeId;
    }

    public String getEvidenceTypeCode() {
        return evidenceTypeCode;
    }

    public void setEvidenceTypeCode(
            String evidenceTypeCode) {
        this.evidenceTypeCode = evidenceTypeCode;
    }

    public String getEvidenceTypeName() {
        return evidenceTypeName;
    }

    public void setEvidenceTypeName(
            String evidenceTypeName) {
        this.evidenceTypeName = evidenceTypeName;
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

    public OffsetDateTime getCapturedAt() {
        return capturedAt;
    }

    public void setCapturedAt(
            OffsetDateTime capturedAt) {
        this.capturedAt = capturedAt;
    }
}