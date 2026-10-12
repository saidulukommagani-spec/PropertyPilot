package com.propertypilot.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Future;
import java.time.Instant;
import java.util.UUID;

public class CreateServiceRequestRequest {

    @NotNull
    private UUID quoteId;

    // Optional consistency fields for existing clients; the accepted quote remains authoritative.
    private UUID propertyId;
    private UUID serviceId;
    private String serviceType;

    private UUID leadId;

    @Pattern(regexp = "LOW|MEDIUM|HIGH|URGENT")
    private String priority;

    @Size(max = 4000)
    private String description;

    @Future
    private Instant preferredDate;

    public UUID getQuoteId() { return quoteId; }
    public void setQuoteId(UUID quoteId) { this.quoteId = quoteId; }

    public UUID getPropertyId() { return propertyId; }
    public void setPropertyId(UUID propertyId) { this.propertyId = propertyId; }
    public UUID getServiceId() { return serviceId; }
    public void setServiceId(UUID serviceId) { this.serviceId = serviceId; }
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public UUID getLeadId() { return leadId; }
    public void setLeadId(UUID leadId) { this.leadId = leadId; }

    public String getPriority() {
        return priority;
    }

    public void setPriority(
            String priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {
        this.description = description;
    }

    public Instant getPreferredDate() { return preferredDate; }
    public void setPreferredDate(Instant preferredDate) { this.preferredDate = preferredDate; }
}
