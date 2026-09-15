package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "localities")
@Getter
@Setter
public class LocalityEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "locality_id")
    private UUID localityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "district_id",
            nullable = false)
    private DistrictEntity district;

    @Column(
            name = "locality_name",
            nullable = false,
            length = 120)
    private String localityName;

    @Column(
            name = "pincode",
            length = 20)
    private String pincode;

    @Version
    @Column(name = "version")
    private Long version;
}