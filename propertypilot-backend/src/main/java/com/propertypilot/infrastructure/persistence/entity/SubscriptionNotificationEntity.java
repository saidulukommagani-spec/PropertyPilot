package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscription_notifications")
public class SubscriptionNotificationEntity
        extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "notification_id")
    private UUID notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "customer_subscription_id",
            nullable = false)
    private CustomerSubscriptionEntity
            customerSubscription;

    @Column(
            name = "notification_type",
            nullable = false,
            length = 50)
    private String notificationType;

    @Column(
            name = "message",
            length = 2000)
    private String message;

    @Column(
            name = "status",
            nullable = false,
            length = 30)
    private String status;

    @Column(name = "sent_date")
    private LocalDateTime sentDate;

    @Version
    @Column(name = "version")
    private Long version;

    public UUID getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(
            UUID notificationId) {
        this.notificationId = notificationId;
    }

    public CustomerSubscriptionEntity
    getCustomerSubscription() {
        return customerSubscription;
    }

    public void setCustomerSubscription(
            CustomerSubscriptionEntity customerSubscription) {
        this.customerSubscription =
                customerSubscription;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(
            String notificationType) {
        this.notificationType =
                notificationType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(
            String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {
        this.status = status;
    }

    public LocalDateTime getSentDate() {
        return sentDate;
    }

    public void setSentDate(
            LocalDateTime sentDate) {
        this.sentDate = sentDate;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(
            Long version) {
        this.version = version;
    }
}