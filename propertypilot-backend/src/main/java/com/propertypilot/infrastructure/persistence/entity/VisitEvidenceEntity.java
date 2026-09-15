package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "visit_evidence")
public class VisitEvidenceEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "evidence_id",
            nullable = false,
            updatable = false
    )
    private UUID evidenceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "visit_id",
            nullable = false
    )
    private VisitEntity visit;

    @Column(
            name = "evidence_type",
            nullable = false,
            length = 30
    )
    private String evidenceType;

    @Column(
            name = "file_name",
            nullable = false
    )
    private String fileName;

    @Column(
            name = "file_url",
            nullable = false
    )
    private String fileUrl;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "captured_at")
    private OffsetDateTime capturedAt;

    @Version
    @Column(
            name = "version",
            nullable = false
    )
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public OffsetDateTime getCapturedAt() {
        return capturedAt;
    }

    public void setCapturedAt(OffsetDateTime capturedAt) {
        this.capturedAt = capturedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}