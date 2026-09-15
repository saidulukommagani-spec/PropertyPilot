package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "document_versions")
@Getter
@Setter
public class DocumentVersion extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "version_id")
    private UUID versionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "document_id",
            nullable = false)
    private Document document;

    @Column(name = "version_number",
            nullable = false)
    private Integer versionNumber;

    @Column(name = "file_name",
            nullable = false)
    private String fileName;

    @Column(name = "file_path",
            nullable = false)
    private String filePath;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "current_version",
            nullable = false)
    private Boolean currentVersion;
}