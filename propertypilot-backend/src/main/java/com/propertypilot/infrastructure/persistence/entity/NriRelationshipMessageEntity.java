package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "nri_relationship_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NriRelationshipMessageEntity extends AuditableEntity {

    @Id
    @Column(name = "nri_relationship_message_id")
    private UUID nriRelationshipMessageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "nri_relationship_assignment_id",
            nullable = false)
    private NriRelationshipAssignmentEntity assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_user_id", nullable = false)
    private UserEntity senderUser;

    @Column(name = "message_body", nullable = false)
    private String messageBody;

    @Column(name = "channel", nullable = false, length = 20)
    private String channel;

    @Column(name = "sent_at", nullable = false)
    private OffsetDateTime sentAt;

    @Column(name = "read_at")
    private OffsetDateTime readAt;
}