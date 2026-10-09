package com.propertypilot.web.controller;

import com.propertypilot.application.dto.AgentDirectoryResponse;
import com.propertypilot.application.dto.AssignServiceRequestAgentRequest;
import com.propertypilot.application.dto.ServiceRequestAssignmentHistoryResponse;
import com.propertypilot.application.dto.ServiceRequestResponse;
import com.propertypilot.application.service.ServiceRequestService;
import com.propertypilot.infrastructure.persistence.repository.AgentRepository;
import com.propertypilot.infrastructure.persistence.repository.AgentSkillRepository;
import com.propertypilot.infrastructure.persistence.repository.AgentAssignmentRepository;
import com.propertypilot.security.SecurityService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class StaffAssignmentController {
    private final AgentRepository agentRepository;
    private final AgentSkillRepository agentSkillRepository;
    private final AgentAssignmentRepository assignmentRepository;
    private final SecurityService securityService;
    private final ServiceRequestService serviceRequestService;

    public StaffAssignmentController(AgentRepository agentRepository, AgentSkillRepository agentSkillRepository,
                                     AgentAssignmentRepository assignmentRepository, SecurityService securityService,
                                     ServiceRequestService serviceRequestService) {
        this.agentRepository = agentRepository;
        this.agentSkillRepository = agentSkillRepository;
        this.assignmentRepository = assignmentRepository;
        this.securityService = securityService;
        this.serviceRequestService = serviceRequestService;
    }

    @GetMapping("/agents")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATIONS')")
    public List<AgentDirectoryResponse> getActiveAgents() {
        securityService.validateOperationsAccess();
        return agentRepository.findAllByStatusOrderByUser_FullNameAsc("ACTIVE").stream()
                .map(this::toDirectoryResponse)
                .toList();
    }

    @GetMapping("/agents/profile")
    @PreAuthorize("hasRole('AGENT')")
    public AgentDirectoryResponse getAgentProfile() {
        var userId = securityService.getCurrentUserId();
        var agent = agentRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new com.propertypilot.web.exception.ResourceNotFoundException("Agent profile not found"));
        return toDirectoryResponse(agent);
    }

    @GetMapping("/admin/agents")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATIONS')")
    public List<AgentDirectoryResponse> getAllAgents() {
        securityService.validateOperationsAccess();
        return agentRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(agent -> agent.getUser().getFullName(), String.CASE_INSENSITIVE_ORDER))
                .map(this::toDirectoryResponse)
                .toList();
    }

    @PostMapping("/service-requests/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATIONS')")
    public ServiceRequestResponse assignAgent(@PathVariable UUID id,
                                              @Valid @RequestBody AssignServiceRequestAgentRequest request) {
        return serviceRequestService.assignAgentWithNotes(id, request.getAgentId(), request.getNotes());
    }

    @GetMapping("/service-requests/{id}/assignment-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATIONS')")
    public List<ServiceRequestAssignmentHistoryResponse> getAssignmentHistory(@PathVariable UUID id) {
        return serviceRequestService.getAssignmentHistory(id);
    }

    private AgentDirectoryResponse toDirectoryResponse(com.propertypilot.infrastructure.persistence.entity.AgentEntity agent) {
        long activeAssignments = assignmentRepository.findByAgent_AgentId(agent.getAgentId()).stream()
                .filter(assignment -> List.of("ASSIGNED", "ACCEPTED", "IN_PROGRESS").contains(assignment.getAssignmentStatus()))
                .count();
        List<String> skills = agentSkillRepository.findByAgent_AgentIdAndStatus(agent.getAgentId(), "ACTIVE").stream()
                .map(skill -> skill.getSkillName()).distinct().sorted().toList();
        return new AgentDirectoryResponse(agent.getAgentId(), agent.getAgentCode(), agent.getUser().getFullName(),
                agent.getUser().getMobileNumber(), agent.getStatus(), skills,
                activeAssignments == 0 ? "AVAILABLE" : "BUSY", activeAssignments, agent.getRegion());
    }
}
