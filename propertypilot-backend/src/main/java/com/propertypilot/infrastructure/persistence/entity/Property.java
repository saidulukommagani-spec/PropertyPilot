package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "properties")
@Getter
@Setter
public class Property extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "property_id")
    private UUID propertyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "customer_id",
            nullable = false
    )
    private CustomerEntity customer;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "property_type", nullable = false)
    private String propertyType;

    @Column(name = "listing_status", nullable = false)
    private String listingStatus;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "archived_at")
    private Instant archivedAt;

    @Column(name = "archived_by")
    private UUID archivedBy;
}