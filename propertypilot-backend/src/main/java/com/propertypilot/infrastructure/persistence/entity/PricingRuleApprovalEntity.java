package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "pricing_rule_approvals")
public class PricingRuleApprovalEntity
        extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "pricing_rule_approval_id",
            nullable = false,
            updatable = false)
    private UUID pricingRuleApprovalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "price_rule_id",
            nullable = false)
    private PricingRuleEntity pricingRule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "requested_by",
            nullable = false)
    private UserEntity requestedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decided_by")
    private UserEntity decidedBy;

    @Column(
            name = "status",
            nullable = false,
            length = 20)
    private String status;

    @Column(name = "decision_notes")
    private String decisionNotes;

    @Column(
            name = "requested_at",
            nullable = false)
    private OffsetDateTime requestedAt;

    @Column(name = "decided_at")
    private OffsetDateTime decidedAt;

    @Version
    @Column(
            name = "version",
            nullable = false)
    private Long version = 0L;

    public UUID getPricingRuleApprovalId() {
        return pricingRuleApprovalId;
    }

    public void setPricingRuleApprovalId(
            UUID pricingRuleApprovalId) {
        this.pricingRuleApprovalId = pricingRuleApprovalId;
    }

    public PricingRuleEntity getPricingRule() {
        return pricingRule;
    }

    public void setPricingRule(
            PricingRuleEntity pricingRule) {
        this.pricingRule = pricingRule;
    }

    public UserEntity getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(
            UserEntity requestedBy) {
        this.requestedBy = requestedBy;
    }

    public UserEntity getDecidedBy() {
        return decidedBy;
    }

    public void setDecidedBy(
            UserEntity decidedBy) {
        this.decidedBy = decidedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {
        this.status = status;
    }

    public String getDecisionNotes() {
        return decisionNotes;
    }

    public void setDecisionNotes(
            String decisionNotes) {
        this.decisionNotes = decisionNotes;
    }

    public OffsetDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(
            OffsetDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public OffsetDateTime getDecidedAt() {
        return decidedAt;
    }

    public void setDecidedAt(
            OffsetDateTime decidedAt) {
        this.decidedAt = decidedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(
            Long version) {
        this.version = version;
    }
}