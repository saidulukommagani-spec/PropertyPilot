package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "coupon_redemptions")
public class CouponRedemptionEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "coupon_redemption_id")
    private UUID couponRedemptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private CouponEntity coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pricing_estimate_id")
    private PricingEstimateEntity pricingEstimate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private PaymentEntity payment;

    @Column(name = "redeemed_at", nullable = false)
    private OffsetDateTime redeemedAt;

    @Column(name = "discount_amount", nullable = false)
    private BigDecimal discountAmount;

    @Column(name = "status", nullable = false)
    private String status;

    @Version
    @Column(name = "version")
    private Long version;

    public UUID getCouponRedemptionId() {
        return couponRedemptionId;
    }

    public void setCouponRedemptionId(UUID couponRedemptionId) {
        this.couponRedemptionId = couponRedemptionId;
    }

    public CouponEntity getCoupon() {
        return coupon;
    }

    public void setCoupon(CouponEntity coupon) {
        this.coupon = coupon;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public PricingEstimateEntity getPricingEstimate() {
        return pricingEstimate;
    }

    public void setPricingEstimate(PricingEstimateEntity pricingEstimate) {
        this.pricingEstimate = pricingEstimate;
    }

    public PaymentEntity getPayment() {
        return payment;
    }

    public void setPayment(PaymentEntity payment) {
        this.payment = payment;
    }

    public OffsetDateTime getRedeemedAt() {
        return redeemedAt;
    }

    public void setRedeemedAt(OffsetDateTime redeemedAt) {
        this.redeemedAt = redeemedAt;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}