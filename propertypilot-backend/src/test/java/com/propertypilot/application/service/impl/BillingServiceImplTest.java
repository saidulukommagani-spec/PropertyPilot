package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.InvoiceResponse;
import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.InvoiceStatus;
import com.propertypilot.domain.enums.InvoiceType;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.InvoiceEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerRepository;
import com.propertypilot.infrastructure.persistence.repository.InvoiceRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class BillingServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private BillingServiceImpl billingService;

    private InvoiceEntity buildInvoice() {

        CustomerEntity customer =
                new CustomerEntity();

        customer.setCustomerId(
                UUID.randomUUID());

        InvoiceEntity invoice =
                new InvoiceEntity();

        invoice.setInvoiceId(
                UUID.randomUUID());

        invoice.setCustomer(customer);

        invoice.setEntityType(
                BillingEntityType.DEAL);

        invoice.setEntityId(
                UUID.randomUUID());

        invoice.setInvoiceType(
                InvoiceType.BUYER_COMMISSION);

        invoice.setInvoiceNumber(
                "PP-INV-001");

        invoice.setInvoiceAmount(
                BigDecimal.valueOf(1000));

        invoice.setTaxAmount(
                BigDecimal.ZERO);

        invoice.setStatus(
                InvoiceStatus.ISSUED);

        invoice.setDescription(
                "Test Invoice");

        invoice.setIssuedAt(
                OffsetDateTime.now());

        return invoice;
    }

    @Test
    void createInvoice_ShouldCreateInvoice() {

        UUID customerId =
                UUID.randomUUID();

        CustomerEntity customer =
                new CustomerEntity();

        customer.setCustomerId(
                customerId);

        InvoiceEntity saved =
                buildInvoice();

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(invoiceRepository.save(any()))
                .thenReturn(saved);

        InvoiceResponse response =
                billingService.createInvoice(
                        BillingEntityType.DEAL,
                        UUID.randomUUID(),
                        customerId,
                        InvoiceType.BUYER_COMMISSION,
                        BigDecimal.valueOf(1000),
                        "Commission Invoice");

        assertThat(response)
                .isNotNull();

        verify(invoiceRepository)
                .save(any());
    }

    @Test
    void createInvoice_ShouldThrow_WhenCustomerNotFound() {

        UUID customerId =
                UUID.randomUUID();

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                billingService.createInvoice(
                        BillingEntityType.DEAL,
                        UUID.randomUUID(),
                        customerId,
                        InvoiceType.BUYER_COMMISSION,
                        BigDecimal.valueOf(1000),
                        "Commission"))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }

    @Test
    void getInvoice_ShouldReturnInvoice() {

        UUID invoiceId =
                UUID.randomUUID();

        InvoiceEntity invoice =
                buildInvoice();

        when(invoiceRepository.findById(invoiceId))
                .thenReturn(Optional.of(invoice));

        InvoiceResponse response =
                billingService.getInvoice(
                        invoiceId);

        assertThat(response)
                .isNotNull();
    }

    @Test
    void getInvoice_ShouldThrow_WhenNotFound() {

        UUID invoiceId =
                UUID.randomUUID();

        when(invoiceRepository.findById(invoiceId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                billingService.getInvoice(
                        invoiceId))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }

    @Test
    void getAllInvoices_ShouldReturnList() {

        when(invoiceRepository.findAll())
                .thenReturn(
                        List.of(buildInvoice()));

        List<InvoiceResponse> result =
                billingService.getAllInvoices();

        assertThat(result)
                .hasSize(1);
    }

    @Test
    void getInvoicesByCustomer_ShouldReturnList() {

        UUID customerId =
                UUID.randomUUID();

        when(invoiceRepository
                .findByCustomerCustomerId(
                        customerId))
                .thenReturn(
                        List.of(buildInvoice()));

        List<InvoiceResponse> result =
                billingService
                        .getInvoicesByCustomer(
                                customerId);

        assertThat(result)
                .hasSize(1);
    }

    @Test
    void getInvoicesByEntity_ShouldReturnList() {

        UUID entityId =
                UUID.randomUUID();

        when(invoiceRepository
                .findByEntityTypeAndEntityId(
                        BillingEntityType.DEAL,
                        entityId))
                .thenReturn(
                        List.of(buildInvoice()));

        List<InvoiceResponse> result =
                billingService
                        .getInvoicesByEntity(
                                BillingEntityType.DEAL,
                                entityId);

        assertThat(result)
                .hasSize(1);
    }
}