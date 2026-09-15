package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "pricing_discount_applications")
public class PricingDiscountApplicationEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "discount_application_id")
    private UUID discountApplicationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "pricing_estimate_id",
            nullable = false
    )
    private PricingEstimateEntity pricingEstimate;

    @Column(name = "coupon_id")
    private UUID couponId;

    @Column(
            name = "discount_type",
            nullable = false
    )
    private String discountType;

    @Column(
            name = "description",
            length = 500
    )
    private String description;

    @Column(
            name = "amount",
            nullable = false
    )
    private BigDecimal amount;

    @Column(name = "metadata")
    private String metadata;

    @Version
    @Column(
            name = "version",
            nullable = false
    )
    private Long version = 0L;

    public UUID getDiscountApplicationId() {
        return discountApplicationId;
    }

    public void setDiscountApplicationId(UUID discountApplicationId) {
        this.discountApplicationId = discountApplicationId;
    }

    public PricingEstimateEntity getPricingEstimate() {
        return pricingEstimate;
    }

    public void setPricingEstimate(
            PricingEstimateEntity pricingEstimate) {
        this.pricingEstimate = pricingEstimate;
    }

    public UUID getCouponId() {
        return couponId;
    }

    public void setCouponId(UUID couponId) {
        this.couponId = couponId;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(
            BigDecimal amount) {
        this.amount = amount;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(
            String metadata) {
        this.metadata = metadata;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(
            Long version) {
        this.version = version;
    }
}