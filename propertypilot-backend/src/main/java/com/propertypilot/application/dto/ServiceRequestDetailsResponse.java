package com.propertypilot.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.List;

@Getter
@Setter
public class ServiceRequestDetailsResponse {

    private UUID serviceRequestId;

    private UUID propertyId;

    private String propertyTitle;

    private String requestType;

    private String status;

    private String priority;

    private BigDecimal amount;

    private String description;

    private Instant requestedAt;

    private Instant scheduledAt;

    private Instant completedAt;

    private String cancellationReasonCode;
    private List<ServiceRequestStatusHistoryResponse>
        statusHistory;
}
