package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlanEntity
        extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "subscription_plan_id",
            nullable = false,
            updatable = false
    )
    private UUID subscriptionPlanId;

    @Column(
            name = "plan_code",
            nullable = false,
            unique = true,
            length = 80
    )
    private String planCode;

    @Column(
            name = "plan_name",
            nullable = false,
            length = 150
    )
    private String planName;

    @Column(name = "description")
    private String description;

    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private String status;

    @Version
    @Column(
            name = "version",
            nullable = false
    )
    private Long version = 0L;

    public UUID getSubscriptionPlanId() {
        return subscriptionPlanId;
    }

    public void setSubscriptionPlanId(
            UUID subscriptionPlanId) {
        this.subscriptionPlanId =
                subscriptionPlanId;
    }

    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(
            String planCode) {
        this.planCode = planCode;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(
            String planName) {
        this.planName = planName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {
        this.description = description;
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