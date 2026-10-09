package com.propertypilot.application.dto;

public record MonitoringDashboardResponse(
        long activeSchedules,
        long dueSchedules,
        long unresolvedAlerts,
        long criticalUnresolvedAlerts) {
}
