package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "agent_skills",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_agent_skills",
                        columnNames = {"agent_id", "skill_name"}
                )
        }
)
@Getter
@Setter
public class AgentSkillEntity extends AuditableEntity {

    @Id
    @Column(name = "skill_id", nullable = false)
    private java.util.UUID skillId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private AgentEntity agent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private ServiceEntity service;

    @Column(name = "skill_name", nullable = false, length = 100)
    private String skillName;

    @Column(name = "skill_level", nullable = false)
    private Integer skillLevel;

    @Column(name = "status", nullable = false, length = 30)
    private String status;
}