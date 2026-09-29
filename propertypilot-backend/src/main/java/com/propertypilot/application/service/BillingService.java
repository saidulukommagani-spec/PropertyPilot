package com.propertypilot.application.service;

import com.propertypilot.application.dto.InvoiceResponse;
import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.InvoiceType;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;

public interface BillingService {

    InvoiceResponse createInvoice(
            BillingEntityType entityType,
            UUID entityId,
            UUID customerId,
            InvoiceType invoiceType,
            BigDecimal amount,
            String description);

    InvoiceResponse getInvoice(
            UUID invoiceId);
List<InvoiceResponse>
getAllInvoices();

List<InvoiceResponse>
getInvoicesByCustomer(
        UUID customerId);

List<InvoiceResponse>
getInvoicesByEntity(
        BillingEntityType entityType,
        UUID entityId);

}