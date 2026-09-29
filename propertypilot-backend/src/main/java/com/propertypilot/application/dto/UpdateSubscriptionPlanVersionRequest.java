package com.propertypilot.application.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;

public class UpdateSubscriptionPlanVersionRequest {

    private Integer versionNumber;

    private BigDecimal price;

    private String currencyCode;

    private OffsetDateTime effectiveFrom;

    private OffsetDateTime effectiveTo;

    private String status;

    private Map<String, Object> benefitsJson;

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
