package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateReportRequest;
import com.propertypilot.application.dto.ReportResponse;
import com.propertypilot.application.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(
            ReportService reportService) {

        this.reportService = reportService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportResponse createReport(
            @RequestBody
            CreateReportRequest request) {

        return reportService.createReport(
                request);
    }

    @GetMapping("/{reportId}")
    public ReportResponse getReport(
            @PathVariable
            UUID reportId) {

        return reportService.getReport(
                reportId);
    }

    @PutMapping("/{reportId}/submit")
    public ReportResponse submitReport(
            @PathVariable
            UUID reportId) {

        return reportService.submitReport(
                reportId);
    }

    @PutMapping("/{reportId}/approve")
    public ReportResponse approveReport(
            @PathVariable
            UUID reportId) {

        return reportService.approveReport(
                reportId);
    }

    @PutMapping("/{reportId}/deliver")
    public ReportResponse deliverReport(
            @PathVariable
            UUID reportId) {

        return reportService.deliverReport(
                reportId);
    }
}