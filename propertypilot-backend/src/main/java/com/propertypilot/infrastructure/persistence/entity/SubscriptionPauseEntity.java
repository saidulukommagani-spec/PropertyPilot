package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "subscription_pauses")
public class SubscriptionPauseEntity
        extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "subscription_pause_id")
    private UUID subscriptionPauseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "customer_subscription_id",
            nullable = false)
    private CustomerSubscriptionEntity customerSubscription;

    @Column(name = "pause_start")
    private LocalDate pauseStart;

    @Column(name = "pause_end")
    private LocalDate pauseEnd;

    @Column(name = "reason")
    private String reason;

    @Column(name = "status")
    private String status;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Version
    @Column(name = "version")
    private Long version;

    public UUID getSubscriptionPauseId() {
        return subscriptionPauseId;
    }

    public void setSubscriptionPauseId(UUID subscriptionPauseId) {
        this.subscriptionPauseId = subscriptionPauseId;
    }

    public CustomerSubscriptionEntity getCustomerSubscription() {
        return customerSubscription;
    }

    public void setCustomerSubscription(
            CustomerSubscriptionEntity customerSubscription) {
        this.customerSubscription = customerSubscription;
    }

    public LocalDate getPauseStart() {
        return pauseStart;
    }

    public void setPauseStart(LocalDate pauseStart) {
        this.pauseStart = pauseStart;
    }

    public LocalDate getPauseEnd() {
        return pauseEnd;
    }

    public void setPauseEnd(LocalDate pauseEnd) {
        this.pauseEnd = pauseEnd;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(UUID approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}