package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.InvoiceStatus;
import com.propertypilot.domain.enums.InvoiceType;

@Entity
@Table(name = "invoices")
public class InvoiceEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "invoice_id")
    private UUID invoiceId;

//     @Column(name = "contract_id")
//     private UUID contractId;

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

   @Enumerated(EnumType.STRING)
@Column(name = "status")
private InvoiceStatus status;

    @Column(name = "issued_at")
    private OffsetDateTime issuedAt;

    @Column(name = "due_at")
    private OffsetDateTime dueAt;

    @Column(name = "paid_at")
    private OffsetDateTime paidAt;

    @Version
    @Column(name = "version")
    private Long version;

 @Enumerated(EnumType.STRING)
@Column(name = "entity_type")
private BillingEntityType entityType;

@Column(name = "entity_id")
private UUID entityId;

@Enumerated(EnumType.STRING)
@Column(name = "invoice_type")
private InvoiceType invoiceType;

@Column(name = "description")
private String description;

public UUID getInvoiceId() {
    return invoiceId;
}

public void setInvoiceId(UUID invoiceId) {
    this.invoiceId = invoiceId;
}

public CustomerEntity getCustomer() {
    return customer;
}

public void setCustomer(CustomerEntity customer) {
    this.customer = customer;
}

public String getInvoiceNumber() {
    return invoiceNumber;
}

public void setInvoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
}

public String getCurrencyCode() {
    return currencyCode;
}

public void setCurrencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
}

public BigDecimal getInvoiceAmount() {
    return invoiceAmount;
}

public void setInvoiceAmount(BigDecimal invoiceAmount) {
    this.invoiceAmount = invoiceAmount;
}

public BigDecimal getTaxAmount() {
    return taxAmount;
}

public void setTaxAmount(BigDecimal taxAmount) {
    this.taxAmount = taxAmount;
}

public InvoiceStatus getStatus() {
    return status;
}

public void setStatus(InvoiceStatus status) {
    this.status = status;
}

public OffsetDateTime getIssuedAt() {
    return issuedAt;
}

public void setIssuedAt(OffsetDateTime issuedAt) {
    this.issuedAt = issuedAt;
}

public OffsetDateTime getDueAt() {
    return dueAt;
}

public void setDueAt(OffsetDateTime dueAt) {
    this.dueAt = dueAt;
}

public OffsetDateTime getPaidAt() {
    return paidAt;
}

public void setPaidAt(OffsetDateTime paidAt) {
    this.paidAt = paidAt;
}

public Long getVersion() {
    return version;
}

public void setVersion(Long version) {
    this.version = version;
}

public BillingEntityType getEntityType() {
    return entityType;
}

public void setEntityType(BillingEntityType entityType) {
    this.entityType = entityType;
}

public UUID getEntityId() {
    return entityId;
}

public void setEntityId(UUID entityId) {
    this.entityId = entityId;
}

public InvoiceType getInvoiceType() {
    return invoiceType;
}

public void setInvoiceType(InvoiceType invoiceType) {
    this.invoiceType = invoiceType;
}

public String getDescription() {
    return description;
}

public void setDescription(String description) {
    this.description = description;
}

}