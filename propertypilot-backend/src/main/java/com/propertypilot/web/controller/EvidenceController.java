package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateEvidenceRequest;
import com.propertypilot.application.dto.EvidenceResponse;
import com.propertypilot.application.service.EvidenceService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/evidence")
public class EvidenceController {

    private final EvidenceService evidenceService;

    public EvidenceController(
            EvidenceService evidenceService) {

        this.evidenceService = evidenceService;
    }

    @PostMapping
    public EvidenceResponse createEvidence(
            @RequestBody
            CreateEvidenceRequest request) {

        return evidenceService.createEvidence(
                request);
    }

    @GetMapping("/{evidenceId}")
    public EvidenceResponse getEvidence(
            @PathVariable UUID evidenceId) {

        return evidenceService.getEvidence(
                evidenceId);
    }
}