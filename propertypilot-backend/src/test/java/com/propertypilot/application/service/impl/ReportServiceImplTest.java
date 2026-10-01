package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateReportRequest;
import com.propertypilot.application.dto.ReportResponse;
import com.propertypilot.infrastructure.persistence.entity.ReportEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.repository.ReportRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ServiceRequestRepository
            serviceRequestRepository;

    @InjectMocks
    private ReportServiceImpl service;

    @Test
    void createReport() {

        UUID serviceRequestId =
                UUID.randomUUID();

        UUID reportId =
                UUID.randomUUID();

        CreateReportRequest request =
                new CreateReportRequest();

        request.setServiceRequestId(
                serviceRequestId);

        request.setReportType(
                "LEGAL");

        request.setSummary(
                "Summary");

        request.setDocumentUrl(
                "http://test.com/report.pdf");

        ServiceRequestEntity serviceRequest =
                new ServiceRequestEntity();

        serviceRequest.setServiceRequestId(
                serviceRequestId);

        ReportEntity saved =
                new ReportEntity();

        saved.setReportId(reportId);
        saved.setServiceRequest(serviceRequest);
        saved.setReportType("LEGAL");
        saved.setReportStatus("DRAFT");
        saved.setSummary("Summary");
        saved.setDocumentUrl(
                "http://test.com/report.pdf");

        when(serviceRequestRepository.findById(
                serviceRequestId))
                .thenReturn(
                        Optional.of(serviceRequest));

        when(reportRepository.save(any()))
                .thenReturn(saved);

        ReportResponse response =
                service.createReport(request);

        assertThat(response)
                .isNotNull();

        assertThat(response.getReportId())
                .isEqualTo(reportId);

        assertThat(response.getReportStatus())
                .isEqualTo("DRAFT");

        verify(reportRepository)
                .save(any(ReportEntity.class));
    }

    @Test
    void createReport_serviceRequestNotFound() {

        UUID serviceRequestId =
                UUID.randomUUID();

        CreateReportRequest request =
                new CreateReportRequest();

        request.setServiceRequestId(
                serviceRequestId);

        when(serviceRequestRepository.findById(
                serviceRequestId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.createReport(request))
                .isInstanceOf(
                        ResourceNotFoundException.class)
                .hasMessage(
                        "Service request not found");
    }

    @Test
    void getReport() {

        UUID reportId =
                UUID.randomUUID();

        UUID serviceRequestId =
                UUID.randomUUID();

        ServiceRequestEntity serviceRequest =
                new ServiceRequestEntity();

        serviceRequest.setServiceRequestId(
                serviceRequestId);

        ReportEntity report =
                new ReportEntity();

        report.setReportId(reportId);
        report.setServiceRequest(serviceRequest);
        report.setReportType("LEGAL");
        report.setReportStatus("DRAFT");
        report.setSummary("Summary");

        when(reportRepository.findById(reportId))
                .thenReturn(Optional.of(report));

        ReportResponse response =
                service.getReport(reportId);

        assertThat(response)
                .isNotNull();

        assertThat(response.getReportId())
                .isEqualTo(reportId);
    }

    @Test
    void getReport_notFound() {

        UUID reportId =
                UUID.randomUUID();

        when(reportRepository.findById(reportId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.getReport(reportId))
                .isInstanceOf(
                        ResourceNotFoundException.class)
                .hasMessage("Report not found");
    }

    @Test
    void submitReport() {

        UUID reportId =
                UUID.randomUUID();

        ServiceRequestEntity serviceRequest =
                new ServiceRequestEntity();

        serviceRequest.setServiceRequestId(
                UUID.randomUUID());

        ReportEntity report =
                new ReportEntity();

        report.setReportId(reportId);
        report.setServiceRequest(serviceRequest);

        when(reportRepository.findById(reportId))
                .thenReturn(Optional.of(report));

        when(reportRepository.save(any()))
                .thenAnswer(
                        invocation ->
                                invocation.getArgument(0));

        ReportResponse response =
                service.submitReport(reportId);

        assertThat(response.getReportStatus())
                .isEqualTo("SUBMITTED");

        assertThat(response.getSubmittedAt())
                .isNotNull();
    }

    @Test
    void approveReport() {

        UUID reportId =
                UUID.randomUUID();

        ServiceRequestEntity serviceRequest =
                new ServiceRequestEntity();

        serviceRequest.setServiceRequestId(
                UUID.randomUUID());

        ReportEntity report =
                new ReportEntity();

        report.setReportId(reportId);
        report.setServiceRequest(serviceRequest);

        when(reportRepository.findById(reportId))
                .thenReturn(Optional.of(report));

        when(reportRepository.save(any()))
                .thenAnswer(
                        invocation ->
                                invocation.getArgument(0));

        ReportResponse response =
                service.approveReport(reportId);

        assertThat(response.getReportStatus())
                .isEqualTo("APPROVED");

        assertThat(response.getApprovedAt())
                .isNotNull();
    }

    @Test
    void deliverReport() {

        UUID reportId =
                UUID.randomUUID();

        ServiceRequestEntity serviceRequest =
                new ServiceRequestEntity();

        serviceRequest.setServiceRequestId(
                UUID.randomUUID());

        ReportEntity report =
                new ReportEntity();

        report.setReportId(reportId);
        report.setServiceRequest(serviceRequest);

        when(reportRepository.findById(reportId))
                .thenReturn(Optional.of(report));

        when(reportRepository.save(any()))
                .thenAnswer(
                        invocation ->
                                invocation.getArgument(0));

        ReportResponse response =
                service.deliverReport(reportId);

        assertThat(response.getReportStatus())
                .isEqualTo("DELIVERED");

        assertThat(response.getDeliveredAt())
                .isNotNull();
    }
}