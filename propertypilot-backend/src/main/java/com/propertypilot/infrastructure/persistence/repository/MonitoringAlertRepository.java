package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.MonitoringAlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MonitoringAlertRepository
        extends JpaRepository<MonitoringAlertEntity, UUID> {
}