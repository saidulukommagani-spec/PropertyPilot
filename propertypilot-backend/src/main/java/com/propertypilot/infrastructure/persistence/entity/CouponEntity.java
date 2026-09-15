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

    // Generate getters/setters
}