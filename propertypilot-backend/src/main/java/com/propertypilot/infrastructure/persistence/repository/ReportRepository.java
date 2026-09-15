package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReportRepository
        extends JpaRepository<ReportEntity, UUID> {

    List<ReportEntity>
    findByServiceRequest_ServiceRequestId(
            UUID serviceRequestId);

    List<ReportEntity>
    findByReportStatus(
            String reportStatus);

    List<ReportEntity>
    findByReportType(
            String reportType);
}