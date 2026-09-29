package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "pricing_profile_parameters",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_profile_parameter",
                        columnNames = {
                                "pricing_profile_id",
                                "parameter_code"
                        }
                )
        }
)
@Getter
@Setter
public class PricingProfileParameterEntity
        extends AuditableEntity {

    @Id
    @Column(
            name = "pricing_parameter_id",
            nullable = false
    )
    private UUID pricingParameterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "pricing_profile_id",
            nullable = false
    )
    private PricingProfileEntity pricingProfile;

    @Column(
            name = "parameter_code",
            nullable = false,
            length = 100
    )
    private String parameterCode;

    @Column(
            name = "parameter_name",
            nullable = false,
            length = 150
    )
    private String parameterName;

    @Column(
            name = "parameter_value",
            nullable = false,
            length = 500
    )
    private String parameterValue;

    @Column(
            name = "parameter_type",
            nullable = false,
            length = 30
    )
    private String parameterType;

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