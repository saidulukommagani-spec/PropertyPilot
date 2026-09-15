package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "subscription_benefits")
public class SubscriptionBenefitEntity {

    @Id
    @Column(name = "benefit_id")
    private UUID benefitId;

    @Column(
            name = "plan_id",
            nullable = false
    )
    private UUID planId;

    @Column(
            name = "benefit_type",
            nullable = false
    )
    private String benefitType;

    @Column(name = "benefit_value")
    private String benefitValue;

    @Column(
            name = "status",
            nullable = false
    )
    private String status;

    public UUID getBenefitId() {
        return benefitId;
    }

    public void setBenefitId(UUID benefitId) {
        this.benefitId = benefitId;
    }

    public UUID getPlanId() {
        return planId;
    }

    public void setPlanId(UUID planId) {
        this.planId = planId;
    }

    public String getBenefitType() {
        return benefitType;
    }

    public void setBenefitType(String benefitType) {
        this.benefitType = benefitType;
    }

    public String getBenefitValue() {
        return benefitValue;
    }

    public void setBenefitValue(String benefitValue) {
        this.benefitValue = benefitValue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}