package com.propertypilot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "AgentDashboardResponse",
        description = "Agent dashboard statistics")
public class AgentDashboardResponse {

    @Schema(
            description = "Total registered agents",
            example = "150")
    private long totalAgents;

    @Schema(
            description = "Currently active agents",
            example = "120")
    private long activeAgents;

    @Schema(
            description = "Agents currently assigned to work",
            example = "90")
    private long assignedAgents;

    @Schema(
            description = "Total completed assignments",
            example = "2500")
    private long completedAssignments;

    @Schema(
            description = "Total rejected assignments",
            example = "35")
    private long rejectedAssignments;

    @Schema(
            description = "Active assignments currently in progress",
            example = "75")
    private long activeAssignments;

    public long getTotalAgents() {
        return totalAgents;
    }

    public void setTotalAgents(long totalAgents) {
        this.totalAgents = totalAgents;
    }

    public long getActiveAgents() {
        return activeAgents;
    }

    public void setActiveAgents(long activeAgents) {
        this.activeAgents = activeAgents;
    }

    public long getAssignedAgents() {
        return assignedAgents;
    }

    public void setAssignedAgents(long assignedAgents) {
        this.assignedAgents = assignedAgents;
    }

    public long getCompletedAssignments() {
        return completedAssignments;
    }

    public void setCompletedAssignments(long completedAssignments) {
        this.completedAssignments =
                completedAssignments;
    }

    public long getRejectedAssignments() {
        return rejectedAssignments;
    }

    public void setRejectedAssignments(long rejectedAssignments) {
        this.rejectedAssignments =
                rejectedAssignments;
    }

    public long getActiveAssignments() {
        return activeAssignments;
    }

    public void setActiveAssignments(long activeAssignments) {
        this.activeAssignments =
                activeAssignments;
    }
}