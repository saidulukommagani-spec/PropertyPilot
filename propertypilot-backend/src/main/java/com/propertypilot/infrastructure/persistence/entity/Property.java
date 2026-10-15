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

    @Column(name = "property_type", nullable = false, length = 100)
    private String propertyType;

    @Column(name = "survey_number", length = 120)
    private String surveyNumber;

    @Column(name = "area_value", precision = 15, scale = 2)
    private BigDecimal areaValue;

    @Column(name = "area_unit", length = 20)
    private String areaUnit;

    @Column(name = "area_guntas")
    private Integer areaGuntas;

    @Column(name = "listing_status", nullable = false, length = 50)
    private String listingStatus;

    @Column(name = "price", precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "archived_at")
    private Instant archivedAt;

    @Column(name = "archived_by")
    private UUID archivedBy;
}
