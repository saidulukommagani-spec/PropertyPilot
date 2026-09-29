package com.propertypilot.infrastructure.persistence.entity;

import com.propertypilot.domain.enums.DealSource;
import com.propertypilot.domain.enums.DealStatus;
import com.propertypilot.domain.enums.DealType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "deals")
public class DealEntity
        extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "deal_id")
    private UUID dealId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "property_id",
            nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "seller_customer_id",
            nullable = false)
    private CustomerEntity seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "lead_id")
    private LeadEntity lead;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "deal_type",
            nullable = false)
    private DealType dealType;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "deal_status",
            nullable = false)
    private DealStatus dealStatus;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "deal_source",
            nullable = false)
    private DealSource dealSource;

    @Column(name = "asking_price")
    private BigDecimal askingPrice;

    @Column(name = "expected_price")
    private BigDecimal expectedPrice;

    @Column(name = "final_deal_amount")
    private BigDecimal finalDealAmount;

    @Column(name = "buyer_commission_percent")
    private BigDecimal buyerCommissionPercent;

    @Column(name = "seller_commission_percent")
    private BigDecimal sellerCommissionPercent;

    @Column(name = "buyer_commission_amount")
    private BigDecimal buyerCommissionAmount;

    @Column(name = "seller_commission_amount")
    private BigDecimal sellerCommissionAmount;

    @Column(name = "selected_buyer_id")
    private UUID selectedBuyerId;

    @Column(name = "exclusive_listing")
    private Boolean exclusiveListing;

    @Column(name = "registration_date")
    private OffsetDateTime registrationDate;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "cancelled_at")
    private OffsetDateTime cancelledAt;

    @Column(name = "remarks")
    private String remarks;

    @Version
    @Column(name = "version")
    private Long version;

    public UUID getDealId() {
        return dealId;
    }

    public void setDealId(UUID dealId) {
        this.dealId = dealId;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public CustomerEntity getSeller() {
        return seller;
    }

    public void setSeller(CustomerEntity seller) {
        this.seller = seller;
    }

    public LeadEntity getLead() {
        return lead;
    }

    public void setLead(LeadEntity lead) {
        this.lead = lead;
    }

    public DealType getDealType() {
        return dealType;
    }

    public void setDealType(
            DealType dealType) {
        this.dealType = dealType;
    }

    public DealStatus getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(
            DealStatus dealStatus) {
        this.dealStatus = dealStatus;
    }

    public DealSource getDealSource() {
        return dealSource;
    }

    public void setDealSource(
            DealSource dealSource) {
        this.dealSource = dealSource;
    }

    public BigDecimal getAskingPrice() {
        return askingPrice;
    }

    public void setAskingPrice(
            BigDecimal askingPrice) {
        this.askingPrice = askingPrice;
    }

    public BigDecimal getExpectedPrice() {
        return expectedPrice;
    }

    public void setExpectedPrice(
            BigDecimal expectedPrice) {
        this.expectedPrice = expectedPrice;
    }

    public BigDecimal getFinalDealAmount() {
        return finalDealAmount;
    }

    public void setFinalDealAmount(
            BigDecimal finalDealAmount) {
        this.finalDealAmount = finalDealAmount;
    }

    public BigDecimal getBuyerCommissionPercent() {
        return buyerCommissionPercent;
    }

    public void setBuyerCommissionPercent(
            BigDecimal buyerCommissionPercent) {
        this.buyerCommissionPercent =
                buyerCommissionPercent;
    }

    public BigDecimal getSellerCommissionPercent() {
        return sellerCommissionPercent;
    }

    public void setSellerCommissionPercent(
            BigDecimal sellerCommissionPercent) {
        this.sellerCommissionPercent =
                sellerCommissionPercent;
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

    public UUID getSelectedBuyerId() {
        return selectedBuyerId;
    }

    public void setSelectedBuyerId(
            UUID selectedBuyerId) {
        this.selectedBuyerId =
                selectedBuyerId;
    }

    public Boolean getExclusiveListing() {
        return exclusiveListing;
    }

    public void setExclusiveListing(
            Boolean exclusiveListing) {
        this.exclusiveListing =
                exclusiveListing;
    }

    public OffsetDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(
            OffsetDateTime registrationDate) {
        this.registrationDate =
                registrationDate;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(
            OffsetDateTime completedAt) {
        this.completedAt =
                completedAt;
    }

    public OffsetDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(
            OffsetDateTime cancelledAt) {
        this.cancelledAt =
                cancelledAt;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(
            String remarks) {
        this.remarks = remarks;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(
            Long version) {
        this.version = version;
    }
}