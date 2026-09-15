package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "monitoring_schedules")
public class MonitoringScheduleEntity
        extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "monitoring_schedule_id")
    private UUID monitoringScheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "customer_subscription_id",
            nullable = false
    )
    private CustomerSubscriptionEntity customerSubscription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "property_id",
            nullable = false
    )
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "service_id",
            nullable = false
    )
    private ServiceEntity service;

    @Column(
            name = "frequency",
            nullable = false
    )
    private String frequency;

    @Column(
            name = "next_due_at",
            nullable = false
    )
    private OffsetDateTime nextDueAt;

    @Column(
            name = "status",
            nullable = false
    )
    private String status;

    @Column(name = "last_generated_at")
    private OffsetDateTime lastGeneratedAt;

    @Version
    @Column(name = "version")
    private Long version;

    public UUID getMonitoringScheduleId() {
        return monitoringScheduleId;
    }

    public void setMonitoringScheduleId(
            UUID monitoringScheduleId) {
        this.monitoringScheduleId =
                monitoringScheduleId;
    }

    public CustomerSubscriptionEntity
    getCustomerSubscription() {
        return customerSubscription;
    }

    public void setCustomerSubscription(
            CustomerSubscriptionEntity
                    customerSubscription) {
        this.customerSubscription =
                customerSubscription;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(
            Property property) {
        this.property = property;
    }

    public ServiceEntity getService() {
        return service;
    }

    public void setService(
            ServiceEntity service) {
        this.service = service;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(
            String frequency) {
        this.frequency = frequency;
    }

    public OffsetDateTime getNextDueAt() {
        return nextDueAt;
    }

    public void setNextDueAt(
            OffsetDateTime nextDueAt) {
        this.nextDueAt = nextDueAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {
        this.status = status;
    }

    public OffsetDateTime getLastGeneratedAt() {
        return lastGeneratedAt;
    }

    public void setLastGeneratedAt(
            OffsetDateTime lastGeneratedAt) {
        this.lastGeneratedAt =
                lastGeneratedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(
            Long version) {
        this.version = version;
    }
}