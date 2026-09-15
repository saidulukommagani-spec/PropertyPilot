package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "subscription_plan_entitlements")
public class SubscriptionPlanEntitlementEntity
        extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "entitlement_id")
    private UUID entitlementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "plan_version_id",
            nullable = false
    )
    private SubscriptionPlanVersionEntity planVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "service_id",
            nullable = false
    )
    private ServiceEntity service;

    @Column(
            name = "quantity",
            nullable = false
    )
    private Integer quantity;

    @Column(
            name = "period_type",
            nullable = false
    )
    private String periodType;

    @Column(
            name = "carry_forward_allowed",
            nullable = false
    )
    private Boolean carryForwardAllowed;

    @Column(
            name = "status",
            nullable = false
    )
    private String status;

    @Version
    @Column(name = "version")
    private Long version;

    public UUID getEntitlementId() {
        return entitlementId;
    }

    public void setEntitlementId(UUID entitlementId) {
        this.entitlementId = entitlementId;
    }

    public SubscriptionPlanVersionEntity getPlanVersion() {
        return planVersion;
    }

    public void setPlanVersion(
            SubscriptionPlanVersionEntity planVersion) {
        this.planVersion = planVersion;
    }

    public ServiceEntity getService() {
        return service;
    }

    public void setService(ServiceEntity service) {
        this.service = service;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

    public Boolean getCarryForwardAllowed() {
        return carryForwardAllowed;
    }

    public void setCarryForwardAllowed(
            Boolean carryForwardAllowed) {
        this.carryForwardAllowed = carryForwardAllowed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}