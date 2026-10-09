package com.propertypilot.application.service;

import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;

import java.time.OffsetDateTime;

public interface MonitoringConfigurationService {
    String normalizeFrequency(String frequency);
    OffsetDateTime nextDueAt(OffsetDateTime from, String frequency);
    void validateMonitoringService(ServiceEntity service);
}
