package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "reference_data",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_reference_data_category_code",
                        columnNames = {
                                "category",
                                "code"
                        }
                )
        }
)
public class ReferenceDataEntity extends AuditableEntity {

    @Id
    @Column(name = "reference_data_id")
    private UUID referenceDataId;

    @Column(name = "category", nullable = false, length = 80)
    private String category;

    @Column(name = "code", nullable = false, length = 80)
    private String code;

    @Column(name = "display_name", nullable = false, length = 150)
    private String displayName;

    @Column(name = "description")
    private String description;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "effective_from", nullable = false)
    private OffsetDateTime effectiveFrom;

    @Column(name = "effective_to")
    private OffsetDateTime effectiveTo;
}