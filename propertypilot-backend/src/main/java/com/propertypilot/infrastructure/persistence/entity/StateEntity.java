package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "states")
public class StateEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "state_id")
    private UUID stateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "country_id",
            nullable = false)
    private CountryEntity country;

    @Column(
            name = "state_name",
            nullable = false,
            length = 120)
    private String stateName;

    @Column(
            name = "state_code",
            length = 20)
    private String stateCode;

    @Version
    @Column(name = "version")
    private Long version;

    // Generate getters/setters
}