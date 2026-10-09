package com.propertypilot.application.dto;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateDealRequest {

    @NotNull(message = "Property is required")
    private UUID propertyId;

    @NotNull(message = "Seller is required")
    private UUID sellerCustomerId;

    private UUID leadId;

    @NotBlank(message = "Deal type is required")
    private String dealType;

    @NotNull(message = "Expected deal amount is required")
    @DecimalMin(value = "0.01", message = "Expected deal amount must be greater than zero")
    @Digits(integer = 15, fraction = 2, message = "Expected deal amount must have at most 2 decimal places")
    private BigDecimal expectedAmount;

    @DecimalMin(value = "0", message = "Seller commission cannot be negative")
    @DecimalMax(value = "100", message = "Seller commission cannot exceed 100 percent")
    @Digits(integer = 3, fraction = 2, message = "Seller commission must have at most 2 decimal places")
    private BigDecimal sellerCommissionPercent;

    @DecimalMin(value = "0", message = "Buyer commission cannot be negative")
    @DecimalMax(value = "100", message = "Buyer commission cannot exceed 100 percent")
    @Digits(integer = 3, fraction = 2, message = "Buyer commission must have at most 2 decimal places")
    private BigDecimal buyerCommissionPercent;

    @Size(max = 2000, message = "Deal notes cannot exceed 2,000 characters")
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
