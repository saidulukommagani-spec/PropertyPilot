package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateEvidenceRequest;
import com.propertypilot.application.dto.EvidenceResponse;
import com.propertypilot.application.service.EvidenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/evidence")
@Tag(
        name = "Evidence Management",
        description =
                "APIs for managing visit evidence uploads and retrieval"
)
public class EvidenceController {

    private final EvidenceService evidenceService;

    public EvidenceController(
            EvidenceService evidenceService) {

        this.evidenceService = evidenceService;
    }

    @Operation(
            summary = "Create evidence",
            description =
                    "Creates evidence for a visit using evidence type and uploaded file information"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Evidence created successfully",
                    content = @Content(
                            schema =
                            @Schema(
                                    implementation =
                                            EvidenceResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Visit or Evidence Type not found"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EvidenceResponse createEvidence(
            @Valid
            @RequestBody
            CreateEvidenceRequest request) {

        return evidenceService.createEvidence(
                request);
    }

    @Operation(
            summary = "Get evidence by ID",
            description =
                    "Retrieves evidence details using evidence ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Evidence retrieved successfully",
                    content = @Content(
                            schema =
                            @Schema(
                                    implementation =
                                            EvidenceResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Evidence not found"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error")
    })
    @GetMapping("/{evidenceId}")
    public EvidenceResponse getEvidence(
            @Parameter(
                    description = "Evidence ID",
                    required = true)
            @PathVariable
            UUID evidenceId) {

        return evidenceService.getEvidence(
                evidenceId);
    }
}