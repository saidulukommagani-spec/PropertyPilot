package com.propertypilot.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class DocumentProcurementRequestSummaryResponse {

    private UUID serviceRequestId;

    private String documentCode;

    private String status;

    private Instant requestedAt;

    private String remarks;
}