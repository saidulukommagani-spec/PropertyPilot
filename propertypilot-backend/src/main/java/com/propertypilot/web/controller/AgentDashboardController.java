package com.propertypilot.web.controller;

import com.propertypilot.application.dto.AgentDashboardResponse;
import com.propertypilot.application.service.AgentDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/agents")
@Tag(
        name = "Agent Dashboard",
        description = "Administrative dashboard APIs for agent management and assignment analytics")
@SecurityRequirement(name = "bearerAuth")
public class AgentDashboardController {

    private final AgentDashboardService
            agentDashboardService;

    public AgentDashboardController(
            AgentDashboardService agentDashboardService) {

        this.agentDashboardService =
                agentDashboardService;
    }

    @Operation(
            summary = "Get agent dashboard",
            description =
                    "Returns consolidated agent statistics including total agents, active agents, assignments, completions and rejections.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Dashboard retrieved successfully"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error")
    })
    @GetMapping("/dashboard")
    public AgentDashboardResponse
    getDashboard() {

        return agentDashboardService
                .getDashboard();
    }
}