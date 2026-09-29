package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreatePaymentRequest;
import com.propertypilot.application.dto.PaymentResponse;
import com.propertypilot.application.service.PaymentService;
import com.propertypilot.domain.enums.InvoiceStatus;
import com.propertypilot.domain.enums.PaymentStatus;
import com.propertypilot.infrastructure.persistence.entity.InvoiceEntity;
import com.propertypilot.infrastructure.persistence.entity.PaymentEntity;
import com.propertypilot.infrastructure.persistence.repository.InvoiceRepository;
import com.propertypilot.infrastructure.persistence.repository.PaymentRepository;
import com.propertypilot.web.exception.BusinessException;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PaymentServiceImpl
        implements PaymentService {

    private final PaymentRepository
            paymentRepository;

    private final InvoiceRepository
            invoiceRepository;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            InvoiceRepository invoiceRepository) {

        this.paymentRepository =
                paymentRepository;

        this.invoiceRepository =
                invoiceRepository;
    }

    @Override
    public PaymentResponse recordPayment(
            UUID invoiceId,
            CreatePaymentRequest request) {

        InvoiceEntity invoice =
                invoiceRepository
                        .findById(invoiceId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Invoice not found"));

        if (invoice.getStatus()
                == InvoiceStatus.PAID) {

            throw new BusinessException(
                    "Invoice already paid");
        }

        if (request.getAmount()
                .compareTo(
                        invoice.getInvoiceAmount()) != 0) {

            throw new BusinessException(
                    "Payment amount must match invoice amount");
        }

        PaymentEntity payment =
                new PaymentEntity();

        payment.setCustomer(
                invoice.getCustomer());

        payment.setInvoice(
                invoice);

        payment.setEntityType(
                invoice.getEntityType());

        payment.setEntityId(
                invoice.getEntityId());

        payment.setAmount(
                request.getAmount());

        payment.setCurrencyCode(
                invoice.getCurrencyCode());

        payment.setPaymentMethod(
                request.getPaymentMethod());

        payment.setProvider(
                request.getProvider());

        payment.setProviderPaymentId(
                request.getProviderPaymentId());

        payment.setPaymentDate(
                OffsetDateTime.now());

        payment.setPaymentStatus(
                PaymentStatus.SUCCESS);

        payment.setIdempotencyKey(
                UUID.randomUUID()
                        .toString());

        PaymentEntity saved =
                paymentRepository.save(
                        payment);

        invoice.setStatus(
                InvoiceStatus.PAID);

        invoice.setPaidAt(
                OffsetDateTime.now());

        invoiceRepository.save(
                invoice);

        return mapPayment(
                saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(
            UUID paymentId) {

        PaymentEntity payment =
                paymentRepository
                        .findById(paymentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payment not found"));

        return mapPayment(
                payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse>
    getPaymentsByInvoice(
            UUID invoiceId) {

        return paymentRepository
                .findByInvoice_InvoiceId(
                        invoiceId)
                .stream()
                .map(this::mapPayment)
                .toList();
    }

    private PaymentResponse mapPayment(
            PaymentEntity entity) {

        PaymentResponse response =
                new PaymentResponse();

        response.setPaymentId(
                entity.getPaymentId());

        response.setInvoiceId(
                entity.getInvoice()
                        .getInvoiceId());

        response.setCustomerId(
                entity.getCustomer()
                        .getCustomerId());

        response.setAmount(
                entity.getAmount());

        response.setPaymentMethod(
                entity.getPaymentMethod());

        response.setPaymentStatus(
                entity.getPaymentStatus());

        response.setProvider(
                entity.getProvider());

        response.setProviderPaymentId(
                entity.getProviderPaymentId());

        response.setPaymentDate(
                entity.getPaymentDate());

        return response;
    }
}