package com.propertypilot.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class PropertyUpdateRequest {

    @NotNull
    private UUID customerId;

    @NotBlank
    private String title;

    @NotBlank
    private String propertyType;

    private String listingStatus;

    private BigDecimal price;

    private String status;
}