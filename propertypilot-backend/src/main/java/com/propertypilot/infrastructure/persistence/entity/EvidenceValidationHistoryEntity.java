package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "evidence_validation_history")
public class EvidenceValidationHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "history_id", nullable = false, updatable = false)
    private UUID historyId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evidence_id", nullable = false)
    private EvidenceEntity evidence;
    @Column(name = "status", nullable = false, length = 32)
    private String status;
    @Column(name = "comment", nullable = false, length = 2000)
    private String comment;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;
    public UUID getHistoryId() { return historyId; }
    public void setHistoryId(UUID historyId) { this.historyId = historyId; }
    public EvidenceEntity getEvidence() { return evidence; }
    public void setEvidence(EvidenceEntity evidence) { this.evidence = evidence; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public OffsetDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(OffsetDateTime occurredAt) { this.occurredAt = occurredAt; }
}
