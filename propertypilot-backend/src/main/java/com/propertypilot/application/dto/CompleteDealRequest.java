package com.propertypilot.application.dto;

import java.math.BigDecimal;

public class CompleteDealRequest {

    private BigDecimal finalDealAmount;

    public BigDecimal getFinalDealAmount() {
        return finalDealAmount;
    }

    public void setFinalDealAmount(
            BigDecimal finalDealAmount) {
        this.finalDealAmount =
                finalDealAmount;
    }
}