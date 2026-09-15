package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.DistrictEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DistrictRepository
        extends JpaRepository<DistrictEntity, UUID> {

    List<DistrictEntity> findByStateStateId(
            UUID stateId);

    boolean existsByStateStateIdAndDistrictName(
            UUID stateId,
            String districtName);
}