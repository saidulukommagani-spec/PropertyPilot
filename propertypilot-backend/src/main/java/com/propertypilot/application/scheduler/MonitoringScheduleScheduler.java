package com.propertypilot.application.scheduler;

import com.propertypilot.application.service.MonitoringRunService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class MonitoringScheduleScheduler {
    private static final Logger log = LoggerFactory.getLogger(MonitoringScheduleScheduler.class);
    private final MonitoringRunService runService;

    public MonitoringScheduleScheduler(MonitoringRunService runService) {
        this.runService = runService;
    }

    @Scheduled(fixedDelayString = "${propertypilot.monitoring.scan-ms:60000}")
    public void processDueSchedules() {
        int runs = runService.processDueSchedules(OffsetDateTime.now());
        if (runs > 0) log.info("Created {} due Monitoring runs", runs);
    }
}
