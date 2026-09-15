package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateVisitRequest;
import com.propertypilot.application.dto.VisitResponse;
import com.propertypilot.application.service.VisitService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/visits")
public class VisitController {

    private final VisitService visitService;

    public VisitController(
            VisitService visitService) {

        this.visitService = visitService;
    }

    @PostMapping
    public VisitResponse createVisit(
            @RequestBody
            CreateVisitRequest request) {

        return visitService.createVisit(
                request);
    }

    @GetMapping("/{visitId}")
    public VisitResponse getVisit(
            @PathVariable
            UUID visitId) {

        return visitService.getVisit(
                visitId);
    }

    @PostMapping("/{visitId}/start")
    public VisitResponse startVisit(
            @PathVariable
            UUID visitId) {

        return visitService.startVisit(
                visitId);
    }

    @PostMapping("/{visitId}/complete")
    public VisitResponse completeVisit(
            @PathVariable
            UUID visitId) {

        return visitService.completeVisit(
                visitId);
    }

    @PostMapping("/{visitId}/cancel")
    public VisitResponse cancelVisit(
            @PathVariable
            UUID visitId,
            @RequestParam
            String reason) {

        return visitService.cancelVisit(
                visitId,
                reason);
    }
}