package com.propertypilot.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public class ClusterDiscountRuleRequest {
    @NotBlank private String serviceType;
    @NotNull @DecimalMin(value = "0.001") private BigDecimal radiusKm;
    @NotNull @Min(1) private Integer minimumRequests;
    @NotNull @DecimalMin(value = "0.01") private BigDecimal discountPercentage;
    @NotBlank @Pattern(regexp = "^(ACTIVE|INACTIVE)$") private String status;

    public String getServiceType() { return serviceType; }
    public void setServiceType(String value) { serviceType = value; }
    public BigDecimal getRadiusKm() { return radiusKm; }
    public void setRadiusKm(BigDecimal value) { radiusKm = value; }
    public Integer getMinimumRequests() { return minimumRequests; }
    public void setMinimumRequests(Integer value) { minimumRequests = value; }
    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal value) { discountPercentage = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { status = value; }
}
