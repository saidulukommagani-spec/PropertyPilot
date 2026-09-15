package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "gps_verifications")
public class GpsVerificationEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "gps_verification_id",
            nullable = false,
            updatable = false
    )
    private UUID gpsVerificationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "visit_id",
            nullable = false
    )
    private VisitEntity visit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_location_id")
    private PropertyLocationEntity propertyLocation;

    @Column(
            name = "latitude",
            nullable = false,
            precision = 9,
            scale = 6
    )
    private BigDecimal latitude;

    @Column(
            name = "longitude",
            nullable = false,
            precision = 9,
            scale = 6
    )
    private BigDecimal longitude;

    @Column(
            name = "validation_status",
            nullable = false,
            length = 30
    )
    private String validationStatus;

    @Column(
            name = "distance_from_expected_km",
            precision = 8,
            scale = 3
    )
    private BigDecimal distanceFromExpectedKm;

    @Column(
            name = "captured_at",
            nullable = false
    )
    private OffsetDateTime capturedAt;

    @Column(name = "validated_at")
    private OffsetDateTime validatedAt;

    @Version
    @Column(
            name = "version",
            nullable = false
    )
    private Long version = 0L;
}