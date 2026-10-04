package com.propertypilot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Schema(
        description =
                "Request used to create a subscription plan version")
public class CreateSubscriptionPlanVersionRequest {

    @NotNull
    @Schema(
            description = "Subscription Plan Id",
            requiredMode =
                    Schema.RequiredMode.REQUIRED)
    private UUID subscriptionPlanId;

    @NotNull
    @Positive
    @Schema(
            description = "Version Number",
            example = "1")
    private Integer versionNumber;

    @NotNull
    @DecimalMin("0.01")
    @Schema(
            description = "Plan Price",
            example = "999.99")
    private BigDecimal price;

    @NotBlank
    @Schema(
            description = "Currency Code",
            example = "INR")
    private String currencyCode;

    @NotNull
    @Schema(
            description = "Effective From Date")
    private OffsetDateTime effectiveFrom;

    @Schema(
            description = "Effective To Date")
    private OffsetDateTime effectiveTo;

    @NotBlank
    @Schema(
            description = "Plan Status",
            example = "ACTIVE")
    private String status;

    @Schema(
            description =
                    "Benefits Configuration")
    private Map<String, Object> benefitsJson;

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