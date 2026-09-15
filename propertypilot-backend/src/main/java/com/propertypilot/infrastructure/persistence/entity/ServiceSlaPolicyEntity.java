package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "service_sla_policies",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_service_sla_policy",
                        columnNames = {"service_id", "policy_name"}
                )
        }
)
@Getter
@Setter
public class ServiceSlaPolicyEntity extends AuditableEntity {

    @Id
    @Column(name = "sla_policy_id", nullable = false)
    private UUID slaPolicyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;

    @Column(name = "policy_name", nullable = false, length = 150)
    private String policyName;

    @Column(name = "acknowledgement_minutes", nullable = false)
    private Integer acknowledgementMinutes;

    @Column(name = "completion_minutes", nullable = false)
    private Integer completionMinutes;

    @Column(
            name = "escalation_policy",
            nullable = false,
            columnDefinition = "jsonb"
    )
    private String escalationPolicy;

    @Column(name = "status", nullable = false, length = 20)
    private String status;
}