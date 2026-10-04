package com.propertypilot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PropertyCreateRequest {

    @Schema(
            description = "Property title",
            example = "Luxury Villa Hyderabad"
    )
    @NotBlank(message = "Title is required")
    private String title;

    @Schema(
            description = "Property type",
            example = "VILLA"
    )
    @NotBlank(message = "Property Type is required")
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