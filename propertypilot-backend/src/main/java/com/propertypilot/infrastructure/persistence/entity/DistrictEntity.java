package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "districts")
public class DistrictEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "district_id")
    private UUID districtId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "state_id",
            nullable = false)
    private StateEntity state;

    @Column(
            name = "district_name",
            nullable = false,
            length = 120)
    private String districtName;

    @Version
    @Column(name = "version")
    private Long version;

    // Generate getters/setters
}