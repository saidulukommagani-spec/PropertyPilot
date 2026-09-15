package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreateReportRequest;
import com.propertypilot.application.dto.ReportResponse;

import java.util.UUID;

public interface ReportService {

    ReportResponse createReport(
            CreateReportRequest request);

    ReportResponse getReport(
            UUID reportId);

    ReportResponse submitReport(
            UUID reportId);

    ReportResponse approveReport(
            UUID reportId);

    ReportResponse deliverReport(
            UUID reportId);
}