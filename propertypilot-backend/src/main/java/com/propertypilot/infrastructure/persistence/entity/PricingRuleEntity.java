package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
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
                        })
        })
public class PricingRuleEntity
        extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "price_rule_id",
            nullable = false,
            updatable = false)
    private UUID priceRuleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "service_id",
            nullable = false)
    private ServiceEntity service;

    @Column(
            name = "rule_name",
            nullable = false,
            length = 150)
    private String ruleName;

    @Column(
            name = "base_amount",
            nullable = false,
            precision = 15,
            scale = 2)
    private BigDecimal baseAmount;

    @Column(
            name = "currency_code",
            nullable = false,
            length = 3)
    private String currencyCode;

    /**
     * Stored as JSON string.
     * Avoids vendor lock-in for now.
     */
   @JdbcTypeCode(SqlTypes.JSON)
@Column(
        name = "rule_definition",
        nullable = false,
        columnDefinition = "jsonb")
private String ruleDefinition;

    @Column(
            name = "effective_from",
            nullable = false)
    private OffsetDateTime effectiveFrom;

    @Column(name = "effective_to")
    private OffsetDateTime effectiveTo;

    @Column(
            name = "status",
            nullable = false,
            length = 20)
    private String status;

    @Version
    @Column(
            name = "version",
            nullable = false)
    private Long version = 0L;

    public UUID getPriceRuleId() {
        return priceRuleId;
    }

    public void setPriceRuleId(UUID priceRuleId) {
        this.priceRuleId = priceRuleId;
    }

    public ServiceEntity getService() {
        return service;
    }

    public void setService(ServiceEntity service) {
        this.service = service;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public BigDecimal getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(BigDecimal baseAmount) {
        this.baseAmount = baseAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getRuleDefinition() {
        return ruleDefinition;
    }

    public void setRuleDefinition(String ruleDefinition) {
        this.ruleDefinition = ruleDefinition;
    }

    public OffsetDateTime getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(
            OffsetDateTime effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public OffsetDateTime getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(
            OffsetDateTime effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}