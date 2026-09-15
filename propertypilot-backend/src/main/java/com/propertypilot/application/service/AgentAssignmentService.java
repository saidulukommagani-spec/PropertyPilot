package com.propertypilot.application.service;

import com.propertypilot.application.dto.AgentAssignmentResponse;
import com.propertypilot.application.dto.CreateAgentAssignmentRequest;
import com.propertypilot.application.dto.RejectAgentAssignmentRequest;

import java.util.UUID;

public interface AgentAssignmentService {

    AgentAssignmentResponse createAssignment(
            CreateAgentAssignmentRequest request);

    AgentAssignmentResponse getAssignment(
            UUID assignmentId);

    AgentAssignmentResponse acceptAssignment(
            UUID assignmentId);

    AgentAssignmentResponse rejectAssignment(
            UUID assignmentId,
            RejectAgentAssignmentRequest request);

    AgentAssignmentResponse startAssignment(
            UUID assignmentId);

    AgentAssignmentResponse completeAssignment(
            UUID assignmentId);
}