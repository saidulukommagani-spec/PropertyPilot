package com.propertypilot.application.dto;

import java.util.UUID;

public class CustomerPreferenceResponse {

    private UUID customerPreferenceId;

    private UUID customerId;

    private String preferredLanguage;

    private String preferredTimezone;

    private String communicationChannels;

    private String preferenceData;

    private Boolean marketingConsent;

    public UUID getCustomerPreferenceId() {
        return customerPreferenceId;
    }

    public void setCustomerPreferenceId(UUID customerPreferenceId) {
        this.customerPreferenceId = customerPreferenceId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public String getPreferredTimezone() {
        return preferredTimezone;
    }

    public void setPreferredTimezone(String preferredTimezone) {
        this.preferredTimezone = preferredTimezone;
    }

    public String getCommunicationChannels() {
        return communicationChannels;
    }

    public void setCommunicationChannels(String communicationChannels) {
        this.communicationChannels = communicationChannels;
    }

    public String getPreferenceData() {
        return preferenceData;
    }

    public void setPreferenceData(String preferenceData) {
        this.preferenceData = preferenceData;
    }

    public Boolean getMarketingConsent() {
        return marketingConsent;
    }

    public void setMarketingConsent(Boolean marketingConsent) {
        this.marketingConsent = marketingConsent;
    }
}