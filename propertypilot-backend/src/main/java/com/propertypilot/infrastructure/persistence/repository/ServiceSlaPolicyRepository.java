package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.ServiceSlaPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServiceSlaPolicyRepository
        extends JpaRepository<ServiceSlaPolicyEntity, UUID> {
}