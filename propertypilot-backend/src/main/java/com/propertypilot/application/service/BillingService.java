package com.propertypilot.application.service;

import com.propertypilot.application.dto.InvoiceResponse;
import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.InvoiceType;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;
import java.time.LocalDate;

public interface BillingService {

    InvoiceResponse createInvoice(
            BillingEntityType entityType,
            UUID entityId,
            UUID customerId,
            InvoiceType invoiceType,
            BigDecimal amount,
            String description);

    InvoiceResponse createInvoice(
            BillingEntityType entityType,
            UUID entityId,
            UUID customerId,
            InvoiceType invoiceType,
            BigDecimal amount,
            String description,
            String currencyCode);

    InvoiceResponse createInvoiceFromCompletedServiceRequest(ServiceRequestEntity request);

    InvoiceResponse createSubscriptionRenewalInvoice(
            UUID customerSubscriptionId, LocalDate periodStart, LocalDate periodEnd);

    InvoiceResponse getInvoice(
            UUID invoiceId);

    InvoiceResponse sendInvoice(UUID invoiceId);

    InvoiceResponse cancelInvoice(UUID invoiceId);
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
