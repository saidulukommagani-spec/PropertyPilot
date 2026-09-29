package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "pricing_parameter_history")
public class PricingParameterHistoryEntity
        extends AuditableEntity {

    @Id
@GeneratedValue(strategy = GenerationType.UUID)
@Column(
        name = "pricing_parameter_history_id",
        nullable = false,
        updatable = false)
private UUID pricingParameterHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "pricing_parameter_id",
            nullable = false)
    private PricingProfileParameterEntity pricingParameter;

    @Column(
            name = "old_value",
            nullable = false,
            length = 255)
    private String oldValue;

    @Column(
            name = "new_value",
            nullable = false,
            length = 255)
    private String newValue;

    @Column(name = "changed_by")
    private UUID changedBy;

    @Column(
            name = "changed_at",
            nullable = false)
    private OffsetDateTime changedAt;

    @Column(
            name = "remarks",
            length = 500)
    private String remarks;

    @Version
    @Column(
            name = "version",
            nullable = false)
    private Long version = 0L;

 public UUID getPricingParameterHistoryId() {
    return pricingParameterHistoryId;
}

public void setPricingParameterHistoryId(
        UUID pricingParameterHistoryId) {
    this.pricingParameterHistoryId =
            pricingParameterHistoryId;
}

    public PricingProfileParameterEntity getPricingParameter() {
        return pricingParameter;
    }

    public void setPricingParameter(
            PricingProfileParameterEntity pricingParameter) {
        this.pricingParameter = pricingParameter;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public UUID getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(UUID changedBy) {
        this.changedBy = changedBy;
    }

    public OffsetDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(
            OffsetDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
@Column(
        name = "parameter_code",
        nullable = false,
        length = 100)
private String parameterCode;

public String getParameterCode() {
    return parameterCode;
}

public void setParameterCode(
        String parameterCode) {
    this.parameterCode = parameterCode;
}

}