package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "monitoring_runs", uniqueConstraints = {
        @UniqueConstraint(name = "uq_monitoring_run_number", columnNames = {"monitoring_schedule_id", "run_number"}),
        @UniqueConstraint(name = "uq_monitoring_run_service_request", columnNames = "service_request_id")
})
public class MonitoringRunEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "monitoring_run_id", nullable = false, updatable = false)
    private UUID monitoringRunId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "monitoring_schedule_id", nullable = false, updatable = false)
    private MonitoringScheduleEntity monitoringSchedule;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_request_id", nullable = false, updatable = false)
    private ServiceRequestEntity serviceRequest;

    @Column(name = "run_number", nullable = false, updatable = false)
    private Integer runNumber;

    @Column(name = "cycle_key", nullable = false, updatable = false, length = 120)
    private String cycleKey;

    @Column(name = "scheduled_for", nullable = false, updatable = false)
    private OffsetDateTime scheduledFor;

    @Column(name = "started_at", nullable = false, updatable = false)
    private OffsetDateTime startedAt;

    @Column(name = "run_status", nullable = false, updatable = false, length = 30)
    private String runStatus;

    @Version
    @Column(name = "version", nullable = false, updatable = false)
    private Long version;

    public UUID getMonitoringRunId() { return monitoringRunId; }
    public void setMonitoringRunId(UUID monitoringRunId) { this.monitoringRunId = monitoringRunId; }
    public MonitoringScheduleEntity getMonitoringSchedule() { return monitoringSchedule; }
    public void setMonitoringSchedule(MonitoringScheduleEntity monitoringSchedule) { this.monitoringSchedule = monitoringSchedule; }
    public ServiceRequestEntity getServiceRequest() { return serviceRequest; }
    public void setServiceRequest(ServiceRequestEntity serviceRequest) { this.serviceRequest = serviceRequest; }
    public Integer getRunNumber() { return runNumber; }
    public void setRunNumber(Integer runNumber) { this.runNumber = runNumber; }
    public String getCycleKey() { return cycleKey; }
    public void setCycleKey(String cycleKey) { this.cycleKey = cycleKey; }
    public OffsetDateTime getScheduledFor() { return scheduledFor; }
    public void setScheduledFor(OffsetDateTime scheduledFor) { this.scheduledFor = scheduledFor; }
    public OffsetDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(OffsetDateTime startedAt) { this.startedAt = startedAt; }
    public String getRunStatus() { return runStatus; }
    public void setRunStatus(String runStatus) { this.runStatus = runStatus; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
