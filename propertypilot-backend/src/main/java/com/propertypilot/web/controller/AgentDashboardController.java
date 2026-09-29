package com.propertypilot.web.controller;

import com.propertypilot.application.dto.AgentDashboardResponse;
import com.propertypilot.application.service.AgentDashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/agents")
public class AgentDashboardController {

    private final AgentDashboardService
            agentDashboardService;

    public AgentDashboardController(
            AgentDashboardService agentDashboardService) {

        this.agentDashboardService =
                agentDashboardService;
    }

    @GetMapping("/dashboard")
    public AgentDashboardResponse
    getDashboard() {

        return agentDashboardService
                .getDashboard();
    }
}