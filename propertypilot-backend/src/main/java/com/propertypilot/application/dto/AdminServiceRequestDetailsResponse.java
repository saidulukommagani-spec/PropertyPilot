package com.propertypilot.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.List;
@Getter
@Setter
public class AdminServiceRequestDetailsResponse {

    private UUID serviceRequestId;

    private UUID customerId;

    private String customerName;

    private String customerEmail;

    private String customerMobile;

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
    private List<ServiceRequestStatusHistoryResponse> statusHistory;
private UUID assignedToUserId;

private String assignedToName;

private Instant assignedAt;

private UUID assignedByUserId;

private String assignedByName;


}