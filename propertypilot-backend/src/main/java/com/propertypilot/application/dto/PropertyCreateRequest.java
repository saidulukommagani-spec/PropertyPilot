package com.propertypilot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
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

    @Schema(description = "Land or municipal survey number", example = "125/2A")
    @Size(max = 120)
    private String surveyNumber;

    @Schema(description = "Entered property area. For ACRES_GUNTAS this is the whole acres component.", example = "2")
    @DecimalMin(value = "0.0", message = "Property area cannot be negative")
    private BigDecimal areaValue;

    @Schema(description = "Area unit: SQ_FT, SQ_YD, or ACRES_GUNTAS", example = "SQ_FT")
    @Pattern(regexp = "SQ_FT|SQ_YD|ACRES_GUNTAS", message = "Choose sq ft, sq yd, or acres and guntas")
    private String areaUnit;

    @Schema(description = "Guntas component when areaUnit is ACRES_GUNTAS", example = "12")
    @Min(value = 0, message = "Guntas must be between 0 and 39")
    @Max(value = 39, message = "Guntas must be between 0 and 39")
    private Integer areaGuntas;

    @Schema(description = "Primary service address", example = "12 Lake View Road, Jubilee Hills")
    @NotBlank(message = "Property address is required")
    @Size(max = 255)
    private String addressLine1;

    @Schema(description = "Additional address information", example = "Building B, Flat 302")
    @Size(max = 255)
    private String addressLine2;

    @Schema(description = "Locality or neighborhood", example = "Jubilee Hills")
    @NotBlank(message = "Locality is required")
    @Size(max = 120)
    private String localityName;

    @Schema(description = "City or town", example = "Hyderabad")
    @NotBlank(message = "City is required")
    @Size(max = 120)
    private String city;

    @Schema(description = "District", example = "Hyderabad")
    @NotBlank(message = "District is required")
    @Size(max = 120)
    private String district;

    @Schema(description = "State", example = "Telangana")
    @NotBlank(message = "State is required")
    @Size(max = 120)
    private String state;

    @Schema(description = "Postal or PIN code", example = "500033")
    @NotBlank(message = "PIN code is required")
    @Size(max = 20)
    private String postalCode;

    @Schema(description = "ISO 3166-1 alpha-2 country code", example = "IN", defaultValue = "IN")
    @NotBlank(message = "Country is required")
    @Pattern(regexp = "[A-Z]{2}", message = "Use a 2-letter country code")
    private String countryCode = "IN";

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
