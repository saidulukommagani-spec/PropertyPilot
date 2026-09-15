package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cluster_rules")
@Getter
@Setter
public class ClusterRuleEntity {

    @Id
    @Column(name = "cluster_rule_id", nullable = false)
    private java.util.UUID clusterRuleId;

    @Column(name = "service_type", nullable = false, length = 100)
    private String serviceType;

    @Column(name = "radius_km", nullable = false)
    private java.math.BigDecimal radiusKm;

    @Column(name = "minimum_requests", nullable = false)
    private Integer minimumRequests;

    @Column(name = "discount_percentage")
    private java.math.BigDecimal discountPercentage;

    @Column(name = "status", nullable = false, length = 20)
    private String status;
}