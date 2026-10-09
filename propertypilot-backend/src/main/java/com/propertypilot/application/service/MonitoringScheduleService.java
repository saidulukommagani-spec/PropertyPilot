package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreateMonitoringScheduleRequest;
import com.propertypilot.application.dto.MonitoringScheduleResponse;
import com.propertypilot.application.dto.MonitoringRunResponse;

import java.util.List;
import java.util.UUID;

public interface MonitoringScheduleService {
    MonitoringScheduleResponse createSchedule(CreateMonitoringScheduleRequest request);
    MonitoringScheduleResponse getSchedule(UUID scheduleId);
    List<MonitoringScheduleResponse> getMySchedules();
    List<MonitoringRunResponse> getRuns(UUID scheduleId);
}
