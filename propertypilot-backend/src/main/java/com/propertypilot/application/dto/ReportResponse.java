package com.propertypilot.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ReportResponse {

    private UUID reportId;

    private UUID serviceRequestId;

    private String reportType;

    private String reportStatus;

    private String summary;

    private String documentUrl;

    private OffsetDateTime submittedAt;

    private OffsetDateTime approvedAt;

    private OffsetDateTime deliveredAt;

    public UUID getReportId() {
        return reportId;
    }

    public void setReportId(UUID reportId) {
        this.reportId = reportId;
    }

    public UUID getServiceRequestId() {
        return serviceRequestId;
    }

    public void setServiceRequestId(
            UUID serviceRequestId) {
        this.serviceRequestId = serviceRequestId;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(
            String reportType) {
        this.reportType = reportType;
    }

    public String getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(
            String reportStatus) {
        this.reportStatus = reportStatus;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(
            String summary) {
        this.summary = summary;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(
            String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public OffsetDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(
            OffsetDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public OffsetDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(
            OffsetDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public OffsetDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(
            OffsetDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
}