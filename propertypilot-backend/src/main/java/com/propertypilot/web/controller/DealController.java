package com.propertypilot.web.controller;

import com.propertypilot.application.dto.*;
import com.propertypilot.application.service.DealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Deal Management",
        description = "Deal lifecycle management APIs"
)
@RestController
@RequestMapping("/api/v1/deals")
public class DealController {

    private final DealService dealService;

    public DealController(
            DealService dealService) {

        this.dealService = dealService;
    }

    @Operation(
            summary = "Create Deal",
            description = "Creates a new property deal"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deal created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<DealResponse> createDeal(
            @Valid @RequestBody CreateDealRequest request) {

        return ResponseEntity.ok(
                dealService.createDeal(request));
    }

    @Operation(
            summary = "Get Deal",
            description = "Retrieve deal by ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deal found"),
            @ApiResponse(responseCode = "404", description = "Deal not found")
    })
    @GetMapping("/{dealId}")
    public ResponseEntity<DealResponse> getDeal(
            @PathVariable UUID dealId) {

        return ResponseEntity.ok(
                dealService.getDeal(dealId));
    }

    @Operation(
            summary = "Get All Deals",
            description = "Retrieve all deals"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deals retrieved")
    })
    @GetMapping
    public ResponseEntity<List<DealResponse>> getAllDeals() {

        return ResponseEntity.ok(
                dealService.getAllDeals());
    }

    @Operation(
            summary = "Add Participant",
            description = "Adds participant to deal"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Participant added")
    })
    @PostMapping("/{dealId}/participants")
    public ResponseEntity<DealParticipantResponse> addParticipant(
            @PathVariable UUID dealId,
            @RequestBody AddDealParticipantRequest request) {

        return ResponseEntity.ok(
                dealService.addParticipant(
                        dealId,
                        request));
    }

    @Operation(
            summary = "Get Deal Participants",
            description = "Returns all participants for a deal"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Participants retrieved")
    })
    @GetMapping("/{dealId}/participants")
    public ResponseEntity<List<DealParticipantResponse>> getParticipants(
            @PathVariable UUID dealId) {

        return ResponseEntity.ok(
                dealService.getParticipants(
                        dealId));
    }

    @Operation(
            summary = "Select Buyer",
            description = "Marks participant as selected buyer"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Buyer selected")
    })
    @PostMapping("/{dealId}/participants/{participantId}/select")
    public ResponseEntity<DealParticipantResponse> selectBuyer(
            @PathVariable UUID dealId,
            @PathVariable UUID participantId) {

        return ResponseEntity.ok(
                dealService.selectBuyer(
                        dealId,
                        participantId));
    }

    @Operation(
            summary = "Update Deal Status",
            description = "Updates current deal status"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated")
    })
    @PutMapping("/{dealId}/status")
    public ResponseEntity<DealResponse> updateDealStatus(
            @PathVariable UUID dealId,
            @RequestBody UpdateDealStatusRequest request) {

        return ResponseEntity.ok(
                dealService.updateDealStatus(
                        dealId,
                        request));
    }

    @Operation(
            summary = "Finalize Deal",
            description = "Finalizes deal and stores final amount"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deal finalized")
    })
    @PostMapping("/{dealId}/finalize")
    public ResponseEntity<DealResponse> finalizeDeal(
            @PathVariable UUID dealId,
            @RequestBody FinalizeDealRequest request) {

        return ResponseEntity.ok(
                dealService.finalizeDeal(
                        dealId,
                        request));
    }

    @Operation(
            summary = "Cancel Deal",
            description = "Cancels an existing deal"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deal cancelled")
    })
    @PostMapping("/{dealId}/cancel")
    public ResponseEntity<DealResponse> cancelDeal(
            @PathVariable UUID dealId,
            @RequestParam String reason) {

        return ResponseEntity.ok(
                dealService.cancelDeal(
                        dealId,
                        reason));
    }

    @Operation(
            summary = "Complete Deal",
            description = "Marks deal as completed"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deal completed")
    })
    @PutMapping("/{dealId}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DealResponse> completeDeal(
            @PathVariable UUID dealId,
            @RequestBody CompleteDealRequest request) {

        return ResponseEntity.ok(
                dealService.completeDeal(
                        dealId,
                        request.getFinalDealAmount()));
    }
}
