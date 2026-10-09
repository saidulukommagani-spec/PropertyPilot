package com.propertypilot.application.service.impl;

import com.propertypilot.application.service.MonitoringConfigurationService;
import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.web.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.Period;
import java.time.format.DateTimeParseException;

@Service
public class MonitoringConfigurationServiceImpl implements MonitoringConfigurationService {

    @Override
    public String normalizeFrequency(String frequency) {
        if (frequency == null || frequency.isBlank()) {
            throw new BusinessException("Monitoring frequency is required");
        }
        try {
            Period period = Period.parse(frequency.trim().toUpperCase());
            if (period.isZero() || period.isNegative()) {
                throw new BusinessException("Monitoring frequency must be a positive ISO-8601 period, such as P15D or P3M");
            }
            String normalized = period.toString();
            if (normalized.length() > 30) {
                throw new BusinessException("Monitoring frequency cannot exceed 30 characters");
            }
            return normalized;
        } catch (DateTimeParseException ex) {
            throw new BusinessException("Monitoring frequency must be an ISO-8601 period, such as P15D or P3M");
        }
    }

    @Override
    public OffsetDateTime nextDueAt(OffsetDateTime from, String frequency) {
        return from.plus(Period.parse(normalizeFrequency(frequency)));
    }

    @Override
    public void validateMonitoringService(ServiceEntity service) {
        if (service == null || !Boolean.TRUE.equals(service.getActiveFlag())) {
            throw new BusinessException("An active Monitoring service is required");
        }
        String category = service.getCategory();
        String code = service.getServiceCode();
        if ((category == null || !"MONITORING".equalsIgnoreCase(category.trim()))
                && (code == null || !code.toUpperCase().startsWith("MONITOR"))) {
            throw new BusinessException("The selected service is not configured as a Monitoring service");
        }
    }
}
