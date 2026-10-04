package com.propertypilot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(
        description =
                "Subscription Notification Response"
)
public class SubscriptionNotificationResponse {

    @Schema(
            description = "Notification Id"
    )
    private UUID notificationId;

    @Schema(
            description = "Customer Subscription Id"
    )
    private UUID customerSubscriptionId;

    @Schema(
            description = "Notification Type"
    )
    private String notificationType;

    @Schema(
            description = "Notification Message"
    )
    private String message;

    @Schema(
            description = "Notification Status"
    )
    private String status;

    @Schema(
            description = "Notification Sent Date"
    )
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