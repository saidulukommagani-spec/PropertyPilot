package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "lead_activities")
public class LeadActivityEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "lead_activity_id")
    private UUID leadActivityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false)
    private LeadEntity lead;

    @Column(name = "activity_type", nullable = false, length = 30)
    private String activityType;

    @Column(name = "subject", nullable = false, length = 200)
    private String subject;

    @Column(name = "details")
    private String details;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private UserEntity performedBy;

    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;

    @Column(name = "next_action_at")
    private OffsetDateTime nextActionAt;

    @Column(name = "outcome", length = 100)
    private String outcome;

    @Version
    @Column(name = "version")
    private Long version;

    public UUID getLeadActivityId() {
        return leadActivityId;
    }

    public void setLeadActivityId(UUID leadActivityId) {
        this.leadActivityId = leadActivityId;
    }

    public LeadEntity getLead() {
        return lead;
    }

    public void setLead(LeadEntity lead) {
        this.lead = lead;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public UserEntity getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(UserEntity performedBy) {
        this.performedBy = performedBy;
    }

    public OffsetDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(OffsetDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public OffsetDateTime getNextActionAt() {
        return nextActionAt;
    }

    public void setNextActionAt(OffsetDateTime nextActionAt) {
        this.nextActionAt = nextActionAt;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}