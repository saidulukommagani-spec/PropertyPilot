package com.propertypilot.application.dto;

import com.propertypilot.domain.enums.PaymentMethod;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(
        name = "CreatePaymentRequest",
        description = "Request to record a payment against an invoice")
public class CreatePaymentRequest {

    @Schema(
            description = "Payment amount",
            example = "2500.00",
            requiredMode =
                    Schema.RequiredMode.REQUIRED)
    @NotNull(
            message = "Amount is required")
    @DecimalMin(
            value = "0.01",
            message = "Amount must be greater than zero")
    private BigDecimal amount;

    @Schema(
            description = "Payment method",
            example = "UPI",
            requiredMode =
                    Schema.RequiredMode.REQUIRED)
    @NotNull(
            message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @Schema(
            description = "Payment provider",
            example = "PhonePe")
    @Size(
            max = 100,
            message =
                    "Provider cannot exceed 100 characters")
    private String provider;

    @Schema(
            description = "External provider payment id",
            example = "TXN123456789")
    @Size(
            max = 100,
            message =
                    "Provider payment id cannot exceed 100 characters")
    private String providerPaymentId;

    @Schema(
            description = "Remarks",
            example = "Full payment received")
    @Size(
            max = 500,
            message =
                    "Remarks cannot exceed 500 characters")
    private String remarks;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(
            BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(
            PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(
            String provider) {
        this.provider = provider;
    }

    public String getProviderPaymentId() {
        return providerPaymentId;
    }

    public void setProviderPaymentId(
            String providerPaymentId) {
        this.providerPaymentId =
                providerPaymentId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(
            String remarks) {
        this.remarks = remarks;
    }
}