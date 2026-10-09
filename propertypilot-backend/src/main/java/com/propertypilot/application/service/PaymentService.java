package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreatePaymentRequest;
import com.propertypilot.application.dto.PaymentResponse;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    PaymentResponse recordPayment(
            UUID invoiceId,
            CreatePaymentRequest request);

    PaymentResponse confirmSettlement(String rawBody, String signature);

    PaymentResponse getPayment(
            UUID paymentId);

    List<PaymentResponse> getAllPayments();

    List<PaymentResponse>
    getPaymentsByInvoice(
            UUID invoiceId);
}
