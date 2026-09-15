package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "quotations")
public class QuotationEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "quotation_id")
    private UUID quotationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "service_request_id",
            nullable = false)
    private ServiceRequestEntity serviceRequest;

    /**
     * VendorEntity will be added later.
     */
    @Column(name = "vendor_id")
    private UUID vendorId;

    /**
     * PricingEstimateEntity will be added later.
     */
    @Column(name = "pricing_estimate_id")
    private UUID pricingEstimateId;

    @Column(name = "quotation_number")
    private String quotationNumber;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(
            name = "currency_code",
            columnDefinition = "char(3)")
    private String currencyCode;

    @Column(name = "status")
    private String status;

    @Column(name = "valid_until")
    private OffsetDateTime validUntil;

    @Column(name = "terms")
    private String terms;

    @Version
    @Column(name = "version")
    private Long version;

    public UUID getQuotationId() {
        return quotationId;
    }

    public void setQuotationId(UUID quotationId) {
        this.quotationId = quotationId;
    }

    public ServiceRequestEntity getServiceRequest() {
        return serviceRequest;
    }

    public void setServiceRequest(
            ServiceRequestEntity serviceRequest) {
        this.serviceRequest = serviceRequest;
    }

    public UUID getVendorId() {
        return vendorId;
    }

    public void setVendorId(UUID vendorId) {
        this.vendorId = vendorId;
    }

    public UUID getPricingEstimateId() {
        return pricingEstimateId;
    }

    public void setPricingEstimateId(
            UUID pricingEstimateId) {
        this.pricingEstimateId = pricingEstimateId;
    }

    public String getQuotationNumber() {
        return quotationNumber;
    }

    public void setQuotationNumber(
            String quotationNumber) {
        this.quotationNumber = quotationNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(
            BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(
            String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {
        this.status = status;
    }

    public OffsetDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(
            OffsetDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(
            String terms) {
        this.terms = terms;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(
            Long version) {
        this.version = version;
    }
}