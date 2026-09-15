package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "complaint_comments")
@Getter
@Setter
public class ComplaintCommentEntity extends AuditableEntity {

    @Id
    @Column(name = "complaint_comment_id", nullable = false)
    private UUID complaintCommentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id", nullable = false)
    private ComplaintEntity complaint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_user_id", nullable = false)
    private UserEntity authorUser;

    @Column(
            name = "comment_text",
            nullable = false,
            columnDefinition = "text"
    )
    private String commentText;

    @Column(name = "visibility", nullable = false, length = 20)
    private String visibility;

    @Column(
            name = "attachment_metadata",
            nullable = false,
            columnDefinition = "jsonb"
    )
    private String attachmentMetadata;
}