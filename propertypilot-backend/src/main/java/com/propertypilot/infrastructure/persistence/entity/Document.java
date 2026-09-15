package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "documents")
@Getter
@Setter
public class Document extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "document_id")
    private UUID documentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "property_id",
            nullable = false)
    private Property property;

    @Column(name = "document_name",
            nullable = false)
    private String documentName;

    @Column(name = "document_type",
            nullable = false)
    private String documentType;

    @Column(name = "status",
            nullable = false)
    private String status;
}