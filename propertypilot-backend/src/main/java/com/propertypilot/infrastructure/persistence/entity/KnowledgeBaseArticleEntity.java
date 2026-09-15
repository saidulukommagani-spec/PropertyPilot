package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "knowledge_base_articles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "knowledge_base_articles_article_slug_key",
                        columnNames = "article_slug"
                )
        }
)
@Getter
@Setter
public class KnowledgeBaseArticleEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "knowledge_base_article_id")
    private UUID knowledgeBaseArticleId;

    @Column(name = "article_slug", nullable = false, length = 160)
    private String articleSlug;

    @Column(name = "title", nullable = false, length = 250)
    private String title;

    @Column(name = "summary")
    private String summary;

    @Column(name = "body", nullable = false, columnDefinition = "text")
    private String body;

    @Column(name = "category", nullable = false, length = 80)
    private String category;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_user_id", nullable = false)
    private UserEntity authorUser;
}