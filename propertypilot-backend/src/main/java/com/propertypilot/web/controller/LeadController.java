package com.propertypilot.web.controller;

import com.propertypilot.application.dto.LeadCreateRequest;
import com.propertypilot.application.dto.LeadResponse;
import com.propertypilot.application.service.LeadService;
import com.propertypilot.domain.enums.LeadStatus;
import org.springframework.web.bind.annotation.*;
import com.propertypilot.application.dto.LeadAssignRequest;
import com.propertypilot.application.dto.LeadAssignmentResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leads")
public class LeadController {

    private final LeadService leadService;

    public LeadController(
            LeadService leadService) {

        this.leadService = leadService;
    }

    @PostMapping
    public LeadResponse createLead(
            @RequestBody
            LeadCreateRequest request) {

        return leadService.createLead(
                request);
    }

    @GetMapping("/{leadId}")
    public LeadResponse getLead(
            @PathVariable
            UUID leadId) {

        return leadService.getLead(
                leadId);
    }

    @PatchMapping("/{leadId}/status")
    public LeadResponse updateStatus(
            @PathVariable
            UUID leadId,

            @RequestParam
            LeadStatus status) {

        return leadService.updateLeadStatus(
                leadId,
                status);
    }

    @GetMapping("/my")
    public List<LeadResponse> getMyLeads() {

        return leadService.getMyLeads();
    }
@PatchMapping("/{leadId}/assign")
public LeadAssignmentResponse assignLead(
        @PathVariable
        UUID leadId,

        @RequestBody
        LeadAssignRequest request) {

    return leadService.assignLead(
            leadId,
            request);
}

}