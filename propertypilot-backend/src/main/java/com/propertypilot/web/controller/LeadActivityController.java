package com.propertypilot.web.controller;

import com.propertypilot.application.dto.LeadActivityCreateRequest;
import com.propertypilot.application.dto.LeadActivityResponse;
import com.propertypilot.application.service.LeadActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/leads/{leadId}/activities")
@PreAuthorize("hasAnyRole('ADMIN','OPERATIONS','CUSTOMER','AGENT')")
@Tag(name = "Lead Activity", description = "Lead audit and engagement history")
public class LeadActivityController {
    private final LeadActivityService service;

    public LeadActivityController(LeadActivityService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get the lead audit timeline")
    public List<LeadActivityResponse> getActivities(@PathVariable UUID leadId) {
        return service.getActivities(leadId);
    }

    @PostMapping
    @Operation(summary = "Record a lead activity")
    public LeadActivityResponse createActivity(
            @PathVariable UUID leadId,
            @Valid @RequestBody LeadActivityCreateRequest request) {
        return service.createActivity(leadId, request);
    }
}
