package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.VendorAssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VendorAssignmentRepository
        extends JpaRepository<VendorAssignmentEntity, UUID> {

    List<VendorAssignmentEntity> findByVendorVendorId(UUID vendorId);

    List<VendorAssignmentEntity> findByServiceRequestServiceRequestId(UUID serviceRequestId);

    List<VendorAssignmentEntity> findByStatus(String status);

    List<VendorAssignmentEntity> findByVendorVendorIdAndStatus(
            UUID vendorId,
            String status
    );
}