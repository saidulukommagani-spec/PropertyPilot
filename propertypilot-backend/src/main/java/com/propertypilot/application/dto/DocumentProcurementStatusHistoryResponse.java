package com.propertypilot.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class DocumentProcurementStatusHistoryResponse {

    private String previousStatus;

    private String newStatus;

    private String reason;

    private Instant changedAt;
}