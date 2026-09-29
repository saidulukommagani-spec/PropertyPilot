package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "deal_activities")
public class DealActivityEntity
        extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "deal_activity_id")
    private UUID dealActivityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "deal_id",
            nullable = false)
    private DealEntity deal;

    @Column(
            name = "activity_type",
            nullable = false)
    private String activityType;

    @Column(
            name = "subject",
            nullable = false)
    private String subject;

    @Column(name = "details")
    private String details;

    @Column(name = "outcome")
    private String outcome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private UserEntity performedBy;

    @Column(name = "occurred_at")
    private OffsetDateTime occurredAt;

    @Column(name = "next_action_at")
    private OffsetDateTime nextActionAt;

    @Version
    @Column(name = "version")
    private Long version;

    public UUID getDealActivityId() {
        return dealActivityId;
    }

    public void setDealActivityId(
            UUID dealActivityId) {
        this.dealActivityId =
                dealActivityId;
    }

    public DealEntity getDeal() {
        return deal;
    }

    public void setDeal(
            DealEntity deal) {
        this.deal = deal;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(
            String activityType) {
        this.activityType =
                activityType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(
            String subject) {
        this.subject =
                subject;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(
            String details) {
        this.details =
                details;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(
            String outcome) {
        this.outcome =
                outcome;
    }

    public UserEntity getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(
            UserEntity performedBy) {
        this.performedBy =
                performedBy;
    }

    public OffsetDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(
            OffsetDateTime occurredAt) {
        this.occurredAt =
                occurredAt;
    }

    public OffsetDateTime getNextActionAt() {
        return nextActionAt;
    }

    public void setNextActionAt(
            OffsetDateTime nextActionAt) {
        this.nextActionAt =
                nextActionAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(
            Long version) {
        this.version =
                version;
    }
}