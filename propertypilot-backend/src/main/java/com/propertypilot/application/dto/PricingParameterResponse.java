package com.propertypilot.application.dto;

import java.util.UUID;

public class PricingParameterResponse {

    private UUID pricingParameterId;

    private String profileCode;

    private String parameterCode;

    private String parameterName;

    private String parameterValue;

    private String parameterType;

    private Boolean activeFlag;

    private Long version;

    public UUID getPricingParameterId() {
        return pricingParameterId;
    }

    public void setPricingParameterId(
            UUID pricingParameterId) {
        this.pricingParameterId = pricingParameterId;
    }

    public String getProfileCode() {
        return profileCode;
    }

    public void setProfileCode(
            String profileCode) {
        this.profileCode = profileCode;
    }

    public String getParameterCode() {
        return parameterCode;
    }

    public void setParameterCode(
            String parameterCode) {
        this.parameterCode = parameterCode;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(
            String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(
            String parameterValue) {
        this.parameterValue = parameterValue;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(
            String parameterType) {
        this.parameterType = parameterType;
    }

    public Boolean getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(
            Boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(
            Long version) {
        this.version = version;
    }
}