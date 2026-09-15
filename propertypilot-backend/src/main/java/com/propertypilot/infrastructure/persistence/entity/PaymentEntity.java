package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class PaymentEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id")
    private UUID paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "customer_id",
            nullable = false)
    private CustomerEntity customer;

    @Column(name = "contract_id")
    private UUID contractId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private InvoiceEntity invoice;

    @Column(name = "provider")
    private String provider;

    @Column(name = "provider_payment_id")
    private String providerPaymentId;

    @Column(
            name = "idempotency_key",
            nullable = false)
    private String idempotencyKey;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(
            name = "currency_code",
            columnDefinition = "char(3)")
    private String currencyCode;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "payment_date")
    private OffsetDateTime paymentDate;

    @Column(name = "failure_reason")
    private String failureReason;

    @Version
    @Column(name = "version")
    private Long version;

    // Generate Getters and Setters
}