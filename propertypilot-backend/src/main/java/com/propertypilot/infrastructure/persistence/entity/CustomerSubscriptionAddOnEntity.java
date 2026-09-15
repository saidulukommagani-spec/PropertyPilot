package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "customer_subscription_add_ons")
public class CustomerSubscriptionAddOnEntity
        extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "customer_subscription_add_on_id")
    private UUID customerSubscriptionAddOnId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "customer_subscription_id",
            nullable = false
    )
    private CustomerSubscriptionEntity customerSubscription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "subscription_add_on_id",
            nullable = false
    )
    private SubscriptionAddOnEntity subscriptionAddOn;

    @Column(name = "pricing_estimate_id")
    private UUID pricingEstimateId;

    @Column(
            name = "quantity",
            nullable = false
    )
    private Integer quantity;

    @Column(
            name = "status",
            nullable = false
    )
    private String status;

    @Column(
            name = "starts_at",
            nullable = false
    )
    private OffsetDateTime startsAt;

    @Column(name = "ends_at")
    private OffsetDateTime endsAt;

    @Version
    @Column(name = "version")
    private Long version;

    public UUID getCustomerSubscriptionAddOnId() {
        return customerSubscriptionAddOnId;
    }

    public void setCustomerSubscriptionAddOnId(
            UUID customerSubscriptionAddOnId) {
        this.customerSubscriptionAddOnId =
                customerSubscriptionAddOnId;
    }

    public CustomerSubscriptionEntity getCustomerSubscription() {
        return customerSubscription;
    }

    public void setCustomerSubscription(
            CustomerSubscriptionEntity customerSubscription) {
        this.customerSubscription = customerSubscription;
    }

    public SubscriptionAddOnEntity getSubscriptionAddOn() {
        return subscriptionAddOn;
    }

    public void setSubscriptionAddOn(
            SubscriptionAddOnEntity subscriptionAddOn) {
        this.subscriptionAddOn = subscriptionAddOn;
    }

    public UUID getPricingEstimateId() {
        return pricingEstimateId;
    }

    public void setPricingEstimateId(
            UUID pricingEstimateId) {
        this.pricingEstimateId = pricingEstimateId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(
            Integer quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {
        this.status = status;
    }

    public OffsetDateTime getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(
            OffsetDateTime startsAt) {
        this.startsAt = startsAt;
    }

    public OffsetDateTime getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(
            OffsetDateTime endsAt) {
        this.endsAt = endsAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(
            Long version) {
        this.version = version;
    }
}