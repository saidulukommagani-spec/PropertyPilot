package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateVisitRequest;
import com.propertypilot.application.dto.VisitResponse;
import com.propertypilot.application.service.VisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/visits")
@RequiredArgsConstructor
@Tag(
        name = "Visit Management",
        description = "Manage agent visits")
public class VisitController {

    private final VisitService visitService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create Visit",
            description =
                    "Creates a new visit for an assignment")
    public VisitResponse createVisit(
            @Valid
            @RequestBody
            CreateVisitRequest request) {

        return visitService.createVisit(
                request);
    }

    @GetMapping("/{visitId}")
    @Operation(
            summary = "Get Visit",
            description =
                    "Retrieve visit details")
    public VisitResponse getVisit(
            @PathVariable
            UUID visitId) {

        return visitService.getVisit(
                visitId);
    }

    @PostMapping("/{visitId}/start")
    @Operation(
            summary = "Start Visit",
            description =
                    "Mark visit as in progress")
    public VisitResponse startVisit(
            @PathVariable
            UUID visitId) {

        return visitService.startVisit(
                visitId);
    }

    @PostMapping("/{visitId}/complete")
    @Operation(
            summary = "Complete Visit",
            description =
                    "Mark visit as completed")
    public VisitResponse completeVisit(
            @PathVariable
            UUID visitId) {

        return visitService.completeVisit(
                visitId);
    }

    @PostMapping("/{visitId}/cancel")
    @Operation(
            summary = "Cancel Visit",
            description =
                    "Cancel an existing visit")
    public VisitResponse cancelVisit(
            @PathVariable
            UUID visitId,
            @RequestParam
            @NotBlank(
                    message =
                            "Cancellation reason is required")
            String reason) {

        return visitService.cancelVisit(
                visitId,
                reason);
    }
}