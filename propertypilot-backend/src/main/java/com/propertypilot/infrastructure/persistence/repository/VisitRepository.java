package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.VisitEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VisitRepository
        extends JpaRepository<VisitEntity, UUID> {

    @EntityGraph(attributePaths = {
            "serviceRequest",
            "serviceRequest.property"
    })
    Optional<VisitEntity> findById(
            UUID visitId);

    List<VisitEntity> findByServiceRequest_ServiceRequestId(
            UUID serviceRequestId);

    List<VisitEntity> findByAgent_AgentId(
            UUID agentId);

    List<VisitEntity> findByServiceRequest_Customer_CustomerIdOrderByScheduledAtDesc(UUID customerId);

    List<VisitEntity> findByStatus(
            String status);

    List<VisitEntity> findByAssignment_AssignmentId(
            UUID assignmentId);

    boolean existsByAssignment_AssignmentId(
            UUID assignmentId);

    int countByServiceRequest_ServiceRequestIdAndStatusNot(UUID serviceRequestId, String status);

    boolean existsByVisitId(
            UUID visitId);

    long countByServiceRequest_ServiceRequestId(UUID serviceRequestId);
}
