package com.propertypilot.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CreateCustomerVerificationRequest {

    private UUID documentId;

    private String verificationType;

    private String referenceNumberHash;

    private String providerReference;

    private OffsetDateTime expiresAt;

    public UUID getDocumentId() {
        return documentId;
    }

    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
    }

    public String getVerificationType() {
        return verificationType;
    }

    public void setVerificationType(String verificationType) {
        this.verificationType = verificationType;
    }

    public String getReferenceNumberHash() {
        return referenceNumberHash;
    }

    public void setReferenceNumberHash(String referenceNumberHash) {
        this.referenceNumberHash = referenceNumberHash;
    }

    public String getProviderReference() {
        return providerReference;
    }

    public void setProviderReference(String providerReference) {
        this.providerReference = providerReference;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(OffsetDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}