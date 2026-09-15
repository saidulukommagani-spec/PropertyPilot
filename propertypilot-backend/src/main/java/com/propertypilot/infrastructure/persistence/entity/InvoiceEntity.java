package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "invoices")
public class InvoiceEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "invoice_id")
    private UUID invoiceId;

    @Column(name = "contract_id")
    private UUID contractId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "customer_id",
            nullable = false)
    private CustomerEntity customer;

    @Column(
            name = "invoice_number",
            nullable = false)
    private String invoiceNumber;

    @Column(
            name = "currency_code",
            columnDefinition = "char(3)")
    private String currencyCode;

    @Column(name = "invoice_amount")
    private BigDecimal invoiceAmount;

    @Column(name = "tax_amount")
    private BigDecimal taxAmount;

    @Column(name = "status")
    private String status;

    @Column(name = "issued_at")
    private OffsetDateTime issuedAt;

    @Column(name = "due_at")
    private OffsetDateTime dueAt;

    @Column(name = "paid_at")
    private OffsetDateTime paidAt;

    @Version
    @Column(name = "version")
    private Long version;

    // Generate Getters and Setters
}