package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "property_locations")
public class PropertyLocationEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "property_location_id",
            nullable = false,
            updatable = false
    )
    private UUID propertyLocationId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "property_id",
            nullable = false,
            unique = true
    )
    private Property property;

    @Column(name = "locality_id")
    private UUID localityId;

    @Column(name = "coverage_zone_id")
    private UUID coverageZoneId;

    @Column(
            name = "address_line_1",
            nullable = false
    )
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    @Column(
            name = "latitude",
            precision = 9,
            scale = 6
    )
    private BigDecimal latitude;

    @Column(
            name = "longitude",
            precision = 9,
            scale = 6
    )
    private BigDecimal longitude;

    @Column(
            name = "location_accuracy_score",
            precision = 5,
            scale = 2
    )
    private BigDecimal locationAccuracyScore;

    @Column(
            name = "map_verification_status",
            nullable = false,
            length = 30
    )
    private String mapVerificationStatus;

    @Version
    @Column(
            name = "version",
            nullable = false
    )
    private Long version = 0L;

    public UUID getPropertyLocationId() {
        return propertyLocationId;
    }

    public void setPropertyLocationId(UUID propertyLocationId) {
        this.propertyLocationId = propertyLocationId;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public UUID getLocalityId() {
        return localityId;
    }

    public void setLocalityId(UUID localityId) {
        this.localityId = localityId;
    }

    public UUID getCoverageZoneId() {
        return coverageZoneId;
    }

    public void setCoverageZoneId(UUID coverageZoneId) {
        this.coverageZoneId = coverageZoneId;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLocationAccuracyScore() {
        return locationAccuracyScore;
    }

    public void setLocationAccuracyScore(BigDecimal locationAccuracyScore) {
        this.locationAccuracyScore = locationAccuracyScore;
    }

    public String getMapVerificationStatus() {
        return mapVerificationStatus;
    }

    public void setMapVerificationStatus(String mapVerificationStatus) {
        this.mapVerificationStatus = mapVerificationStatus;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}