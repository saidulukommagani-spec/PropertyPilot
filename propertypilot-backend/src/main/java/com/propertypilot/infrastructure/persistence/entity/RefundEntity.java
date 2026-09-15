package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "refunds")
public class RefundEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "refund_id")
    private UUID refundId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "payment_id",
            nullable = false)
    private PaymentEntity payment;

    @Column(
            name = "refund_reference",
            nullable = false)
    private String refundReference;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(
            name = "currency_code",
            columnDefinition = "char(3)")
    private String currencyCode;

    @Column(
            name = "reason_code",
            nullable = false)
    private String reasonCode;

    @Column(name = "reason_details")
    private String reasonDetails;

    @Column(name = "status")
    private String status;

    @Column(name = "provider_refund_id")
    private String providerRefundId;

    @Column(name = "requested_at")
    private OffsetDateTime requestedAt;

    @Column(name = "processed_at")
    private OffsetDateTime processedAt;

    @Column(name = "failed_at")
    private OffsetDateTime failedAt;

    @Version
    @Column(name = "version")
    private Long version;

    // Generate Getters and Setters
}