package com.propertypilot.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DocumentProcurementResponse {

    private UUID serviceRequestId;

    private String documentCode;

    private String status;

    private String message;
}