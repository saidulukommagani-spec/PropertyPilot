package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.ComplaintCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ComplaintCommentRepository
        extends JpaRepository<ComplaintCommentEntity, UUID> {
}