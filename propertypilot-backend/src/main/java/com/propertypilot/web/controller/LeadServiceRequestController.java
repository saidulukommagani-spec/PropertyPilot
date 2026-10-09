package com.propertypilot.web.controller;

import com.propertypilot.application.dto.LeadServiceRequestResponse;
import com.propertypilot.application.service.LeadServiceRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/leads/{leadId}/service-requests")
@PreAuthorize("hasAnyRole('ADMIN','OPERATIONS','CUSTOMER','AGENT')")
@Tag(name = "Lead Service Requests", description = "Service delivery linked to a lead")
public class LeadServiceRequestController {
    private final LeadServiceRequestService service;

    public LeadServiceRequestController(LeadServiceRequestService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List requests linked to a lead", description = "Includes current SLA, fulfillment counts, agent, and invoiced/collected service revenue.")
    public List<LeadServiceRequestResponse> getLinkedRequests(@PathVariable UUID leadId) {
        return service.getLeadServiceRequests(leadId);
    }
}
