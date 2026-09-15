package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

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

    @ManyToOne(fetch = FetchType.LAZY,
            optional = false)
    @JoinColumn(
            name = "visit_id",
            nullable = false
    )
    private VisitEntity visit;

    @Column(
            name = "evidence_type",
            nullable = false
    )
    private String evidenceType;

    @Column(
            name = "file_url",
            nullable = false
    )
    private String fileUrl;

    @Column(name = "media_type")
    private String mediaType;

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

    public VisitEntity getVisit() {
        return visit;
    }

    public void setVisit(VisitEntity visit) {
        this.visit = visit;
    }

    public String getEvidenceType() {
        return evidenceType;
    }

    public void setEvidenceType(String evidenceType) {
        this.evidenceType = evidenceType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
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

    public void setVersion(Long version) {
        this.version = version;
    }
}