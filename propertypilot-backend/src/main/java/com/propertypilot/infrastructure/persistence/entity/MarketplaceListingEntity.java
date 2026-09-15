package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "marketplace_listings")
@Getter
@Setter
public class MarketplaceListingEntity extends AuditableEntity {

    @Id
    @Column(name = "marketplace_listing_id")
    private UUID marketplaceListingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_customer_id", nullable = false)
    private CustomerEntity sellerCustomer;

    @Column(name = "listing_type", nullable = false, length = 20)
    private String listingType;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "asking_price", precision = 15, scale = 2)
    private BigDecimal askingPrice;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;
}