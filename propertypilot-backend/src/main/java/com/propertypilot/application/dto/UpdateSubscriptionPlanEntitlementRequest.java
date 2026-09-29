package com.propertypilot.application.dto;

public class UpdateSubscriptionPlanEntitlementRequest {

    private Integer quantity;

    private String periodType;

    private Boolean carryForwardAllowed;

    private String status;

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(
            Integer quantity) {
        this.quantity = quantity;
    }

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(
            String periodType) {
        this.periodType = periodType;
    }

    public Boolean getCarryForwardAllowed() {
        return carryForwardAllowed;
    }

    public void setCarryForwardAllowed(
            Boolean carryForwardAllowed) {
        this.carryForwardAllowed =
                carryForwardAllowed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {
        this.status = status;
    }
}