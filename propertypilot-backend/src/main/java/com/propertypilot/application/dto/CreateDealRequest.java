package com.propertypilot.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateDealRequest {

    private UUID propertyId;

    private UUID sellerCustomerId;

    private UUID leadId;

    private String dealType;

    private BigDecimal expectedAmount;

    private BigDecimal sellerCommissionPercent;

    private BigDecimal buyerCommissionPercent;

    private String remarks;

    public UUID getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(
            UUID propertyId) {
        this.propertyId = propertyId;
    }

    public UUID getSellerCustomerId() {
        return sellerCustomerId;
    }

    public void setSellerCustomerId(
            UUID sellerCustomerId) {
        this.sellerCustomerId =
                sellerCustomerId;
    }

    public UUID getLeadId() {
        return leadId;
    }

    public void setLeadId(
            UUID leadId) {
        this.leadId = leadId;
    }

    public String getDealType() {
        return dealType;
    }

    public void setDealType(
            String dealType) {
        this.dealType = dealType;
    }

    public BigDecimal getExpectedAmount() {
        return expectedAmount;
    }

    public void setExpectedAmount(
            BigDecimal expectedAmount) {
        this.expectedAmount =
                expectedAmount;
    }

    public BigDecimal getSellerCommissionPercent() {
        return sellerCommissionPercent;
    }

    public void setSellerCommissionPercent(
            BigDecimal sellerCommissionPercent) {
        this.sellerCommissionPercent =
                sellerCommissionPercent;
    }

    public BigDecimal getBuyerCommissionPercent() {
        return buyerCommissionPercent;
    }

    public void setBuyerCommissionPercent(
            BigDecimal buyerCommissionPercent) {
        this.buyerCommissionPercent =
                buyerCommissionPercent;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(
            String remarks) {
        this.remarks = remarks;
    }
    private String dealSource;

private Boolean exclusiveListing;

public String getDealSource() {
    return dealSource;
}

public void setDealSource(String dealSource) {
    this.dealSource = dealSource;
}

public Boolean getExclusiveListing() {
    return exclusiveListing;
}

public void setExclusiveListing(Boolean exclusiveListing) {
    this.exclusiveListing = exclusiveListing;
}
}