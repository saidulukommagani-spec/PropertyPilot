package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.AgentDashboardResponse;
import com.propertypilot.application.service.AgentDashboardService;
import com.propertypilot.infrastructure.persistence.repository.AgentAssignmentRepository;
import com.propertypilot.infrastructure.persistence.repository.AgentRepository;
import org.springframework.stereotype.Service;

@Service
public class AgentDashboardServiceImpl
        implements AgentDashboardService {

    private final AgentRepository agentRepository;

    private final AgentAssignmentRepository
            assignmentRepository;

    public AgentDashboardServiceImpl(
            AgentRepository agentRepository,
            AgentAssignmentRepository assignmentRepository) {

        this.agentRepository =
                agentRepository;

        this.assignmentRepository =
                assignmentRepository;
    }

    @Override
    public AgentDashboardResponse getDashboard() {

        AgentDashboardResponse response =
                new AgentDashboardResponse();

        response.setTotalAgents(
                agentRepository.count());

        response.setActiveAgents(
                agentRepository.countByStatus(
                        "ACTIVE"));

        response.setAssignedAgents(
                assignmentRepository.countByAssignmentStatus(
                        "ASSIGNED"));

        response.setActiveAssignments(
                assignmentRepository.countByAssignmentStatus(
                        "IN_PROGRESS"));

        response.setCompletedAssignments(
                assignmentRepository.countByAssignmentStatus(
                        "COMPLETED"));

        response.setRejectedAssignments(
                assignmentRepository.countByAssignmentStatus(
                        "REJECTED"));

        return response;
    }
}