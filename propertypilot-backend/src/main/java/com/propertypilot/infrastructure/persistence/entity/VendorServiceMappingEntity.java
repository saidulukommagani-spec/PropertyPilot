package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "vendor_service_mappings")
public class VendorServiceMappingEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "vendor_service_mapping_id")
    private UUID vendorServiceMappingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private VendorEntity vendor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;

    /**
     * CoverageZoneEntity will be added later.
     */
    @Column(name = "coverage_zone_id")
    private UUID coverageZoneId;

    @Column(name = "agreed_rate")
    private BigDecimal agreedRate;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "coverage_scope", columnDefinition = "jsonb")
    private String coverageScope;

    @Column(name = "required_verification_level")
    private String requiredVerificationLevel;

    @Column(name = "capacity_status", nullable = false)
    private String capacityStatus;

    @Version
    @Column(name = "version")
    private Long version;

    public UUID getVendorServiceMappingId() {
        return vendorServiceMappingId;
    }

    public void setVendorServiceMappingId(UUID vendorServiceMappingId) {
        this.vendorServiceMappingId = vendorServiceMappingId;
    }

    public VendorEntity getVendor() {
        return vendor;
    }

    public void setVendor(VendorEntity vendor) {
        this.vendor = vendor;
    }

    public ServiceEntity getService() {
        return service;
    }

    public void setService(ServiceEntity service) {
        this.service = service;
    }

    public UUID getCoverageZoneId() {
        return coverageZoneId;
    }

    public void setCoverageZoneId(UUID coverageZoneId) {
        this.coverageZoneId = coverageZoneId;
    }

    public BigDecimal getAgreedRate() {
        return agreedRate;
    }

    public void setAgreedRate(BigDecimal agreedRate) {
        this.agreedRate = agreedRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCoverageScope() {
        return coverageScope;
    }

    public void setCoverageScope(String coverageScope) {
        this.coverageScope = coverageScope;
    }

    public String getRequiredVerificationLevel() {
        return requiredVerificationLevel;
    }

    public void setRequiredVerificationLevel(String requiredVerificationLevel) {
        this.requiredVerificationLevel = requiredVerificationLevel;
    }

    public String getCapacityStatus() {
        return capacityStatus;
    }

    public void setCapacityStatus(String capacityStatus) {
        this.capacityStatus = capacityStatus;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}