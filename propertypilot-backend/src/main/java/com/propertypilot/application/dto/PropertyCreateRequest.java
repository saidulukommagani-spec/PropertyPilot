package com.propertypilot.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PropertyCreateRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Property Type is required")
    private String propertyType;

    private String listingStatus;

    private BigDecimal price;

    private String status;
}