package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.ServiceRequestStatusHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServiceRequestStatusHistoryRepository
        extends JpaRepository<
                ServiceRequestStatusHistoryEntity,
                UUID> {

    List<ServiceRequestStatusHistoryEntity>
    findByServiceRequest_ServiceRequestIdOrderByChangedAtDesc(
            UUID serviceRequestId);
}