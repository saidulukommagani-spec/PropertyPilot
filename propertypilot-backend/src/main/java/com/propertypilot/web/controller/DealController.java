package com.propertypilot.web.controller;

import com.propertypilot.application.dto.*;
import com.propertypilot.application.service.DealService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deals")
public class DealController {

    private final DealService dealService;

    public DealController(
            DealService dealService) {

        this.dealService = dealService;
    }

    @PostMapping
    public ResponseEntity<DealResponse>
    createDeal(
            @RequestBody
            CreateDealRequest request) {

        return ResponseEntity.ok(
                dealService.createDeal(
                        request));
    }

    @GetMapping("/{dealId}")
    public ResponseEntity<DealResponse>
    getDeal(
            @PathVariable UUID dealId) {

        return ResponseEntity.ok(
                dealService.getDeal(
                        dealId));
    }

    @GetMapping
    public ResponseEntity<List<DealResponse>>
    getAllDeals() {

        return ResponseEntity.ok(
                dealService.getAllDeals());
    }

    @PostMapping("/{dealId}/participants")
    public ResponseEntity<DealParticipantResponse>
    addParticipant(
            @PathVariable UUID dealId,
            @RequestBody
            AddDealParticipantRequest request) {

        return ResponseEntity.ok(
                dealService.addParticipant(
                        dealId,
                        request));
    }

    @GetMapping("/{dealId}/participants")
    public ResponseEntity<
            List<DealParticipantResponse>>
    getParticipants(
            @PathVariable UUID dealId) {

        return ResponseEntity.ok(
                dealService.getParticipants(
                        dealId));
    }

    @PostMapping(
            "/{dealId}/participants/{participantId}/select")
    public ResponseEntity<
            DealParticipantResponse>
    selectBuyer(
            @PathVariable UUID dealId,
            @PathVariable UUID participantId) {

        return ResponseEntity.ok(
                dealService.selectBuyer(
                        dealId,
                        participantId));
    }

    @PutMapping("/{dealId}/status")
    public ResponseEntity<DealResponse>
    updateDealStatus(
            @PathVariable UUID dealId,
            @RequestBody
            UpdateDealStatusRequest request) {

        return ResponseEntity.ok(
                dealService.updateDealStatus(
                        dealId,
                        request));
    }

    @PostMapping("/{dealId}/finalize")
    public ResponseEntity<DealResponse>
    finalizeDeal(
            @PathVariable UUID dealId,
            @RequestBody
            FinalizeDealRequest request) {

        return ResponseEntity.ok(
                dealService.finalizeDeal(
                        dealId,
                        request));
    }

    @PostMapping("/{dealId}/cancel")
    public ResponseEntity<DealResponse>
    cancelDeal(
            @PathVariable UUID dealId,
            @RequestParam String reason) {

        return ResponseEntity.ok(
                dealService.cancelDeal(
                        dealId,
                        reason));
    }
    @PutMapping("/{dealId}/complete")
@PreAuthorize(
        "hasAuthority('ADMIN')")
public ResponseEntity<DealResponse>
completeDeal(
        @PathVariable UUID dealId,
        @RequestBody CompleteDealRequest request) {

    return ResponseEntity.ok(
            dealService.completeDeal(
                    dealId,
                    request.getFinalDealAmount()));
}
}