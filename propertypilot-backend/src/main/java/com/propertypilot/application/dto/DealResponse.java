package com.propertypilot.application.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class DealResponse {

    private UUID dealId;

    private UUID propertyId;

    private UUID sellerCustomerId;

    private UUID leadId;

    private String dealType;

    private String dealStatus;

    private BigDecimal expectedAmount;

    private BigDecimal finalAmount;

    private BigDecimal sellerCommissionPercent;

    private BigDecimal buyerCommissionPercent;

    private BigDecimal totalCommissionAmount;

    private OffsetDateTime createdAt;

    private OffsetDateTime closedAt;

    private String remarks;

    private UUID selectedBuyerCustomerId;

    private String dealSource;

    private Boolean exclusiveListing;

    private BigDecimal buyerCommissionAmount;

    private BigDecimal sellerCommissionAmount;

    public UUID getDealId() {
        return dealId;
    }

    public void setDealId(UUID dealId) {
        this.dealId = dealId;
    }

    public UUID getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(UUID propertyId) {
        this.propertyId = propertyId;
    }

    public UUID getSellerCustomerId() {
        return sellerCustomerId;
    }

    public void setSellerCustomerId(UUID sellerCustomerId) {
        this.sellerCustomerId = sellerCustomerId;
    }

    public UUID getLeadId() {
        return leadId;
    }

    public void setLeadId(UUID leadId) {
        this.leadId = leadId;
    }

    public String getDealType() {
        return dealType;
    }

    public void setDealType(String dealType) {
        this.dealType = dealType;
    }

    public String getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(String dealStatus) {
        this.dealStatus = dealStatus;
    }

    public BigDecimal getExpectedAmount() {
        return expectedAmount;
    }

    public void setExpectedAmount(
            BigDecimal expectedAmount) {
        this.expectedAmount = expectedAmount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(
            BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
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

    public BigDecimal getTotalCommissionAmount() {
        return totalCommissionAmount;
    }

    public void setTotalCommissionAmount(
            BigDecimal totalCommissionAmount) {
        this.totalCommissionAmount =
                totalCommissionAmount;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(
            OffsetDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(
            String remarks) {
        this.remarks = remarks;
    }

    public UUID getSelectedBuyerCustomerId() {
        return selectedBuyerCustomerId;
    }

    public void setSelectedBuyerCustomerId(
            UUID selectedBuyerCustomerId) {
        this.selectedBuyerCustomerId =
                selectedBuyerCustomerId;
    }

    public String getDealSource() {
        return dealSource;
    }

    public void setDealSource(
            String dealSource) {
        this.dealSource = dealSource;
    }

    public Boolean getExclusiveListing() {
        return exclusiveListing;
    }

    public void setExclusiveListing(
            Boolean exclusiveListing) {
        this.exclusiveListing =
                exclusiveListing;
    }

    public BigDecimal getBuyerCommissionAmount() {
        return buyerCommissionAmount;
    }

    public void setBuyerCommissionAmount(
            BigDecimal buyerCommissionAmount) {
        this.buyerCommissionAmount =
                buyerCommissionAmount;
    }

    public BigDecimal getSellerCommissionAmount() {
        return sellerCommissionAmount;
    }

    public void setSellerCommissionAmount(
            BigDecimal sellerCommissionAmount) {
        this.sellerCommissionAmount =
                sellerCommissionAmount;
    }
}