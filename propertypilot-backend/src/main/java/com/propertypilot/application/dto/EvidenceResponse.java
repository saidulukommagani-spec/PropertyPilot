package com.propertypilot.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.math.BigDecimal;
import java.util.List;

public class EvidenceResponse {

    private UUID evidenceId;

    private UUID visitId;

    private UUID serviceRequestId;

    private UUID evidenceTypeId;

    private String evidenceTypeCode;

    private String evidenceTypeName;

    private String fileUrl;

    private String mediaType;

    private String checksumSha256;

    private OffsetDateTime capturedAt;

    private BigDecimal latitude;

    private BigDecimal longitude;
    private String fileName;
    private String remarks;
    private String deviceInfo;
    private String validationStatus;
    private String validationComment;
    private String uploadedByName;
    private String validatedByName;
    private OffsetDateTime validatedAt;
    private String propertyName;
    private String customerName;
    private String serviceName;
    private String agentName;
    private String visitStatus;
    private OffsetDateTime visitScheduledAt;
    private OffsetDateTime createdAt;
    private List<EvidenceValidationHistoryResponse> validationHistory;

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

    public UUID getServiceRequestId() { return serviceRequestId; }

    public void setServiceRequestId(UUID serviceRequestId) { this.serviceRequestId = serviceRequestId; }

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

    public BigDecimal getLatitude() { return latitude; }

    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }

    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }
    public String getValidationStatus() { return validationStatus; }
    public void setValidationStatus(String validationStatus) { this.validationStatus = validationStatus; }
    public String getValidationComment() { return validationComment; }
    public void setValidationComment(String validationComment) { this.validationComment = validationComment; }
    public String getUploadedByName() { return uploadedByName; }
    public void setUploadedByName(String uploadedByName) { this.uploadedByName = uploadedByName; }
    public String getValidatedByName() { return validatedByName; }
    public void setValidatedByName(String validatedByName) { this.validatedByName = validatedByName; }
    public OffsetDateTime getValidatedAt() { return validatedAt; }
    public void setValidatedAt(OffsetDateTime validatedAt) { this.validatedAt = validatedAt; }
    public String getPropertyName() { return propertyName; }
    public void setPropertyName(String propertyName) { this.propertyName = propertyName; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }
    public String getVisitStatus() { return visitStatus; }
    public void setVisitStatus(String visitStatus) { this.visitStatus = visitStatus; }
    public OffsetDateTime getVisitScheduledAt() { return visitScheduledAt; }
    public void setVisitScheduledAt(OffsetDateTime visitScheduledAt) { this.visitScheduledAt = visitScheduledAt; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public List<EvidenceValidationHistoryResponse> getValidationHistory() { return validationHistory; }
    public void setValidationHistory(List<EvidenceValidationHistoryResponse> validationHistory) { this.validationHistory = validationHistory; }
}
