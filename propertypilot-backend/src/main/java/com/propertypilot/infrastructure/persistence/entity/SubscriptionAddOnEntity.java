package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "subscription_add_ons")
public class SubscriptionAddOnEntity
        extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "subscription_add_on_id")
    private UUID subscriptionAddOnId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "service_id",
            nullable = false
    )
    private ServiceEntity service;

    @Column(
            name = "name",
            nullable = false
    )
    private String name;

    @Column(
            name = "price",
            nullable = false
    )
    private BigDecimal price;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            name = "eligibility_rule_json",
            columnDefinition = "jsonb",
            nullable = false
    )
    private String eligibilityRuleJson;

    @Column(
            name = "status",
            nullable = false
    )
    private String status;

    @Version
    @Column(name = "version")
    private Long version;

    public UUID getSubscriptionAddOnId() {
        return subscriptionAddOnId;
    }

    public void setSubscriptionAddOnId(
            UUID subscriptionAddOnId) {
        this.subscriptionAddOnId =
                subscriptionAddOnId;
    }

    public ServiceEntity getService() {
        return service;
    }

    public void setService(ServiceEntity service) {
        this.service = service;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getEligibilityRuleJson() {
        return eligibilityRuleJson;
    }

    public void setEligibilityRuleJson(
            String eligibilityRuleJson) {
        this.eligibilityRuleJson =
                eligibilityRuleJson;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}