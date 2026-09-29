package com.propertypilot.application.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class FinalizeDealRequest {

    private BigDecimal finalDealAmount;

    private OffsetDateTime registrationDate;

    private String remarks;

    public BigDecimal getFinalDealAmount() {
        return finalDealAmount;
    }

    public void setFinalDealAmount(
            BigDecimal finalDealAmount) {
        this.finalDealAmount = finalDealAmount;
    }

    public OffsetDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(
            OffsetDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(
            String remarks) {
        this.remarks = remarks;
    }
}