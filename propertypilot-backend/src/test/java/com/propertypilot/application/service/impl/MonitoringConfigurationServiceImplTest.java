package com.propertypilot.application.service.impl;

import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.web.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MonitoringConfigurationServiceImplTest {
    private final MonitoringConfigurationServiceImpl service = new MonitoringConfigurationServiceImpl();

    @Test
    void supportsAdminConfiguredCustomFrequencies() {
        assertEquals("P15D", service.normalizeFrequency("p15d"));
        assertEquals("P3M", service.normalizeFrequency("P3M"));
        OffsetDateTime start = OffsetDateTime.parse("2026-01-31T10:00:00Z");
        assertEquals(OffsetDateTime.parse("2026-04-30T10:00:00Z"), service.nextDueAt(start, "P3M"));
    }

    @Test
    void rejectsNonPositiveOrMalformedFrequencies() {
        assertThrows(BusinessException.class, () -> service.normalizeFrequency("MONTHLY"));
        assertThrows(BusinessException.class, () -> service.normalizeFrequency("P0D"));
        assertThrows(BusinessException.class, () -> service.normalizeFrequency("-P1D"));
    }

    @Test
    void acceptsMonitoringCategoryAndRejectsOtherServices() {
        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setActiveFlag(true);
        serviceEntity.setCategory("MONITORING");
        assertDoesNotThrow(() -> service.validateMonitoringService(serviceEntity));
        serviceEntity.setCategory("INSPECTION");
        serviceEntity.setServiceCode("PROPERTY_CHECK");
        assertThrows(BusinessException.class, () -> service.validateMonitoringService(serviceEntity));
    }
}
