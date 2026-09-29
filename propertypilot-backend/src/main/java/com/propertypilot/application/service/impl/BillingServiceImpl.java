package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.InvoiceResponse;
import com.propertypilot.application.service.BillingService;
import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.InvoiceStatus;
import com.propertypilot.domain.enums.InvoiceType;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.InvoiceEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerRepository;
import com.propertypilot.infrastructure.persistence.repository.InvoiceRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.InvoiceType;
import com.propertypilot.domain.enums.InvoiceStatus;
import com.propertypilot.application.dto.InvoiceResponse;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class BillingServiceImpl
        implements BillingService {

    private final InvoiceRepository invoiceRepository;

    private final CustomerRepository customerRepository;

   @Override
public InvoiceResponse  createInvoice(
        BillingEntityType entityType,
        UUID entityId,
        UUID customerId,
        InvoiceType invoiceType,
        BigDecimal amount,
        String description) {

        CustomerEntity customer =
                customerRepository
                        .findById(customerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found"));

        InvoiceEntity invoice =
                new InvoiceEntity();

        invoice.setCustomer(customer);

  invoice.setEntityType(
        entityType);

        invoice.setEntityId(
                entityId);

invoice.setInvoiceType(
        invoiceType);

        invoice.setInvoiceAmount(
                amount);

        invoice.setTaxAmount(
                BigDecimal.ZERO);

        invoice.setStatus(
                InvoiceStatus.ISSUED);

        invoice.setDescription(
                description);

        invoice.setIssuedAt(
                OffsetDateTime.now());

        invoice.setCurrencyCode(
                "INR");

        invoice.setInvoiceNumber(
                generateInvoiceNumber());

        InvoiceEntity saved =
                invoiceRepository.save(
                        invoice);

      return mapInvoice(saved);
    }

    private String generateInvoiceNumber() {

        return "PP-INV-"
                + System.currentTimeMillis();
    }

  @Override
@Transactional(readOnly = true)
public InvoiceResponse getInvoice(
        UUID invoiceId) {

    InvoiceEntity invoice =
            invoiceRepository
                    .findById(invoiceId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Invoice not found"));

    return mapInvoice(invoice);
}

@Override
@Transactional(readOnly = true)
public List<InvoiceResponse>
getAllInvoices() {

    return invoiceRepository
            .findAll()
            .stream()
            .map(this::mapInvoice)
            .toList();
}

@Override
@Transactional(readOnly = true)
public List<InvoiceResponse>
getInvoicesByCustomer(
        UUID customerId) {

    return invoiceRepository
            .findByCustomerCustomerId(
                    customerId)
            .stream()
            .map(this::mapInvoice)
            .toList();
}

@Override
@Transactional(readOnly = true)
public List<InvoiceResponse>
getInvoicesByEntity(
        BillingEntityType entityType,
        UUID entityId) {

    return invoiceRepository
            .findByEntityTypeAndEntityId(
                    entityType,
                    entityId)
            .stream()
            .map(this::mapInvoice)
            .toList();
}

private InvoiceResponse mapInvoice(
        InvoiceEntity entity) {

    InvoiceResponse response =
            new InvoiceResponse();

    response.setInvoiceId(
            entity.getInvoiceId());

    response.setCustomerId(
            entity.getCustomer()
                    .getCustomerId());

    response.setEntityType(
            entity.getEntityType());

    response.setEntityId(
            entity.getEntityId());

    response.setInvoiceType(
            entity.getInvoiceType());

    response.setInvoiceNumber(
            entity.getInvoiceNumber());

    response.setInvoiceAmount(
            entity.getInvoiceAmount());

    response.setTaxAmount(
            entity.getTaxAmount());

    response.setStatus(
            entity.getStatus());

    response.setDescription(
            entity.getDescription());

    response.setIssuedAt(
            entity.getIssuedAt());

    response.setDueAt(
            entity.getDueAt());

    response.setPaidAt(
            entity.getPaidAt());

    return response;
}

}