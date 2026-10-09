package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "coupons")
public class CouponEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "coupon_id")
    private UUID couponId;

    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "description")
    private String description;

    @Column(name = "discount_type")
    private String discountType;

    @Column(name = "discount_value")
    private BigDecimal discountValue;

    @Column(name = "maximum_discount")
    private BigDecimal maximumDiscount;

    @Column(name = "minimum_order_amount")
    private BigDecimal minimumOrderAmount;

    @Column(name = "valid_from")
    private OffsetDateTime validFrom;

    @Column(name = "valid_to")
    private OffsetDateTime validTo;

    @Column(name = "redemption_limit")
    private Integer redemptionLimit;

    @Column(name = "per_customer_limit")
    private Integer perCustomerLimit;

    @Column(name = "status")
    private String status;

    @Version
    @Column(name = "version")
    private Long version;

    public UUID getCouponId() { return couponId; }
    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String value) { couponCode = value; }
    public String getDescription() { return description; }
    public void setDescription(String value) { description = value; }
    public String getDiscountType() { return discountType; }
    public void setDiscountType(String value) { discountType = value; }
    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal value) { discountValue = value; }
    public BigDecimal getMaximumDiscount() { return maximumDiscount; }
    public void setMaximumDiscount(BigDecimal value) { maximumDiscount = value; }
    public BigDecimal getMinimumOrderAmount() { return minimumOrderAmount; }
    public void setMinimumOrderAmount(BigDecimal value) { minimumOrderAmount = value; }
    public OffsetDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(OffsetDateTime value) { validFrom = value; }
    public OffsetDateTime getValidTo() { return validTo; }
    public void setValidTo(OffsetDateTime value) { validTo = value; }
    public Integer getRedemptionLimit() { return redemptionLimit; }
    public void setRedemptionLimit(Integer value) { redemptionLimit = value; }
    public Integer getPerCustomerLimit() { return perCustomerLimit; }
    public void setPerCustomerLimit(Integer value) { perCustomerLimit = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { status = value; }
}
