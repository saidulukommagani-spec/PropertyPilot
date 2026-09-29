package com.propertypilot.application.dto;

import com.propertypilot.domain.enums.PaymentMethod;

import java.math.BigDecimal;

public class CreatePaymentRequest {

    private BigDecimal amount;

    private PaymentMethod paymentMethod;

    private String provider;

    private String providerPaymentId;

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
        this.paymentMethod =
                paymentMethod;
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