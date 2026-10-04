package com.propertypilot.web.controller;

import com.propertypilot.application.dto.AnalyticsDashboardResponse;
import com.propertypilot.application.service.AnalyticsDashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/analytics")
@Validated
@PreAuthorize("hasRole('ADMIN')")
@Tag(
        name = "Analytics Dashboard",
        description = "Administrative analytics and KPI dashboard APIs"
)
public class AnalyticsDashboardController {

    private final AnalyticsDashboardService
            analyticsDashboardService;

    public AnalyticsDashboardController(
            AnalyticsDashboardService analyticsDashboardService) {

        this.analyticsDashboardService =
                analyticsDashboardService;
    }

    @GetMapping
    @Operation(
            summary = "Get Analytics Dashboard",
            description =
                    "Returns PropertyPilot system level analytics dashboard metrics."
    )
    public ResponseEntity<AnalyticsDashboardResponse>
    getAnalyticsDashboard() {

        return ResponseEntity.ok(
                analyticsDashboardService
                        .getAnalyticsDashboard());
    }
}