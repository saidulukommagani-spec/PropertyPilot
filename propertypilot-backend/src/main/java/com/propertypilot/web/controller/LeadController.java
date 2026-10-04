package com.propertypilot.web.controller;

import com.propertypilot.application.dto.LeadAssignRequest;
import com.propertypilot.application.dto.LeadAssignmentResponse;
import com.propertypilot.application.dto.LeadCreateRequest;
import com.propertypilot.application.dto.LeadResponse;
import com.propertypilot.application.service.LeadService;
import com.propertypilot.domain.enums.LeadStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leads")
@Tag(
        name = "Lead Management",
        description = "Lead lifecycle management APIs"
)
public class LeadController {

    private final LeadService leadService;

    public LeadController(
            LeadService leadService) {

        this.leadService = leadService;
    }

    @Operation(
            summary = "Create Lead",
            description = "Creates a new lead"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Lead created successfully",
                    content = @Content(
                            schema =
                            @Schema(
                                    implementation =
                                            LeadResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeadResponse createLead(
            @RequestBody
            LeadCreateRequest request) {

        return leadService.createLead(
                request);
    }

    @Operation(
            summary = "Get Lead",
            description = "Fetch lead by ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lead retrieved"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Lead not found")
    })
    @GetMapping("/{leadId}")
    public LeadResponse getLead(
            @Parameter(
                    description = "Lead ID")
            @PathVariable
            UUID leadId) {

        return leadService.getLead(
                leadId);
    }

    @Operation(
            summary = "Update Lead Status",
            description = "Updates lead status"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Status updated")
    })
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

    @Operation(
            summary = "Get My Leads",
            description = "Returns logged-in user's leads"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Leads retrieved")
    })
    @GetMapping("/my")
    public List<LeadResponse> getMyLeads() {

        return leadService.getMyLeads();
    }

    @Operation(
            summary = "Assign Lead",
            description = "Assign lead to agent"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lead assigned successfully")
    })
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