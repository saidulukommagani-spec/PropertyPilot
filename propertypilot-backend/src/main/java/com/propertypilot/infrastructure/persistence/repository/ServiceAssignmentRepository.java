package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.ServiceAssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceAssignmentRepository
        extends JpaRepository<ServiceAssignmentEntity, UUID> {

    List<ServiceAssignmentEntity>
    findByServiceRequest_ServiceRequestIdOrderByAssignedAtDesc(
            UUID serviceRequestId);

    Optional<ServiceAssignmentEntity>
    findFirstByServiceRequest_ServiceRequestIdAndStatusOrderByAssignedAtDesc(
            UUID serviceRequestId,
            String status);

    List<ServiceAssignmentEntity>
    findByAgent_AgentIdAndStatus(
            UUID agentId,
            String status);
            Optional<ServiceAssignmentEntity>
findByServiceRequest_ServiceRequestIdAndStatus(
        UUID serviceRequestId,
        String status);
}