package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.AgentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AgentRepository
        extends JpaRepository<AgentEntity, UUID> {

    Optional<AgentEntity> findByUser_UserId(UUID userId);

    Optional<AgentEntity> findByUser_Email(String email);

    Optional<AgentEntity> findByAgentCode(String agentCode);

    Optional<AgentEntity> findByAgentIdAndStatus(
            UUID agentId,
            String status
    );

    Optional<AgentEntity> findByUser_UserIdAndStatus(
            UUID userId,
            String status
    );
long count();

long countByStatus(
        String status);

}