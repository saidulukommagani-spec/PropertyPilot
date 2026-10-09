package com.propertypilot.application.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record QuoteResponse(
        UUID quoteId,
        UUID customerId,
        UUID propertyId,
        UUID serviceId,
        String serviceName,
        String status,
        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        String currencyCode,
        BigDecimal standardPrice,
        BigDecimal savings,
        OffsetDateTime expiresAt,
        OffsetDateTime acceptedAt,
        OffsetDateTime rejectedAt,
        String rejectionReason,
        List<QuoteLineItemResponse> lineItems,
        String customerName,
        String customerEmail,
        String propertyName,
        OffsetDateTime createdAt) { }
