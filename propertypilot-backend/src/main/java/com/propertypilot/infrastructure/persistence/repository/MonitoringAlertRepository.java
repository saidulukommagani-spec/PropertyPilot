package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.MonitoringAlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.Collection;

public interface MonitoringAlertRepository
        extends JpaRepository<MonitoringAlertEntity, UUID> {
    long countByStatusIn(Collection<String> statuses);
    long countBySeverityIgnoreCaseAndStatusIn(String severity, Collection<String> statuses);
}
