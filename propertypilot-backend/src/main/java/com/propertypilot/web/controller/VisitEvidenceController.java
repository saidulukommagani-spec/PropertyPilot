package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateVisitEvidenceRequest;
import com.propertypilot.application.dto.VisitEvidenceResponse;
import com.propertypilot.application.service.VisitEvidenceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/visit-evidence")
public class VisitEvidenceController {

    private final VisitEvidenceService
            visitEvidenceService;

    public VisitEvidenceController(
            VisitEvidenceService visitEvidenceService) {

        this.visitEvidenceService =
                visitEvidenceService;
    }

    @PostMapping
    public VisitEvidenceResponse uploadEvidence(
            @RequestBody
            CreateVisitEvidenceRequest request) {

        return visitEvidenceService
                .uploadEvidence(request);
    }

    @GetMapping("/{visitId}")
    public List<VisitEvidenceResponse>
    getVisitEvidence(
            @PathVariable UUID visitId) {

        return visitEvidenceService
                .getVisitEvidence(visitId);
    }
}