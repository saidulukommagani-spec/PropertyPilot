package com.propertypilot.application.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Provider-authenticated settlement facts. The entire serialized event must be HMAC signed. */
public record PaymentSettlementEvent(
        String eventId,
        String provider,
        String providerPaymentId,
        UUID invoiceId,
        BigDecimal amount,
        String currencyCode,
        String status,
        OffsetDateTime settledAt,
        String failureReason) { }
