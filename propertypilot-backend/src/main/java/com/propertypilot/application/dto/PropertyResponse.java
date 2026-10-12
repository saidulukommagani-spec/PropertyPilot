package com.propertypilot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class PropertyResponse {

    @Schema(description = "Property Identifier")
    private UUID propertyId;

    @Schema(description = "Customer Identifier")
    private UUID customerId;

    @Schema(description = "Property Title")
    private String title;

    @Schema(description = "Property Type")
    private String propertyType;

    @Schema(description = "Primary service address")
    private String addressLine1;

    @Schema(description = "Additional address information")
    private String addressLine2;

    private String localityName;
    private String city;
    private String district;
    private String state;
    private String postalCode;
    private String countryCode;

    @Schema(description = "Listing Status")
    private String listingStatus;

    @Schema(description = "Property Price")
    private BigDecimal price;

    @Schema(description = "Property Status")
    private String status;
}
