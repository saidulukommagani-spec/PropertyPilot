package com.propertypilot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(
        name = "CreateGpsVerificationRequest",
        description = "GPS verification request")
public class CreateGpsVerificationRequest {

    @Schema(
            description = "Visit identifier",
            requiredMode =
                    Schema.RequiredMode.REQUIRED)
    @NotNull(
            message = "Visit Id is required")
    private UUID visitId;

    @Schema(
            description = "Latitude",
            example = "17.385044")
    @NotNull(
            message = "Latitude is required")
    @DecimalMin(
            value = "-90.0",
            message = "Latitude must be >= -90")
    @DecimalMax(
            value = "90.0",
            message = "Latitude must be <= 90")
    private BigDecimal latitude;

    @Schema(
            description = "Longitude",
            example = "78.486671")
    @NotNull(
            message = "Longitude is required")
    @DecimalMin(
            value = "-180.0",
            message = "Longitude must be >= -180")
    @DecimalMax(
            value = "180.0",
            message = "Longitude must be <= 180")
    private BigDecimal longitude;

    public UUID getVisitId() {
        return visitId;
    }

    public void setVisitId(
            UUID visitId) {
        this.visitId = visitId;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(
            BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(
            BigDecimal longitude) {
        this.longitude = longitude;
    }
}