package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.PaymentMethod;
import com.propertypilot.domain.enums.PaymentStatus;

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

//     @Column(name = "contract_id")
//     private UUID contractId;

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

  
    @Column(
            name = "currency_code",
            columnDefinition = "char(3)")
    private String currencyCode;

    @Column(name = "amount")
    private BigDecimal amount;

   @Enumerated(EnumType.STRING)
@Column(name = "payment_status")
private PaymentStatus paymentStatus;

    @Column(name = "payment_date")
    private OffsetDateTime paymentDate;

    @Column(name = "failure_reason")
    private String failureReason;

    @Version
    @Column(name = "version")
    private Long version;

@Enumerated(EnumType.STRING)
@Column(name = "entity_type")
private BillingEntityType entityType;

@Column(name = "entity_id")
private UUID entityId;

public UUID getPaymentId() {
    return paymentId;
}

public void setPaymentId(UUID paymentId) {
    this.paymentId = paymentId;
}

public CustomerEntity getCustomer() {
    return customer;
}

public void setCustomer(CustomerEntity customer) {
    this.customer = customer;
}

public InvoiceEntity getInvoice() {
    return invoice;
}

public void setInvoice(InvoiceEntity invoice) {
    this.invoice = invoice;
}

public String getProvider() {
    return provider;
}

public void setProvider(String provider) {
    this.provider = provider;
}

public String getProviderPaymentId() {
    return providerPaymentId;
}

public void setProviderPaymentId(String providerPaymentId) {
    this.providerPaymentId = providerPaymentId;
}

public String getIdempotencyKey() {
    return idempotencyKey;
}

public void setIdempotencyKey(String idempotencyKey) {
    this.idempotencyKey = idempotencyKey;
}

@Enumerated(EnumType.STRING)
@Column(name = "payment_method")
private PaymentMethod paymentMethod;

public PaymentMethod getPaymentMethod() {
    return paymentMethod;
}

public void setPaymentMethod(
        PaymentMethod paymentMethod) {
    this.paymentMethod = paymentMethod;
}

public String getCurrencyCode() {
    return currencyCode;
}

public void setCurrencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
}

public BigDecimal getAmount() {
    return amount;
}

public void setAmount(BigDecimal amount) {
    this.amount = amount;
}

public OffsetDateTime getPaymentDate() {
    return paymentDate;
}

public void setPaymentDate(OffsetDateTime paymentDate) {
    this.paymentDate = paymentDate;
}

public String getFailureReason() {
    return failureReason;
}

public void setFailureReason(String failureReason) {
    this.failureReason = failureReason;
}

public Long getVersion() {
    return version;
}

public void setVersion(Long version) {
    this.version = version;
}


public void setPaymentStatus(PaymentStatus paymentStatus) {
    this.paymentStatus = paymentStatus;
}
public PaymentStatus getPaymentStatus() {
    return paymentStatus;
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

}