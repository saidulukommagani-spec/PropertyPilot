package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "service_request_status_history")
@Getter
@Setter
public class ServiceRequestStatusHistoryEntity
        extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "status_history_id")
    private UUID statusHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "service_request_id",
            nullable = false)
    private ServiceRequestEntity serviceRequest;

    @Column(name = "previous_status")
    private String previousStatus;

    @Column(
            name = "new_status",
            nullable = false)
    private String newStatus;

    @Column(name = "change_reason")
    private String changeReason;

    @Column(name = "changed_at")
    private Instant changedAt;
}