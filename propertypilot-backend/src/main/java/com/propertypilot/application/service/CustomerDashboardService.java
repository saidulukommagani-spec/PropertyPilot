package com.propertypilot.application.service;

import com.propertypilot.application.dto.CustomerDashboardResponse;

import java.util.UUID;

public interface CustomerDashboardService {

    CustomerDashboardResponse getDashboard(
            UUID customerId);
}