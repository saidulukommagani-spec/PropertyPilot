package com.propertypilot.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class PricingCouponRequest {
    @NotBlank @Pattern(regexp = "^[A-Z0-9_-]{3,80}$") private String couponCode;
    private String description;
    @NotBlank @Pattern(regexp = "^(FIXED|PERCENTAGE)$") private String discountType;
    @NotNull @DecimalMin(value = "0.01") private BigDecimal discountValue;
    @DecimalMin(value = "0.01") private BigDecimal maximumDiscount;
    @NotNull @DecimalMin(value = "0.00") private BigDecimal minimumOrderAmount;
    @NotNull private OffsetDateTime validFrom;
    @NotNull private OffsetDateTime validTo;
    private Integer redemptionLimit;
    private Integer perCustomerLimit;
    @NotBlank @Pattern(regexp = "^(DRAFT|ACTIVE|SUSPENDED|EXPIRED)$") private String status;

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String value) { couponCode = value; }
    public String getDescription() { return description; }
    public void setDescription(String value) { description = value; }
    public String getDiscountType() { return discountType; }
    public void setDiscountType(String value) { discountType = value; }
    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal value) { discountValue = value; }
    public BigDecimal getMaximumDiscount() { return maximumDiscount; }
    public void setMaximumDiscount(BigDecimal value) { maximumDiscount = value; }
    public BigDecimal getMinimumOrderAmount() { return minimumOrderAmount; }
    public void setMinimumOrderAmount(BigDecimal value) { minimumOrderAmount = value; }
    public OffsetDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(OffsetDateTime value) { validFrom = value; }
    public OffsetDateTime getValidTo() { return validTo; }
    public void setValidTo(OffsetDateTime value) { validTo = value; }
    public Integer getRedemptionLimit() { return redemptionLimit; }
    public void setRedemptionLimit(Integer value) { redemptionLimit = value; }
    public Integer getPerCustomerLimit() { return perCustomerLimit; }
    public void setPerCustomerLimit(Integer value) { perCustomerLimit = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { status = value; }
}
