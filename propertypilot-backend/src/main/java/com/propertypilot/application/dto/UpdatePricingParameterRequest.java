package com.propertypilot.application.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdatePricingParameterRequest {

    @NotBlank
    private String parameterValue;

    private String remarks;

    public String getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(
            String parameterValue) {
        this.parameterValue = parameterValue;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(
            String remarks) {
        this.remarks = remarks;
    }
}