package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateMonitoringScheduleRequest;
import com.propertypilot.application.dto.MonitoringRunResponse;
import com.propertypilot.application.dto.MonitoringScheduleResponse;
import com.propertypilot.application.service.MonitoringRunService;
import com.propertypilot.application.service.MonitoringScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/monitoring/schedules")
public class MonitoringScheduleController {
    private final MonitoringScheduleService scheduleService;
    private final MonitoringRunService runService;

    public MonitoringScheduleController(MonitoringScheduleService scheduleService, MonitoringRunService runService) {
        this.scheduleService = scheduleService;
        this.runService = runService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','OPERATIONS')")
    public ResponseEntity<MonitoringScheduleResponse> create(@Valid @RequestBody CreateMonitoringScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.createSchedule(request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<MonitoringScheduleResponse> getMine() {
        return scheduleService.getMySchedules();
    }

    @GetMapping("/{scheduleId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN','OPERATIONS')")
    public MonitoringScheduleResponse get(@PathVariable UUID scheduleId) {
        return scheduleService.getSchedule(scheduleId);
    }

    @GetMapping("/{scheduleId}/runs")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN','OPERATIONS')")
    public List<MonitoringRunResponse> getRuns(@PathVariable UUID scheduleId) {
        return scheduleService.getRuns(scheduleId);
    }

    @PostMapping("/{scheduleId}/run-due")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATIONS')")
    public ResponseEntity<MonitoringRunResponse> runIfDue(@PathVariable UUID scheduleId) {
        MonitoringRunResponse run = runService.runScheduleIfDue(scheduleId, OffsetDateTime.now());
        return run == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(run);
    }
}
