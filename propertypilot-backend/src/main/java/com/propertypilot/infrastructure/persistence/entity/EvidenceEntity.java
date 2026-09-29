package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

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
            nullable = false
    )
    private String fileUrl;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "media_type")
    private String mediaType;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longitude")
    private BigDecimal longitude;

    @Column(name = "captured_at")
    private OffsetDateTime capturedAt;

    @Column(name = "checksum_sha256")
    private String checksumSha256;

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

    public Long getVersion() {
        return version;
    }

    public void setVersion(
            Long version) {
        this.version = version;
    }
}