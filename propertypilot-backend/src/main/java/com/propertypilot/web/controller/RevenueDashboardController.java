package com.propertypilot.web.controller;

import com.propertypilot.application.dto.RevenueDashboardResponse;
import com.propertypilot.application.service.RevenueDashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/revenue")
public class RevenueDashboardController {

    private final RevenueDashboardService
            revenueDashboardService;

    public RevenueDashboardController(
            RevenueDashboardService revenueDashboardService) {

        this.revenueDashboardService =
                revenueDashboardService;
    }

    @GetMapping
    public RevenueDashboardResponse
    getRevenueDashboard() {

        return revenueDashboardService
                .getRevenueDashboard();
    }
}