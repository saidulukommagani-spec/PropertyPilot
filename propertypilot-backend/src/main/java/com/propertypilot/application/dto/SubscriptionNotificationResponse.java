package com.propertypilot.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class SubscriptionNotificationResponse {

    private UUID notificationId;

    private UUID customerSubscriptionId;

    private String notificationType;

    private String message;

    private String status;

    private LocalDateTime sentDate;

    public UUID getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(
            UUID notificationId) {
        this.notificationId = notificationId;
    }

    public UUID getCustomerSubscriptionId() {
        return customerSubscriptionId;
    }

    public void setCustomerSubscriptionId(
            UUID customerSubscriptionId) {
        this.customerSubscriptionId =
                customerSubscriptionId;
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
}