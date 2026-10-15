package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "services")
public class ServiceEntity
        extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "service_id")
    private UUID serviceId;

    @Column(name = "service_code")
    private String serviceCode;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "category")
    private String category;

    @Column(name = "catalog_group", length = 100)
    private String catalogGroup;

    @Column(name = "catalog_priority", nullable = false, length = 20)
    private String catalogPriority = "CORE";

    @Column(name = "delivery_policy", length = 255)
    private String deliveryPolicy;

    @Column(name = "frequency", length = 120)
    private String frequency;

    @Column(name = "catalog_notes", columnDefinition = "text")
    private String catalogNotes;

    @Column(name = "conditional_property_types", nullable = false, columnDefinition = "text[]")
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.ARRAY)
    private String[] conditionalPropertyTypes = new String[0];

    @Column(name = "base_price")
    private BigDecimal basePrice = BigDecimal.ZERO;

    @Column(name = "active_flag")
    private Boolean activeFlag = true;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "requires_visit", nullable = false)
    private Boolean requiresVisit = false;

    @Column(name = "visit_mandatory", nullable = false)
    private Boolean visitMandatory = false;

    @Column(name = "max_visits_allowed", nullable = false)
    private Integer maxVisitsAllowed = 0;

    @Column(name = "requires_gps", nullable = false)
    private Boolean requiresGps = false;

    @Column(name = "gps_radius_km", nullable = false, precision = 8, scale = 3)
    private BigDecimal gpsRadiusKm = new BigDecimal("0.200");

    @Column(name = "requires_before_photo", nullable = false)
    private Boolean requiresBeforePhoto = false;

    @Column(name = "requires_after_photo", nullable = false)
    private Boolean requiresAfterPhoto = false;

    @Column(name = "requires_signature", nullable = false)
    private Boolean requiresSignature = false;

    @Column(name = "requires_document_upload", nullable = false)
    private Boolean requiresDocumentUpload = false;

    @Column(name = "is_remote", nullable = false)
    private Boolean remote = false;

    @Column(name = "billable", nullable = false)
    private Boolean billable = true;

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCatalogGroup() { return catalogGroup; }
    public void setCatalogGroup(String catalogGroup) { this.catalogGroup = catalogGroup; }
    public String getCatalogPriority() { return catalogPriority; }
    public void setCatalogPriority(String catalogPriority) { this.catalogPriority = catalogPriority; }
    public String getDeliveryPolicy() { return deliveryPolicy; }
    public void setDeliveryPolicy(String deliveryPolicy) { this.deliveryPolicy = deliveryPolicy; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public String getCatalogNotes() { return catalogNotes; }
    public void setCatalogNotes(String catalogNotes) { this.catalogNotes = catalogNotes; }
    public String[] getConditionalPropertyTypes() { return conditionalPropertyTypes; }
    public void setConditionalPropertyTypes(String[] conditionalPropertyTypes) { this.conditionalPropertyTypes = conditionalPropertyTypes; }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public Boolean getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

public Boolean getRequiresVisit() {
    return requiresVisit;
}

public void setRequiresVisit(Boolean requiresVisit) {
    this.requiresVisit = requiresVisit;
}

public Boolean getVisitMandatory() {
    return visitMandatory;
}

public void setVisitMandatory(Boolean visitMandatory) {
    this.visitMandatory = visitMandatory;
}

public Integer getMaxVisitsAllowed() {
    return maxVisitsAllowed;
}

public void setMaxVisitsAllowed(Integer maxVisitsAllowed) {
    this.maxVisitsAllowed = maxVisitsAllowed;
}

public Boolean getRequiresGps() {
    return requiresGps;
}

public void setRequiresGps(Boolean requiresGps) {
    this.requiresGps = requiresGps;
}

public BigDecimal getGpsRadiusKm() { return gpsRadiusKm; }
public void setGpsRadiusKm(BigDecimal gpsRadiusKm) { this.gpsRadiusKm = gpsRadiusKm; }

public Boolean getRequiresBeforePhoto() {
    return requiresBeforePhoto;
}

public void setRequiresBeforePhoto(Boolean requiresBeforePhoto) {
    this.requiresBeforePhoto = requiresBeforePhoto;
}

public Boolean getRequiresAfterPhoto() {
    return requiresAfterPhoto;
}

public void setRequiresAfterPhoto(Boolean requiresAfterPhoto) {
    this.requiresAfterPhoto = requiresAfterPhoto;
}

public Boolean getRequiresSignature() {
    return requiresSignature;
}

public void setRequiresSignature(Boolean requiresSignature) {
    this.requiresSignature = requiresSignature;
}

public Boolean getRequiresDocumentUpload() {
    return requiresDocumentUpload;
}

public void setRequiresDocumentUpload(
        Boolean requiresDocumentUpload) {
    this.requiresDocumentUpload =
            requiresDocumentUpload;
}

    public Boolean getRemote() { return remote; }
    public void setRemote(Boolean remote) { this.remote = remote; }
    public Boolean getBillable() { return billable; }
    public void setBillable(Boolean billable) { this.billable = billable; }

}
