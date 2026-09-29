package com.propertypilot.application.dto;

public class AgentDashboardResponse {

    private long totalAgents;

    private long activeAgents;

    private long assignedAgents;

    private long completedAssignments;

    private long rejectedAssignments;

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
        this.completedAssignments = completedAssignments;
    }

    public long getRejectedAssignments() {
        return rejectedAssignments;
    }

    public void setRejectedAssignments(long rejectedAssignments) {
        this.rejectedAssignments = rejectedAssignments;
    }

    public long getActiveAssignments() {
        return activeAssignments;
    }

    public void setActiveAssignments(long activeAssignments) {
        this.activeAssignments = activeAssignments;
    }

   
}