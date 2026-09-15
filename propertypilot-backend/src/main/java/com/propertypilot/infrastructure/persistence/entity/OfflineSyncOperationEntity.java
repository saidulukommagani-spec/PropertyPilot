package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "offline_sync_operations")
public class OfflineSyncOperationEntity extends AuditableEntity {

    @Id
    @Column(name = "offline_sync_operation_id")
    private UUID offlineSyncOperationId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "device_id", nullable = false, length = 255)
    private String deviceId;

    @Column(name = "client_operation_id", nullable = false, length = 160)
    private String clientOperationId;

    @Column(name = "entity_type", nullable = false, length = 80)
    private String entityType;

    @Column(name = "entity_id")
    private UUID entityId;

    @Column(name = "operation_type", nullable = false, length = 20)
    private String operationType;

    @Column(name = "payload", columnDefinition = "jsonb", nullable = false)
    private String payload;

    @Column(name = "client_occurred_at", nullable = false)
    private OffsetDateTime clientOccurredAt;

    @Column(name = "received_at", nullable = false)
    private OffsetDateTime receivedAt;

    @Column(name = "processed_at")
    private OffsetDateTime processedAt;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "conflict_details", columnDefinition = "jsonb")
    private String conflictDetails;

    @Column(name = "error_message")
    private String errorMessage;
}