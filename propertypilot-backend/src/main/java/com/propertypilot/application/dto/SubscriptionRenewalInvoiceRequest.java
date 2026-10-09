package com.propertypilot.application.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record SubscriptionRenewalInvoiceRequest(
        @NotNull UUID customerSubscriptionId,
        @NotNull LocalDate periodStart,
        @NotNull LocalDate periodEnd) {
}
