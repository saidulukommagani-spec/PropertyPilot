package com.propertypilot.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateMonitoringScheduleRequest(
        UUID customerSubscriptionId,
        UUID sourceServiceRequestId,
        @NotNull UUID propertyId,
        @NotNull UUID serviceId,
        @NotBlank String frequency,
        OffsetDateTime firstDueAt) {
}
