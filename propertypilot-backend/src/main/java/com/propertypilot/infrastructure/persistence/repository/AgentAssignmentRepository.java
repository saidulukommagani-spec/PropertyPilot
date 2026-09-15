package com.propertypilot.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.propertypilot.infrastructure.persistence.entity.AgentAssignmentEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgentAssignmentRepository
        extends JpaRepository<AgentAssignmentEntity, UUID> {

    Optional<AgentAssignmentEntity>
    findByAssignmentId(UUID assignmentId);

    List<AgentAssignmentEntity>
    findByAgent_AgentId(UUID agentId);

    List<AgentAssignmentEntity>
    findByServiceRequest_ServiceRequestId(
            UUID serviceRequestId);

    Optional<AgentAssignmentEntity>
    findByServiceRequest_ServiceRequestIdAndAssignmentStatusIn(
            UUID serviceRequestId,
            List<String> statuses);

    List<AgentAssignmentEntity>
    findByAssignmentStatus(String assignmentStatus);

    
    
}