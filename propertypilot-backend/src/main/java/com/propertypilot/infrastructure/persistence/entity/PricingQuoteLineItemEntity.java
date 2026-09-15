package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "pricing_quote_line_items")
public class PricingQuoteLineItemEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "quote_line_item_id")
    private UUID quoteLineItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "pricing_estimate_id",
            nullable = false
    )
    private PricingEstimateEntity pricingEstimate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private ServiceEntity service;

    @Column(
            name = "line_type",
            nullable = false
    )
    private String lineType;

    @Column(
            name = "description",
            nullable = false,
            length = 500
    )
    private String description;

    @Column(
            name = "quantity",
            nullable = false
    )
    private BigDecimal quantity;

    @Column(
            name = "unit_amount",
            nullable = false
    )
    private BigDecimal unitAmount;

    @Column(
            name = "line_amount",
            nullable = false
    )
    private BigDecimal lineAmount;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Version
    @Column(
            name = "version",
            nullable = false
    )
    private Long version = 0L;

    public UUID getQuoteLineItemId() {
        return quoteLineItemId;
    }

    public void setQuoteLineItemId(UUID quoteLineItemId) {
        this.quoteLineItemId = quoteLineItemId;
    }

    public PricingEstimateEntity getPricingEstimate() {
        return pricingEstimate;
    }

    public void setPricingEstimate(
            PricingEstimateEntity pricingEstimate) {
        this.pricingEstimate = pricingEstimate;
    }

    public ServiceEntity getService() {
        return service;
    }

    public void setService(ServiceEntity service) {
        this.service = service;
    }

    public String getLineType() {
        return lineType;
    }

    public void setLineType(String lineType) {
        this.lineType = lineType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {
        this.description = description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(
            BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitAmount() {
        return unitAmount;
    }

    public void setUnitAmount(
            BigDecimal unitAmount) {
        this.unitAmount = unitAmount;
    }

    public BigDecimal getLineAmount() {
        return lineAmount;
    }

    public void setLineAmount(
            BigDecimal lineAmount) {
        this.lineAmount = lineAmount;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(
            Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(
            Long version) {
        this.version = version;
    }
}