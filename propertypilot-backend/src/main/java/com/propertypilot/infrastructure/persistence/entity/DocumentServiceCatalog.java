package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "document_service_catalog")
@Getter
@Setter
public class DocumentServiceCatalog {

    @Id
    @Column(name = "document_service_id")
    private UUID documentServiceId;

    @Column(name = "document_code")
    private String documentCode;

    @Column(name = "service_available")
    private Boolean serviceAvailable;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}