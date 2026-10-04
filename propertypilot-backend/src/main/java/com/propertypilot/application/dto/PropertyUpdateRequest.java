package com.propertypilot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class PropertyUpdateRequest {

    @Schema(
            description = "Customer Id",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    @NotNull
    private UUID customerId;

    @Schema(
            description = "Property title",
            example = "Luxury Villa Hyderabad"
    )
    @NotBlank
    private String title;

    @Schema(
            description = "Property type",
            example = "VILLA"
    )
    @NotBlank
    private String propertyType;

    @Schema(
            description = "Listing status",
            example = "FOR_SALE"
    )
    private String listingStatus;

    @Schema(
            description = "Property price",
            example = "7500000"
    )
    private BigDecimal price;

    @Schema(
            description = "Property status",
            example = "ACTIVE"
    )
    private String status;
}