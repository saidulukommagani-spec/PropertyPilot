package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateReportRequest;
import com.propertypilot.application.dto.ReportResponse;
import com.propertypilot.application.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@Tag(
        name = "Report Management",
        description = "APIs for report lifecycle management"
)
public class ReportController {

    private final ReportService reportService;

    public ReportController(
            ReportService reportService) {

        this.reportService = reportService;
    }

    @Operation(
            summary = "Create report",
            description =
                    "Creates a report for a service request"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Report created successfully"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportResponse createReport(
            @RequestBody
            CreateReportRequest request) {

        return reportService.createReport(
                request);
    }

    @Operation(
            summary = "Get report",
            description =
                    "Retrieves report by report id"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Report retrieved successfully"
    )
    @GetMapping("/{reportId}")
    public ReportResponse getReport(
            @PathVariable
            UUID reportId) {

        return reportService.getReport(
                reportId);
    }

    @Operation(
            summary = "Submit report",
            description =
                    "Submits report for approval"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Report submitted successfully"
    )
    @PutMapping("/{reportId}/submit")
    public ReportResponse submitReport(
            @PathVariable
            UUID reportId) {

        return reportService.submitReport(
                reportId);
    }

    @Operation(
            summary = "Approve report",
            description =
                    "Approves submitted report"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Report approved successfully"
    )
    @PutMapping("/{reportId}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS')")
    public ReportResponse approveReport(
            @PathVariable
            UUID reportId) {

        return reportService.approveReport(
                reportId);
    }

    @Operation(
            summary = "Deliver report",
            description =
                    "Marks report as delivered"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Report delivered successfully"
    )
    @PutMapping("/{reportId}/deliver")
    public ReportResponse deliverReport(
            @PathVariable
            UUID reportId) {

        return reportService.deliverReport(
                reportId);
    }
}
