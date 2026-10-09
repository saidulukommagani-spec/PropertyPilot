package com.propertypilot.web.controller;

import com.propertypilot.application.dto.LeadAssignRequest;
import com.propertypilot.application.dto.LeadAssignmentResponse;
import com.propertypilot.application.dto.LeadCreateRequest;
import com.propertypilot.application.dto.LeadResponse;
import com.propertypilot.application.dto.LeadDealConversionRequest;
import com.propertypilot.application.dto.DealResponse;
import com.propertypilot.application.service.DealService;
import jakarta.validation.Valid;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import com.propertypilot.application.dto.OperationsLeadResponse;
import com.propertypilot.application.dto.OperationsLeadPageResponse;
import com.propertypilot.web.exception.BusinessException;

@RestController
@Validated
@RequestMapping("/api/v1/leads")
@Tag(
        name = "Lead Management",
        description = "Lead lifecycle management APIs"
)
public class LeadController {

    private final LeadService leadService;
    private final DealService dealService;

    public LeadController(
            LeadService leadService,
            DealService dealService) {

        this.leadService = leadService;
        this.dealService = dealService;
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
    @PreAuthorize("hasRole('CUSTOMER')")
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
    @PreAuthorize("hasAnyRole('ADMIN','OPERATIONS','CUSTOMER','AGENT')")
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasAnyRole('CUSTOMER','AGENT')")
    public List<LeadResponse> getMyLeads() {

        return leadService.getMyLeads();
    }

    @Operation(summary = "Search the operations lead queue",
            description = "Returns a paged lead queue searchable by customer name/contact, source, property type and location.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OPERATIONS')")
    public OperationsLeadPageResponse searchOperationsLeads(
            @RequestParam(required = false) @Size(max = 200) String search,
            @RequestParam(required = false) LeadStatus status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "25") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        if (!List.of("createdAt", "status", "source", "location", "propertyType").contains(sort)) {
            throw new BusinessException("Unsupported lead sort field");
        }
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(direction, sort).and(Sort.by(Sort.Direction.ASC, "leadId")));
        return OperationsLeadPageResponse.from(leadService.searchOperationsLeads(search, status, pageable));
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
    @PreAuthorize("hasRole('ADMIN')")
    public LeadAssignmentResponse assignLead(
            @PathVariable
            UUID leadId,

            @RequestBody
            LeadAssignRequest request) {

        return leadService.assignLead(
                leadId,
                request);
    }

    @Operation(summary = "Convert lead to deal", description = "Creates a linked deal and records the lead conversion atomically")
    @PostMapping("/{leadId}/convert-to-deal")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public DealResponse convertToDeal(
            @PathVariable UUID leadId,
            @Valid @RequestBody LeadDealConversionRequest request) {
        return dealService.convertLeadToDeal(leadId, request);
    }

    @Operation(summary = "Get deals linked to lead")
    @GetMapping("/{leadId}/deals")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATIONS','CUSTOMER','AGENT')")
    public List<DealResponse> getDeals(@PathVariable UUID leadId) {
        return dealService.getDealsByLead(leadId);
    }
}
