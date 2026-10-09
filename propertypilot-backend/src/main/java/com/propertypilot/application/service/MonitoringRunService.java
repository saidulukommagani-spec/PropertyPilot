package com.propertypilot.application.service;

import com.propertypilot.application.dto.MonitoringRunResponse;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface MonitoringRunService {
    MonitoringRunResponse runScheduleIfDue(UUID scheduleId, OffsetDateTime now);
    int processDueSchedules(OffsetDateTime now);
    MonitoringRunResponse createInitialRunFromPurchasedRequest(UUID scheduleId, OffsetDateTime now);
}
