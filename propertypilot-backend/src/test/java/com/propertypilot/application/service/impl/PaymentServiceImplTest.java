package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreatePaymentRequest;
import com.propertypilot.application.dto.PaymentResponse;
import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.InvoiceStatus;
import com.propertypilot.domain.enums.PaymentMethod;
import com.propertypilot.domain.enums.PaymentStatus;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.InvoiceEntity;
import com.propertypilot.infrastructure.persistence.entity.PaymentEntity;
import com.propertypilot.infrastructure.persistence.repository.InvoiceRepository;
import com.propertypilot.infrastructure.persistence.repository.PaymentRepository;
import com.propertypilot.web.exception.BusinessException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private PaymentServiceImpl service;

    @Test
    void recordPayment_success() {

        UUID invoiceId = UUID.randomUUID();

        CustomerEntity customer =
                new CustomerEntity();
        customer.setCustomerId(
                UUID.randomUUID());

        InvoiceEntity invoice =
                new InvoiceEntity();

        invoice.setInvoiceId(
                invoiceId);

        invoice.setCustomer(
                customer);

        invoice.setStatus(InvoiceStatus.ISSUED);

        invoice.setInvoiceAmount(
                BigDecimal.valueOf(1000));

        invoice.setCurrencyCode(
                "INR");

    invoice.setEntityType(
        BillingEntityType.SERVICE_REQUEST);

        invoice.setEntityId(
                UUID.randomUUID());

        CreatePaymentRequest request =
                new CreatePaymentRequest();

        request.setAmount(
                BigDecimal.valueOf(1000));

        request.setPaymentMethod(
        PaymentMethod.UPI);

        request.setProvider(
                "PHONEPE");

        request.setProviderPaymentId(
                "TXN123");

        PaymentEntity saved =
                new PaymentEntity();

        saved.setPaymentId(
                UUID.randomUUID());

        saved.setInvoice(
                invoice);

        saved.setCustomer(
                customer);

        saved.setAmount(
                request.getAmount());

        saved.setPaymentMethod(
                request.getPaymentMethod());

        saved.setProvider(
                request.getProvider());

        saved.setProviderPaymentId(
                request.getProviderPaymentId());

        saved.setPaymentDate(
                OffsetDateTime.now());

        saved.setPaymentStatus(
                PaymentStatus.SUCCESS);

        when(invoiceRepository.findById(
                invoiceId))
                .thenReturn(
                        Optional.of(invoice));

        when(paymentRepository.save(
                any(PaymentEntity.class)))
                .thenReturn(saved);

        PaymentResponse response =
                service.recordPayment(
                        invoiceId,
                        request);

        assertThat(response)
                .isNotNull();

        assertThat(
                response.getPaymentId())
                .isEqualTo(
                        saved.getPaymentId());

        verify(paymentRepository)
                .save(any(
                        PaymentEntity.class));

        verify(invoiceRepository)
                .save(invoice);
    }

    @Test
    void recordPayment_invoiceNotFound() {

        UUID invoiceId =
                UUID.randomUUID();

        when(invoiceRepository.findById(
                invoiceId))
                .thenReturn(
                        Optional.empty());

        assertThatThrownBy(() ->
                service.recordPayment(
                        invoiceId,
                        new CreatePaymentRequest()))
                .isInstanceOf(
                        ResourceNotFoundException.class)
                .hasMessage(
                        "Invoice not found");
    }

    @Test
    void recordPayment_invoiceAlreadyPaid() {

        InvoiceEntity invoice =
                new InvoiceEntity();

        invoice.setStatus(
                InvoiceStatus.PAID);

        when(invoiceRepository.findById(
                any(UUID.class)))
                .thenReturn(
                        Optional.of(invoice));

        assertThatThrownBy(() ->
                service.recordPayment(
                        UUID.randomUUID(),
                        new CreatePaymentRequest()))
                .isInstanceOf(
                        BusinessException.class)
                .hasMessage(
                        "Invoice already paid");
    }

    @Test
    void recordPayment_amountMismatch() {

        InvoiceEntity invoice =
                new InvoiceEntity();

        invoice.setStatus(InvoiceStatus.ISSUED);

        invoice.setInvoiceAmount(
                BigDecimal.valueOf(1000));

        CreatePaymentRequest request =
                new CreatePaymentRequest();

        request.setAmount(
                BigDecimal.valueOf(500));

        when(invoiceRepository.findById(
                any(UUID.class)))
                .thenReturn(
                        Optional.of(invoice));

        assertThatThrownBy(() ->
                service.recordPayment(
                        UUID.randomUUID(),
                        request))
                .isInstanceOf(
                        BusinessException.class)
                .hasMessage(
                        "Payment amount must match invoice amount");
    }

    @Test
    void getPayment_success() {

        UUID paymentId =
                UUID.randomUUID();

        CustomerEntity customer =
                new CustomerEntity();

        customer.setCustomerId(
                UUID.randomUUID());

        InvoiceEntity invoice =
                new InvoiceEntity();

        invoice.setInvoiceId(
                UUID.randomUUID());

        PaymentEntity payment =
                new PaymentEntity();

        payment.setPaymentId(
                paymentId);

        payment.setCustomer(
                customer);

        payment.setInvoice(
                invoice);

        payment.setAmount(
                BigDecimal.valueOf(1000));

        payment.setPaymentStatus(
                PaymentStatus.SUCCESS);

        when(paymentRepository.findById(
                paymentId))
                .thenReturn(
                        Optional.of(payment));

        PaymentResponse response =
                service.getPayment(
                        paymentId);

        assertThat(response)
                .isNotNull();

        assertThat(
                response.getPaymentId())
                .isEqualTo(
                        paymentId);
    }

    @Test
    void getPayment_notFound() {

        UUID paymentId =
                UUID.randomUUID();

        when(paymentRepository.findById(
                paymentId))
                .thenReturn(
                        Optional.empty());

        assertThatThrownBy(() ->
                service.getPayment(
                        paymentId))
                .isInstanceOf(
                        ResourceNotFoundException.class)
                .hasMessage(
                        "Payment not found");
    }

    @Test
    void getPaymentsByInvoice_success() {

        UUID invoiceId =
                UUID.randomUUID();

        CustomerEntity customer =
                new CustomerEntity();

        customer.setCustomerId(
                UUID.randomUUID());

        InvoiceEntity invoice =
                new InvoiceEntity();

        invoice.setInvoiceId(
                invoiceId);

        PaymentEntity payment =
                new PaymentEntity();

        payment.setPaymentId(
                UUID.randomUUID());

        payment.setInvoice(
                invoice);

        payment.setCustomer(
                customer);

        payment.setAmount(
                BigDecimal.TEN);

        payment.setPaymentStatus(
                PaymentStatus.SUCCESS);

        when(paymentRepository
                .findByInvoice_InvoiceId(
                        invoiceId))
                .thenReturn(
                        List.of(payment));

        List<PaymentResponse> responses =
                service.getPaymentsByInvoice(
                        invoiceId);

        assertThat(responses)
                .hasSize(1);

        assertThat(
                responses.get(0)
                        .getInvoiceId())
                .isEqualTo(
                        invoiceId);
    }
}