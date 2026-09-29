package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CustomerDashboardResponse;
import com.propertypilot.application.service.CustomerDashboardService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/customer-dashboard")
public class CustomerDashboardController {

    private final CustomerDashboardService
            customerDashboardService;

    public CustomerDashboardController(
            CustomerDashboardService customerDashboardService) {

        this.customerDashboardService =
                customerDashboardService;
    }

    @GetMapping("/{customerId}")
    public CustomerDashboardResponse getDashboard(
            @PathVariable
            UUID customerId) {

        return customerDashboardService
                .getDashboard(customerId);
    }
}