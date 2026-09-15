package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "customer_subscriptions")
public class CustomerSubscriptionEntity
        extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "customer_subscription_id",
            nullable = false,
            updatable = false)
    private UUID customerSubscriptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "customer_id",
            nullable = false)
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "plan_version_id",
            nullable = false)
    private SubscriptionPlanVersionEntity planVersion;

    @Column(
            name = "status",
            nullable = false,
            length = 30)
    private String status;

    @Column(
            name = "start_date",
            nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(
            name = "auto_renew",
            nullable = false)
    private Boolean autoRenew = false;

    @Version
    @Column(
            name = "version",
            nullable = false)
    private Long version = 0L;

    public UUID getCustomerSubscriptionId() {
        return customerSubscriptionId;
    }

    public void setCustomerSubscriptionId(
            UUID customerSubscriptionId) {
        this.customerSubscriptionId =
                customerSubscriptionId;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(
            CustomerEntity customer) {
        this.customer = customer;
    }

    public SubscriptionPlanVersionEntity getPlanVersion() {
        return planVersion;
    }

    public void setPlanVersion(
            SubscriptionPlanVersionEntity planVersion) {
        this.planVersion = planVersion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(
            LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(
            LocalDate endDate) {
        this.endDate = endDate;
    }

    public Boolean getAutoRenew() {
        return autoRenew;
    }

    public void setAutoRenew(
            Boolean autoRenew) {
        this.autoRenew = autoRenew;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(
            Long version) {
        this.version = version;
    }
}