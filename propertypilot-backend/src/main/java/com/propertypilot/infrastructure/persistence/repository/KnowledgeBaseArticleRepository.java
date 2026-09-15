package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.KnowledgeBaseArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface KnowledgeBaseArticleRepository
        extends JpaRepository<KnowledgeBaseArticleEntity, UUID> {

    Optional<KnowledgeBaseArticleEntity> findByArticleSlug(String articleSlug);
}