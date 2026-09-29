package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "evidence_types")
public class EvidenceTypeEntity
        extends AuditableEntity {

    @Id
    @Column(name = "evidence_type_id")
    private UUID evidenceTypeId;

    @Column(
            name = "code",
            nullable = false
    )
    private String code;

    @Column(
            name = "name",
            nullable = false
    )
    private String name;

    @Column(
            name = "category",
            nullable = false
    )
    private String category;

    @Column(name = "description")
    private String description;

    @Column(
            name = "active_flag",
            nullable = false
    )
    private Boolean activeFlag;

    @Version
    @Column(
            name = "version",
            nullable = false
    )
    private Long version = 0L;

    public UUID getEvidenceTypeId() {
        return evidenceTypeId;
    }

    public void setEvidenceTypeId(
            UUID evidenceTypeId) {
        this.evidenceTypeId = evidenceTypeId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(
            String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(
            String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(
            String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {
        this.description = description;
    }

    public Boolean getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(
            Boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(
            Long version) {
        this.version = version;
    }
}