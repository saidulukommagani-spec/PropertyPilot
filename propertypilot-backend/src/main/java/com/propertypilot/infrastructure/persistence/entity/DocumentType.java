package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "document_types")
@Getter
@Setter
public class DocumentType {

    @Id
    @Column(name = "document_type_id")
    private UUID documentTypeId;

    @Column(name = "property_category",
            nullable = false,
            length = 50)
    private String propertyCategory;

    @Column(name = "document_code",
            nullable = false,
            unique = true,
            length = 100)
    private String documentCode;

    @Column(name = "document_name",
            nullable = false,
            length = 255)
    private String documentName;

    @Column(name = "mandatory",
            nullable = false)
    private Boolean mandatory;

    @Column(name = "active",
            nullable = false)
    private Boolean active;

    @Column(name = "created_at",
            nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at",
            nullable = false)
    private OffsetDateTime updatedAt;
}