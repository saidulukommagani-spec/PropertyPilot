package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "subscription_renewals")
public class SubscriptionRenewalEntity
        extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "subscription_renewal_id")
    private UUID subscriptionRenewalId;

    /*
     * Original subscription
     * being renewed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "customer_subscription_id",
            nullable = false)
    private CustomerSubscriptionEntity
            customerSubscription;

    /*
     * New subscription
     * created after renewal.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "new_customer_subscription_id")
    private CustomerSubscriptionEntity
            newCustomerSubscription;

    @Column(name = "payment_id")
    private UUID paymentId;

    @Column(
            name = "renewal_date",
            nullable = false)
    private LocalDate renewalDate;

    @Column(
            name = "period_start",
            nullable = false)
    private LocalDate periodStart;

    @Column(
            name = "period_end",
            nullable = false)
    private LocalDate periodEnd;

    /*
     * MANUAL
     * AUTO
     * ADMIN
     */
    @Column(
            name = "renewal_type",
            length = 30)
    private String renewalType;

    @Column(
            name = "renewal_amount",
            precision = 12,
            scale = 2)
    private BigDecimal renewalAmount;

    @Column(
            name = "remarks",
            length = 1000)
    private String remarks;

    /*
     * PENDING
     * SUCCESS
     * FAILED
     */
    @Column(
            name = "status",
            nullable = false,
            length = 30)
    private String status;

    @Version
    @Column(name = "version")
    private Long version;

    public UUID getSubscriptionRenewalId() {
        return subscriptionRenewalId;
    }

    public void setSubscriptionRenewalId(
            UUID subscriptionRenewalId) {
        this.subscriptionRenewalId =
                subscriptionRenewalId;
    }

    public CustomerSubscriptionEntity
    getCustomerSubscription() {
        return customerSubscription;
    }

    public void setCustomerSubscription(
            CustomerSubscriptionEntity customerSubscription) {
        this.customerSubscription =
                customerSubscription;
    }

    public CustomerSubscriptionEntity
    getNewCustomerSubscription() {
        return newCustomerSubscription;
    }

    public void setNewCustomerSubscription(
            CustomerSubscriptionEntity newCustomerSubscription) {
        this.newCustomerSubscription =
                newCustomerSubscription;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(
            UUID paymentId) {
        this.paymentId = paymentId;
    }

    public LocalDate getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(
            LocalDate renewalDate) {
        this.renewalDate = renewalDate;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(
            LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(
            LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public String getRenewalType() {
        return renewalType;
    }

    public void setRenewalType(
            String renewalType) {
        this.renewalType = renewalType;
    }

    public BigDecimal getRenewalAmount() {
        return renewalAmount;
    }

    public void setRenewalAmount(
            BigDecimal renewalAmount) {
        this.renewalAmount = renewalAmount;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(
            String remarks) {
        this.remarks = remarks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {
        this.status = status;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(
            Long version) {
        this.version = version;
    }
}