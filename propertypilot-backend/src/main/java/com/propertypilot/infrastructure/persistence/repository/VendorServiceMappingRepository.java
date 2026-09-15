package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.VendorServiceMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VendorServiceMappingRepository
        extends JpaRepository<VendorServiceMappingEntity, UUID> {

    List<VendorServiceMappingEntity> findByVendorVendorId(UUID vendorId);

    List<VendorServiceMappingEntity> findByServiceServiceId(UUID serviceId);

    List<VendorServiceMappingEntity> findByStatus(String status);

    List<VendorServiceMappingEntity> findByCapacityStatus(String capacityStatus);
}