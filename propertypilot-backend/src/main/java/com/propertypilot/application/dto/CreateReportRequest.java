package com.propertypilot.application.dto;

import java.util.UUID;

public class CreateReportRequest {

    private UUID serviceRequestId;

    private String reportType;

    private String summary;

    private String documentUrl;

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
}