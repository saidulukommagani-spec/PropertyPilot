package com.propertypilot.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateGpsVerificationRequest {

    private UUID visitId;

    private BigDecimal latitude;

    private BigDecimal longitude;

    public UUID getVisitId() {
        return visitId;
    }

    public void setVisitId(UUID visitId) {
        this.visitId = visitId;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
}