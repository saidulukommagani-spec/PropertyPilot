package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "marketplace_commissions")
@Getter
@Setter
public class MarketplaceCommissionEntity extends AuditableEntity {

    @Id
    @Column(name = "marketplace_commission_id")
    private UUID marketplaceCommissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marketplace_listing_id", nullable = false)
    private MarketplaceListingEntity marketplaceListing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marketplace_inquiry_id")
    private MarketplaceInquiryEntity marketplaceInquiry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private InvoiceEntity invoice;

    @Column(name = "basis_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal basisAmount;

    @Column(name = "rate_percentage", nullable = false, precision = 7, scale = 4)
    private BigDecimal ratePercentage;

    @Column(name = "commission_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal commissionAmount;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private VendorEntity vendor;

    @Column(name = "settled_at")
    private OffsetDateTime settledAt;
}