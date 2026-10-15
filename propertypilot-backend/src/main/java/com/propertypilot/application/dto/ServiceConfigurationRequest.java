package com.propertypilot.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public class ServiceConfigurationRequest {
    @NotBlank @Pattern(regexp = "^[A-Z][A-Z0-9_]{1,79}$") private String serviceCode;
    @NotBlank private String serviceName;
    @NotBlank @Pattern(regexp = "^[A-Z][A-Z0-9_]{1,79}$") private String category;
    @NotBlank @Size(max = 100) private String catalogGroup;
    @NotBlank @Pattern(regexp = "^(CORE|OPTIONAL)$") private String catalogPriority;
    @Size(max = 255) private String deliveryPolicy;
    @Size(max = 120) private String frequency;
    @Size(max = 4000) private String catalogNotes;
    @NotNull private List<String> eligiblePropertyTypes;
    @NotNull private List<String> conditionalPropertyTypes;
    @NotNull @PositiveOrZero private BigDecimal basePrice;
    @NotNull private Boolean activeFlag;
    @NotNull private Boolean requiresVisit;
    @NotNull private Boolean visitMandatory;
    @NotNull @PositiveOrZero private Integer maxVisitsAllowed;
    @NotNull private Boolean requiresGps;
    @NotNull @DecimalMin(value = "0.001") private BigDecimal gpsRadiusKm;
    @NotNull private Boolean requiresBeforePhoto;
    @NotNull private Boolean requiresAfterPhoto;
    @NotNull private Boolean requiresSignature;
    @NotNull private Boolean requiresDocumentUpload;
    @NotNull private Boolean remote;
    @NotNull private Boolean billable;

    public String getServiceCode() { return serviceCode; }
    public void setServiceCode(String value) { serviceCode = value; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String value) { serviceName = value; }
    public String getCategory() { return category; }
    public void setCategory(String value) { category = value; }
    public String getCatalogGroup() { return catalogGroup; }
    public void setCatalogGroup(String value) { catalogGroup = value; }
    public String getCatalogPriority() { return catalogPriority; }
    public void setCatalogPriority(String value) { catalogPriority = value; }
    public String getDeliveryPolicy() { return deliveryPolicy; }
    public void setDeliveryPolicy(String value) { deliveryPolicy = value; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String value) { frequency = value; }
    public String getCatalogNotes() { return catalogNotes; }
    public void setCatalogNotes(String value) { catalogNotes = value; }
    public List<String> getEligiblePropertyTypes() { return eligiblePropertyTypes; }
    public void setEligiblePropertyTypes(List<String> value) { eligiblePropertyTypes = value; }
    public List<String> getConditionalPropertyTypes() { return conditionalPropertyTypes; }
    public void setConditionalPropertyTypes(List<String> value) { conditionalPropertyTypes = value; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal value) { basePrice = value; }
    public Boolean getActiveFlag() { return activeFlag; }
    public void setActiveFlag(Boolean value) { activeFlag = value; }
    public Boolean getRequiresVisit() { return requiresVisit; }
    public void setRequiresVisit(Boolean value) { requiresVisit = value; }
    public Boolean getVisitMandatory() { return visitMandatory; }
    public void setVisitMandatory(Boolean value) { visitMandatory = value; }
    public Integer getMaxVisitsAllowed() { return maxVisitsAllowed; }
    public void setMaxVisitsAllowed(Integer value) { maxVisitsAllowed = value; }
    public Boolean getRequiresGps() { return requiresGps; }
    public void setRequiresGps(Boolean value) { requiresGps = value; }
    public BigDecimal getGpsRadiusKm() { return gpsRadiusKm; }
    public void setGpsRadiusKm(BigDecimal value) { gpsRadiusKm = value; }
    public Boolean getRequiresBeforePhoto() { return requiresBeforePhoto; }
    public void setRequiresBeforePhoto(Boolean value) { requiresBeforePhoto = value; }
    public Boolean getRequiresAfterPhoto() { return requiresAfterPhoto; }
    public void setRequiresAfterPhoto(Boolean value) { requiresAfterPhoto = value; }
    public Boolean getRequiresSignature() { return requiresSignature; }
    public void setRequiresSignature(Boolean value) { requiresSignature = value; }
    public Boolean getRequiresDocumentUpload() { return requiresDocumentUpload; }
    public void setRequiresDocumentUpload(Boolean value) { requiresDocumentUpload = value; }
    public Boolean getRemote() { return remote; }
    public void setRemote(Boolean value) { remote = value; }
    public Boolean getBillable() { return billable; }
    public void setBillable(Boolean value) { billable = value; }
}
