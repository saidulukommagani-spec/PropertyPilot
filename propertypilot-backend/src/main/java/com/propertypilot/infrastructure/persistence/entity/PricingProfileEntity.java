package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "pricing_profiles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_pricing_profile_code",
                        columnNames = "profile_code"
                )
        }
)
@Getter
@Setter
public class PricingProfileEntity
        extends AuditableEntity {

    @Id
    @Column(
            name = "pricing_profile_id",
            nullable = false
    )
    private UUID pricingProfileId;

    @Column(
            name = "profile_code",
            nullable = false,
            length = 50
    )
    private String profileCode;

    @Column(
            name = "profile_name",
            nullable = false,
            length = 150
    )
    private String profileName;

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
    private Long version;
}