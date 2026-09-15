package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "service_price_rules",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_service_price_rule",
                        columnNames = {
                                "service_id",
                                "rule_name",
                                "effective_from"
                        }
                )
        }
)
@Getter
@Setter
public class ServicePriceRuleEntity extends AuditableEntity {

    @Id
    @Column(name = "price_rule_id", nullable = false)
    private UUID priceRuleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;

    @Column(name = "rule_name", nullable = false, length = 150)
    private String ruleName;

    @Column(name = "base_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal baseAmount;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(
            name = "rule_definition",
            nullable = false,
            columnDefinition = "jsonb"
    )
    private String ruleDefinition;

    @Column(name = "effective_from", nullable = false)
    private OffsetDateTime effectiveFrom;

    @Column(name = "effective_to")
    private OffsetDateTime effectiveTo;

    @Column(name = "status", nullable = false, length = 20)
    private String status;
}