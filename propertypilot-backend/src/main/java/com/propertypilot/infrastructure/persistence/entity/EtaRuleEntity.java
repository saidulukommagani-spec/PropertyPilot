package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "eta_rules")
@Getter
@Setter
public class EtaRuleEntity {

    @Id
    @Column(name = "eta_rule_id", nullable = false)
    private java.util.UUID etaRuleId;

    @Column(name = "service_type", nullable = false, length = 100)
    private String serviceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private LocationMasterEntity location;

    @Column(name = "target_hours", nullable = false)
    private Integer targetHours;

    @Column(name = "warning_hours", nullable = false)
    private Integer warningHours;

    @Column(name = "critical_hours", nullable = false)
    private Integer criticalHours;

    @Column(name = "status", nullable = false, length = 20)
    private String status;
}