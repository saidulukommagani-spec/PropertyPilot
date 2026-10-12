package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @Column(name = "locality_name", length = 120)
    private String localityName;

    @Column(name = "city", length = 120)
    private String city;

    @Column(name = "district", length = 120)
    private String district;

    @Column(name = "state", length = 120)
    private String state;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode = "IN";

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

    public String getLocalityName() { return localityName; }
    public void setLocalityName(String localityName) { this.localityName = localityName; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

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
