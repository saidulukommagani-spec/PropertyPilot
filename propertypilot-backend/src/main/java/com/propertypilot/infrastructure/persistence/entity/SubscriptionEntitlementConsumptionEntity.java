package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscription_entitlement_consumptions")
public class SubscriptionEntitlementConsumptionEntity
        extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "entitlement_consumption_id")
    private UUID entitlementConsumptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "entitlement_id",
            nullable = false
    )
    private SubscriptionPlanEntitlementEntity entitlement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "customer_subscription_id",
            nullable = false
    )
    private CustomerSubscriptionEntity customerSubscription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "service_id",
            nullable = false
    )
    private ServiceEntity service;

    @Column(
            name = "period_start",
            nullable = false
    )
    private LocalDate periodStart;

    @Column(name = "period_end")
    private LocalDate periodEnd;

    @Column(
            name = "entitled_quantity",
            nullable = false
    )
    private Integer entitledQuantity;

    @Column(
            name = "consumed_quantity",
            nullable = false
    )
    private Integer consumedQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_request_id")
    private ServiceRequestEntity serviceRequest;

    @Column(name = "expired_at")
    private OffsetDateTime expiredAt;

    @Version
    @Column(name = "version")
    private Long version;

    public UUID getEntitlementConsumptionId() {
        return entitlementConsumptionId;
    }

    public void setEntitlementConsumptionId(
            UUID entitlementConsumptionId) {
        this.entitlementConsumptionId =
                entitlementConsumptionId;
    }

    public SubscriptionPlanEntitlementEntity getEntitlement() {
        return entitlement;
    }

    public void setEntitlement(
            SubscriptionPlanEntitlementEntity entitlement) {
        this.entitlement = entitlement;
    }

    public CustomerSubscriptionEntity getCustomerSubscription() {
        return customerSubscription;
    }

    public void setCustomerSubscription(
            CustomerSubscriptionEntity customerSubscription) {
        this.customerSubscription =
                customerSubscription;
    }

    public ServiceEntity getService() {
        return service;
    }

    public void setService(ServiceEntity service) {
        this.service = service;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public Integer getEntitledQuantity() {
        return entitledQuantity;
    }

    public void setEntitledQuantity(
            Integer entitledQuantity) {
        this.entitledQuantity =
                entitledQuantity;
    }

    public Integer getConsumedQuantity() {
        return consumedQuantity;
    }

    public void setConsumedQuantity(
            Integer consumedQuantity) {
        this.consumedQuantity =
                consumedQuantity;
    }

    public ServiceRequestEntity getServiceRequest() {
        return serviceRequest;
    }

    public void setServiceRequest(
            ServiceRequestEntity serviceRequest) {
        this.serviceRequest = serviceRequest;
    }

    public OffsetDateTime getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(
            OffsetDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}