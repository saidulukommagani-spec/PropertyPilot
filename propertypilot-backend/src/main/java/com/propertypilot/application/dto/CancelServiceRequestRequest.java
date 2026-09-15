package com.propertypilot.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelServiceRequestRequest {

    private String reasonCode;

    private String reason;
}