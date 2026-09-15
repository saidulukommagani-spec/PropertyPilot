package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateReportRequest;
import com.propertypilot.application.dto.ReportResponse;
import com.propertypilot.application.service.ReportService;
import com.propertypilot.infrastructure.persistence.entity.ReportEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.repository.ReportRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ServiceRequestRepository serviceRequestRepository;

    public ReportServiceImpl(
            ReportRepository reportRepository,
            ServiceRequestRepository serviceRequestRepository) {

        this.reportRepository = reportRepository;
        this.serviceRequestRepository = serviceRequestRepository;
    }

    @Override
    public ReportResponse createReport(
            CreateReportRequest request) {

        ServiceRequestEntity serviceRequest =
                serviceRequestRepository.findById(
                                request.getServiceRequestId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Service request not found"));

        ReportEntity report = new ReportEntity();

        report.setServiceRequest(serviceRequest);
        report.setReportType(request.getReportType());
        report.setSummary(request.getSummary());
        report.setDocumentUrl(request.getDocumentUrl());
        report.setReportStatus("DRAFT");

        return buildResponse(
                reportRepository.save(report));
    }

    @Override
    public ReportResponse getReport(
            UUID reportId) {

        return buildResponse(
                getReportEntity(reportId));
    }

    @Override
    public ReportResponse submitReport(
            UUID reportId) {

        ReportEntity report =
                getReportEntity(reportId);

        report.setReportStatus("SUBMITTED");
        report.setSubmittedAt(
                OffsetDateTime.now());

        return buildResponse(
                reportRepository.save(report));
    }

    @Override
    public ReportResponse approveReport(
            UUID reportId) {

        ReportEntity report =
                getReportEntity(reportId);

        report.setReportStatus("APPROVED");
        report.setApprovedAt(
                OffsetDateTime.now());

        return buildResponse(
                reportRepository.save(report));
    }

    @Override
    public ReportResponse deliverReport(
            UUID reportId) {

        ReportEntity report =
                getReportEntity(reportId);

        report.setReportStatus("DELIVERED");
        report.setDeliveredAt(
                OffsetDateTime.now());

        return buildResponse(
                reportRepository.save(report));
    }

    private ReportEntity getReportEntity(
            UUID reportId) {

        return reportRepository.findById(reportId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Report not found"));
    }

    private ReportResponse buildResponse(
            ReportEntity report) {

        ReportResponse response =
                new ReportResponse();

        response.setReportId(
                report.getReportId());

        response.setServiceRequestId(
                report.getServiceRequest()
                        .getServiceRequestId());

        response.setReportType(
                report.getReportType());

        response.setReportStatus(
                report.getReportStatus());

        response.setSummary(
                report.getSummary());

        response.setDocumentUrl(
                report.getDocumentUrl());

        response.setSubmittedAt(
                report.getSubmittedAt());

        response.setApprovedAt(
                report.getApprovedAt());

        response.setDeliveredAt(
                report.getDeliveredAt());

        return response;
    }
}