package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "subscription_plan_versions")
public class SubscriptionPlanVersionEntity {

@Id
@GeneratedValue(strategy = GenerationType.UUID)
@Column(name = "plan_version_id")
private UUID planVersionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "subscription_plan_id",
            nullable = false)
    private SubscriptionPlanEntity subscriptionPlan;

    @Column(name = "version_number")
    private Integer versionNumber;

    @Column(name = "price")
    private BigDecimal price;

 @Column(name = "currency_code")
private String currencyCode;

    @Column(name = "effective_from")
    private OffsetDateTime effectiveFrom;

    @Column(name = "effective_to")
    private OffsetDateTime effectiveTo;

    @Column(name = "status")
    private String status;

  @JdbcTypeCode(SqlTypes.JSON)
@Column(
        name = "benefits_json",
        columnDefinition = "jsonb")
private Map<String, Object> benefitsJson;
public Map<String, Object> getBenefitsJson() {
    return benefitsJson;
}

public void setBenefitsJson(
        Map<String, Object> benefitsJson) {
    this.benefitsJson = benefitsJson;
}
    @Version
    @Column(name = "version")
    private Long version;

    public UUID getPlanVersionId() {
        return planVersionId;
    }

    public void setPlanVersionId(UUID planVersionId) {
        this.planVersionId = planVersionId;
    }

    public SubscriptionPlanEntity getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public void setSubscriptionPlan(
            SubscriptionPlanEntity subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public OffsetDateTime getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(
            OffsetDateTime effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public OffsetDateTime getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(
            OffsetDateTime effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}