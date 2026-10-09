package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.ServiceRequestStatusHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.UUID;
import java.time.Instant;

public interface ServiceRequestStatusHistoryRepository
        extends JpaRepository<
                ServiceRequestStatusHistoryEntity,
                UUID> {

    List<ServiceRequestStatusHistoryEntity>
    findByServiceRequest_ServiceRequestIdOrderByChangedAtDesc(
            UUID serviceRequestId);

    @EntityGraph(attributePaths = {"serviceRequest", "serviceRequest.property"})
    Page<ServiceRequestStatusHistoryEntity> findByChangedAtBetweenOrderByChangedAtDesc(
            Instant from, Instant to, Pageable pageable);
}
