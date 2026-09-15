package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "marketplace_inquiries")
@Getter
@Setter
public class MarketplaceInquiryEntity extends AuditableEntity {

    @Id
    @Column(name = "marketplace_inquiry_id")
    private UUID marketplaceInquiryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marketplace_listing_id", nullable = false)
    private MarketplaceListingEntity marketplaceListing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interested_customer_id", nullable = false)
    private CustomerEntity interestedCustomer;

    @Column(name = "message")
    private String message;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "responded_at")
    private OffsetDateTime respondedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    private LeadEntity lead;
}