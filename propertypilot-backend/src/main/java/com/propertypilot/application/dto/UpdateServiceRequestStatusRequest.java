package com.propertypilot.application.dto;

import jakarta.validation.constraints.Size;

public class UpdateServiceRequestStatusRequest {

    @Size(max = 255, message = "Change reason must be 255 characters or fewer")
    private String changeReason;

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(
            String changeReason) {
        this.changeReason = changeReason;
    }

    
}
