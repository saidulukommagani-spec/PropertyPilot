package com.propertypilot.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class DocumentProcurementDetailsResponse {

    private UUID serviceRequestId;

    private UUID propertyId;

    private String documentCode;

    private String requestType;

    private String priority;

    private String status;

    private BigDecimal amount;

    private Instant requestedAt;

    private Instant scheduledAt;

    private Instant completedAt;

    private String description;

    private String cancellationReasonCode;
}