package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.AgentSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface AgentSkillRepository
        extends JpaRepository<AgentSkillEntity, UUID> {
    List<AgentSkillEntity> findByAgent_AgentIdAndStatus(UUID agentId, String status);
}
