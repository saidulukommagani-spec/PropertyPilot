package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "reports")
public class ReportEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "report_id",
            nullable = false,
            updatable = false
    )
    private UUID reportId;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "service_request_id",
            nullable = false
    )
    private ServiceRequestEntity serviceRequest;

    @Column(
            name = "report_type",
            nullable = false
    )
    private String reportType;

    @Column(
            name = "report_status",
            nullable = false
    )
    private String reportStatus;

    @Column(name = "summary")
    private String summary;

    @Column(name = "document_url")
    private String documentUrl;

    @Column(name = "submitted_at")
    private OffsetDateTime submittedAt;

    @Column(name = "approved_at")
    private OffsetDateTime approvedAt;

    @Column(name = "delivered_at")
    private OffsetDateTime deliveredAt;

    @Version
    @Column(name = "version")
    private Long version = 0L;

    public UUID getReportId() {
        return reportId;
    }

    public void setReportId(UUID reportId) {
        this.reportId = reportId;
    }

    public ServiceRequestEntity getServiceRequest() {
        return serviceRequest;
    }

    public void setServiceRequest(
            ServiceRequestEntity serviceRequest) {
        this.serviceRequest = serviceRequest;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(
            Long version) {
        this.version = version;
    }
}