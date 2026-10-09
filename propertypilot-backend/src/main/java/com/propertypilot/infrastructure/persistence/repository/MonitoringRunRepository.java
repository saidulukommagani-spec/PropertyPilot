package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.MonitoringRunEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MonitoringRunRepository extends JpaRepository<MonitoringRunEntity, UUID> {
    List<MonitoringRunEntity> findByMonitoringSchedule_MonitoringScheduleIdOrderByRunNumberAsc(UUID scheduleId);
    Optional<MonitoringRunEntity> findTopByMonitoringSchedule_MonitoringScheduleIdOrderByRunNumberDesc(UUID scheduleId);
    boolean existsByMonitoringSchedule_MonitoringScheduleIdAndCycleKey(UUID scheduleId, String cycleKey);
}
