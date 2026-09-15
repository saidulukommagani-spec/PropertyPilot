package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "notification_templates",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_notification_template",
                        columnNames = {
                                "template_code",
                                "channel",
                                "locale"
                        }
                )
        }
)
@Getter
@Setter
public class NotificationTemplateEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "notification_template_id")
    private UUID notificationTemplateId;

    @Column(name = "template_code", nullable = false, length = 80)
    private String templateCode;

    @Column(name = "channel", nullable = false, length = 20)
    private String channel;

    @Column(name = "locale", nullable = false, length = 10)
    private String locale;

    @Column(name = "subject_template")
    private String subjectTemplate;

    @Column(name = "body_template", nullable = false)
    private String bodyTemplate;

    @Column(name = "status", nullable = false, length = 20)
    private String status;
}