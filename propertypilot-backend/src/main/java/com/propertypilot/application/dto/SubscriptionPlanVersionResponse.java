package com.propertypilot.application.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public class SubscriptionPlanVersionResponse {

    private UUID planVersionId;

    private UUID subscriptionPlanId;

    private Integer versionNumber;

    private BigDecimal price;

    private String currencyCode;

    private OffsetDateTime effectiveFrom;

    private OffsetDateTime effectiveTo;

    private String status;

    private Map<String, Object> benefitsJson;

    public UUID getPlanVersionId() {
        return planVersionId;
    }

    public void setPlanVersionId(
            UUID planVersionId) {
        this.planVersionId =
                planVersionId;
    }

    public UUID getSubscriptionPlanId() {
        return subscriptionPlanId;
    }

    public void setSubscriptionPlanId(
            UUID subscriptionPlanId) {
        this.subscriptionPlanId =
                subscriptionPlanId;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(
            Integer versionNumber) {
        this.versionNumber =
                versionNumber;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(
            BigDecimal price) {
        this.price = price;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(
            String currencyCode) {
        this.currencyCode =
                currencyCode;
    }

    public OffsetDateTime getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(
            OffsetDateTime effectiveFrom) {
        this.effectiveFrom =
                effectiveFrom;
    }

    public OffsetDateTime getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(
            OffsetDateTime effectiveTo) {
        this.effectiveTo =
                effectiveTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {
        this.status = status;
    }

    public Map<String, Object> getBenefitsJson() {
        return benefitsJson;
    }

    public void setBenefitsJson(
            Map<String, Object> benefitsJson) {
        this.benefitsJson =
                benefitsJson;
    }
}
