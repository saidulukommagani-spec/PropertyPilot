package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
        name = "service_eligibility_rules",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_service_eligibility_rule",
                        columnNames = {"service_id", "rule_name"}
                )
        }
)
@Getter
@Setter
public class ServiceEligibilityRuleEntity extends AuditableEntity {

    @Id
    @Column(name = "eligibility_rule_id", nullable = false)
    private java.util.UUID eligibilityRuleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;

    @Column(name = "rule_name", nullable = false, length = 150)
    private String ruleName;

 @Column(
        name = "rule_definition",
        nullable = false,
        columnDefinition = "jsonb"
)
@JdbcTypeCode(SqlTypes.JSON)
private String ruleDefinition;

    @Column(name = "priority", nullable = false)
    private Integer priority;

    @Column(name = "status", nullable = false, length = 20)
    private String status;
}