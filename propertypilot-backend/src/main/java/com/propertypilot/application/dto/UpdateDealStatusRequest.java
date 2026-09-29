package com.propertypilot.application.dto;

public class UpdateDealStatusRequest {

    private String dealStatus;

    private String remarks;

    public String getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(
            String dealStatus) {
        this.dealStatus = dealStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(
            String remarks) {
        this.remarks = remarks;
    }
}