package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "evidence")
public class EvidenceEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "evidence_id",
            nullable = false,
            updatable = false
    )
    private UUID evidenceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_request_id")
    private ServiceRequestEntity serviceRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id")
    private VisitEntity visit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evidence_type_id")
    private EvidenceTypeEntity evidenceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "captured_by")
    private UserEntity capturedBy;

    @Column(name = "file_name")
    private String fileName;

    @Column(
            name = "file_url",
            nullable = false,
            columnDefinition = "text"
    )
    private String fileUrl;

    @Column(name = "remarks", columnDefinition = "text")
    private String remarks;

    @Column(name = "media_type", length = 100)
    private String mediaType;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longitude")
    private BigDecimal longitude;

    @Column(name = "captured_at")
    private OffsetDateTime capturedAt;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "checksum_sha256")
    private String checksumSha256;

    @Column(name = "device_info", length = 500)
    private String deviceInfo;
    @Column(name = "validation_status", nullable = false, length = 32)
    private String validationStatus = "PENDING";
    @Column(name = "validation_comment", length = 2000)
    private String validationComment;
    @Column(name = "validated_by")
    private UUID validatedBy;
    @Column(name = "validated_at")
    private OffsetDateTime validatedAt;

    @Version
    @Column(name = "version")
    private Long version = 0L;

    public UUID getEvidenceId() {
        return evidenceId;
    }

    public void setEvidenceId(UUID evidenceId) {
        this.evidenceId = evidenceId;
    }

    public ServiceRequestEntity getServiceRequest() {
        return serviceRequest;
    }

    public void setServiceRequest(
            ServiceRequestEntity serviceRequest) {
        this.serviceRequest = serviceRequest;
    }

    public VisitEntity getVisit() {
        return visit;
    }

    public void setVisit(
            VisitEntity visit) {
        this.visit = visit;
    }

    public EvidenceTypeEntity getEvidenceType() {
        return evidenceType;
    }

    public void setEvidenceType(
            EvidenceTypeEntity evidenceType) {
        this.evidenceType = evidenceType;
    }

    public UserEntity getCapturedBy() {
        return capturedBy;
    }

    public void setCapturedBy(
            UserEntity capturedBy) {
        this.capturedBy = capturedBy;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(
            String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(
            String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(
            String remarks) {
        this.remarks = remarks;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(
            String mediaType) {
        this.mediaType = mediaType;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(
            BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(
            BigDecimal longitude) {
        this.longitude = longitude;
    }

    public OffsetDateTime getCapturedAt() {
        return capturedAt;
    }

    public void setCapturedAt(
            OffsetDateTime capturedAt) {
        this.capturedAt = capturedAt;
    }

    public String getChecksumSha256() {
        return checksumSha256;
    }

    public void setChecksumSha256(
            String checksumSha256) {
        this.checksumSha256 = checksumSha256;
    }

    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }
    public String getValidationStatus() { return validationStatus; }
    public void setValidationStatus(String validationStatus) { this.validationStatus = validationStatus; }
    public String getValidationComment() { return validationComment; }
    public void setValidationComment(String validationComment) { this.validationComment = validationComment; }
    public UUID getValidatedBy() { return validatedBy; }
    public void setValidatedBy(UUID validatedBy) { this.validatedBy = validatedBy; }
    public OffsetDateTime getValidatedAt() { return validatedAt; }
    public void setValidatedAt(OffsetDateTime validatedAt) { this.validatedAt = validatedAt; }

    public Long getVersion() {
        return version;
    }

    public void setVersion(
            Long version) {
        this.version = version;
    }
}
