package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "monitoring_alerts")
public class MonitoringAlertEntity
        extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "monitoring_alert_id")
    private UUID monitoringAlertId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monitoring_schedule_id")
    private MonitoringScheduleEntity monitoringSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "property_id",
            nullable = false
    )
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_request_id")
    private ServiceRequestEntity serviceRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private ReportEntity report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommended_service_id")
    private ServiceEntity recommendedService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acknowledged_by")
    private UserEntity acknowledgedBy;

    @Column(
            name = "alert_type",
            nullable = false
    )
    private String alertType;

    @Column(
            name = "severity",
            nullable = false
    )
    private String severity;

    @Column(
            name = "title",
            nullable = false
    )
    private String title;

    @Column(name = "description")
    private String description;

    @Column(
            name = "detected_at",
            nullable = false
    )
    private OffsetDateTime detectedAt;

    @Column(
            name = "status",
            nullable = false
    )
    private String status;

    @Column(name = "acknowledged_at")
    private OffsetDateTime acknowledgedAt;

    @Column(name = "resolved_at")
    private OffsetDateTime resolvedAt;

    @Version
    @Column(name = "version")
    private Long version;

    public UUID getMonitoringAlertId() {
        return monitoringAlertId;
    }

    public void setMonitoringAlertId(
            UUID monitoringAlertId) {
        this.monitoringAlertId =
                monitoringAlertId;
    }

    public MonitoringScheduleEntity
    getMonitoringSchedule() {
        return monitoringSchedule;
    }

    public void setMonitoringSchedule(
            MonitoringScheduleEntity
                    monitoringSchedule) {
        this.monitoringSchedule =
                monitoringSchedule;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(
            Property property) {
        this.property = property;
    }

    public ServiceRequestEntity
    getServiceRequest() {
        return serviceRequest;
    }

    public void setServiceRequest(
            ServiceRequestEntity
                    serviceRequest) {
        this.serviceRequest =
                serviceRequest;
    }

    public ReportEntity getReport() {
        return report;
    }

    public void setReport(
            ReportEntity report) {
        this.report = report;
    }

    public ServiceEntity
    getRecommendedService() {
        return recommendedService;
    }

    public void setRecommendedService(
            ServiceEntity recommendedService) {
        this.recommendedService =
                recommendedService;
    }

    public UserEntity getAcknowledgedBy() {
        return acknowledgedBy;
    }

    public void setAcknowledgedBy(
            UserEntity acknowledgedBy) {
        this.acknowledgedBy =
                acknowledgedBy;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(
            String alertType) {
        this.alertType = alertType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(
            String severity) {
        this.severity = severity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(
            String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {
        this.description = description;
    }

    public OffsetDateTime getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(
            OffsetDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {
        this.status = status;
    }

    public OffsetDateTime
    getAcknowledgedAt() {
        return acknowledgedAt;
    }

    public void setAcknowledgedAt(
            OffsetDateTime acknowledgedAt) {
        this.acknowledgedAt =
                acknowledgedAt;
    }

    public OffsetDateTime
    getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(
            OffsetDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(
            Long version) {
        this.version = version;
    }
}