package com.propertypilot.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentProcurementStatusUpdateRequest {

    private String status;

    private String reason;

    private String cancellationReasonCode;
}