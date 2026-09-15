package com.propertypilot.application.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class GpsVerificationResponse {

    private UUID gpsVerificationId;

    private UUID visitId;

    private UUID propertyLocationId;

    private String validationStatus;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private BigDecimal distanceFromExpectedKm;

    private OffsetDateTime capturedAt;

    private OffsetDateTime validatedAt;

    public UUID getGpsVerificationId() {
        return gpsVerificationId;
    }

    public void setGpsVerificationId(UUID gpsVerificationId) {
        this.gpsVerificationId = gpsVerificationId;
    }

    public UUID getVisitId() {
        return visitId;
    }

    public void setVisitId(UUID visitId) {
        this.visitId = visitId;
    }

    public UUID getPropertyLocationId() {
        return propertyLocationId;
    }

    public void setPropertyLocationId(UUID propertyLocationId) {
        this.propertyLocationId = propertyLocationId;
    }

    public String getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(String validationStatus) {
        this.validationStatus = validationStatus;
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

    public BigDecimal getDistanceFromExpectedKm() {
        return distanceFromExpectedKm;
    }

    public void setDistanceFromExpectedKm(BigDecimal distanceFromExpectedKm) {
        this.distanceFromExpectedKm = distanceFromExpectedKm;
    }

    public OffsetDateTime getCapturedAt() {
        return capturedAt;
    }

    public void setCapturedAt(OffsetDateTime capturedAt) {
        this.capturedAt = capturedAt;
    }

    public OffsetDateTime getValidatedAt() {
        return validatedAt;
    }

    public void setValidatedAt(OffsetDateTime validatedAt) {
        this.validatedAt = validatedAt;
    }
}