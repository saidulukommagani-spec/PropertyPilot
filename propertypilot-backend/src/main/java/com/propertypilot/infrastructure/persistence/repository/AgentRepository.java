package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.AgentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface AgentRepository
        extends JpaRepository<AgentEntity, UUID> {

    @EntityGraph(attributePaths = "user")
    Optional<AgentEntity> findByUser_UserId(UUID userId);

    @EntityGraph(attributePaths = "user")
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

    @EntityGraph(attributePaths = "user")
    List<AgentEntity> findAllByStatusOrderByUser_FullNameAsc(String status);
long count();

long countByStatus(
        String status);

}
