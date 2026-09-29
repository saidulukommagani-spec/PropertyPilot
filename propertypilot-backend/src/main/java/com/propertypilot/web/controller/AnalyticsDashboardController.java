package com.propertypilot.web.controller;

import com.propertypilot.application.dto.AnalyticsDashboardResponse;
import com.propertypilot.application.service.AnalyticsDashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/analytics")
public class AnalyticsDashboardController {

    private final AnalyticsDashboardService
            analyticsDashboardService;

    public AnalyticsDashboardController(
            AnalyticsDashboardService analyticsDashboardService) {

        this.analyticsDashboardService =
                analyticsDashboardService;
    }

    @GetMapping
    public AnalyticsDashboardResponse
    getAnalyticsDashboard() {

        return analyticsDashboardService
                .getAnalyticsDashboard();
    }
}