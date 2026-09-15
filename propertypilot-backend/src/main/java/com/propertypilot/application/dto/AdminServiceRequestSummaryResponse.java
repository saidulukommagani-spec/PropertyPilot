package com.propertypilot.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class AdminServiceRequestSummaryResponse {

    private UUID serviceRequestId;

    private UUID propertyId;

    private String propertyTitle;

    private UUID customerId;

    private String customerName;

    private String requestType;

    private String status;

    private String priority;

    private Instant requestedAt;
}