package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.ServiceSlaPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServiceSlaPolicyRepository
        extends JpaRepository<ServiceSlaPolicyEntity, UUID> {
    java.util.List<ServiceSlaPolicyEntity> findByStatusIgnoreCase(String status);
    java.util.List<ServiceSlaPolicyEntity> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    java.util.List<ServiceSlaPolicyEntity> findByService_ServiceIdAndStatusIgnoreCaseOrderByCreatedAtDesc(UUID serviceId, String status);
}
