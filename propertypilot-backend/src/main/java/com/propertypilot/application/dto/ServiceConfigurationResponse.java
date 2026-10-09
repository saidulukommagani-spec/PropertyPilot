package com.propertypilot.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ServiceConfigurationResponse(
        UUID serviceId,
        String serviceCode,
        String serviceName,
        String category,
        BigDecimal basePrice,
        Boolean activeFlag,
        Boolean requiresVisit,
        Boolean visitMandatory,
        Integer maxVisitsAllowed,
        Boolean requiresGps,
        BigDecimal gpsRadiusKm,
        Boolean requiresBeforePhoto,
        Boolean requiresAfterPhoto,
        Boolean requiresSignature,
        Boolean requiresDocumentUpload,
        Boolean remote,
        Boolean billable) {
}
