package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscription_lifecycle_events")
public class SubscriptionLifecycleEventEntity
        extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "subscription_lifecycle_event_id")
    private UUID subscriptionLifecycleEventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "customer_subscription_id",
            nullable = false)
    private CustomerSubscriptionEntity customerSubscription;

    @Column(name = "previous_status")
    private String previousStatus;

    @Column(name = "new_status")
    private String newStatus;

    @Column(name = "effective_at")
    private OffsetDateTime effectiveAt;

    @Column(name = "reason_code")
    private String reasonCode;

    @Column(name = "payment_id")
    private UUID paymentId;

    @Version
    @Column(name = "version")
    private Long version;

    public UUID getSubscriptionLifecycleEventId() {
        return subscriptionLifecycleEventId;
    }

    public void setSubscriptionLifecycleEventId(
            UUID subscriptionLifecycleEventId) {
        this.subscriptionLifecycleEventId =
                subscriptionLifecycleEventId;
    }

    public CustomerSubscriptionEntity getCustomerSubscription() {
        return customerSubscription;
    }

    public void setCustomerSubscription(
            CustomerSubscriptionEntity customerSubscription) {
        this.customerSubscription = customerSubscription;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public OffsetDateTime getEffectiveAt() {
        return effectiveAt;
    }

    public void setEffectiveAt(
            OffsetDateTime effectiveAt) {
        this.effectiveAt = effectiveAt;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}