package com.propertypilot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(
        description =
                "Request used to create a report"
)
public class CreateReportRequest {

    @Schema(
            description =
                    "Associated service request id"
    )
    private UUID serviceRequestId;

    @Schema(
            description =
                    "Report type"
    )
    private String reportType;

    @Schema(
            description =
                    "Report summary"
    )
    private String summary;

    @Schema(
            description =
                    "Generated document url"
    )
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