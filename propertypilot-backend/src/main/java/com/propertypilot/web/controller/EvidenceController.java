package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateEvidenceRequest;
import com.propertypilot.application.dto.EvidenceResponse;
import com.propertypilot.application.service.EvidenceService;
import com.propertypilot.application.service.EvidenceStorageService;
import com.propertypilot.application.dto.EvidenceTypeResponse;
import com.propertypilot.application.dto.EvidenceValidationRequest;
import com.propertypilot.application.dto.VisitEvidenceSummaryResponse;
import com.propertypilot.application.dto.EvidenceDashboardResponse;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.io.IOException;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/evidence")
@Tag(
        name = "Evidence Management",
        description =
                "APIs for managing visit evidence uploads and retrieval"
)
public class EvidenceController {

    private final EvidenceService evidenceService;
    private final EvidenceStorageService evidenceStorageService;

    @Operation(summary = "List evidence for a service request")
    @GetMapping("/service-request/{serviceRequestId}")
    public List<EvidenceResponse> getEvidenceForServiceRequest(@PathVariable UUID serviceRequestId) {
        return evidenceService.getEvidenceForServiceRequest(serviceRequestId);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATIONS')")
    public List<EvidenceResponse> getAllEvidenceForOperations() {
        return evidenceService.getAllEvidenceForOperations();
    }

    public EvidenceController(EvidenceService evidenceService, EvidenceStorageService evidenceStorageService) {

        this.evidenceService = evidenceService;
        this.evidenceStorageService = evidenceStorageService;
    }

    @GetMapping("/types")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATIONS','AGENT')")
    public List<EvidenceTypeResponse> getEvidenceTypes() { return evidenceService.getActiveEvidenceTypes(); }

    @GetMapping("/validation")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATIONS')")
    public List<EvidenceResponse> getValidationQueue() { return evidenceService.getValidationQueue(); }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATIONS')")
    public EvidenceDashboardResponse getDashboard() { return evidenceService.getDashboard(); }

    @GetMapping("/visit/{visitId}")
    public List<EvidenceResponse> getEvidenceForVisit(@PathVariable UUID visitId) { return evidenceService.getEvidenceForVisit(visitId); }

    @GetMapping("/visit/{visitId}/summary")
    public VisitEvidenceSummaryResponse getVisitEvidenceSummary(@PathVariable UUID visitId) { return evidenceService.getVisitEvidenceSummary(visitId); }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','OPERATIONS','AGENT')")
    @ResponseStatus(HttpStatus.CREATED)
    public EvidenceResponse uploadEvidence(@RequestParam(required = false) UUID visitId,
                                           @RequestParam(required = false) UUID serviceRequestId,
                                           @RequestParam UUID evidenceTypeId,
                                           @RequestPart MultipartFile file,
                                           @RequestParam(required = false) BigDecimal latitude,
                                           @RequestParam(required = false) BigDecimal longitude,
                                           @RequestParam(required = false) OffsetDateTime capturedAt,
                                           @RequestParam(required = false) String deviceInfo,
                                           @RequestParam(required = false) String remarks) throws IOException {
        if ((visitId == null) == (serviceRequestId == null)) {
            throw new com.propertypilot.web.exception.BusinessException(
                    "Provide exactly one of visitId or serviceRequestId");
        }
        String key = evidenceStorageService.store(file);
        try {
            CreateEvidenceRequest request = new CreateEvidenceRequest();
            request.setVisitId(visitId);
            request.setServiceRequestId(serviceRequestId);
            request.setEvidenceTypeId(evidenceTypeId);
            String fileName = file.getOriginalFilename() == null ? "evidence" : file.getOriginalFilename().replaceAll("[\\r\\n]", "");
            request.setFileUrl("storage://" + key); request.setFileName(fileName.substring(0, Math.min(255, fileName.length())));
            request.setMediaType(file.getContentType() == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : file.getContentType());
            request.setChecksumSha256(sha256(file));
            request.setCapturedAt(capturedAt); request.setLatitude(latitude); request.setLongitude(longitude); request.setDeviceInfo(deviceInfo);
            request.setRemarks(remarks);
            return evidenceService.createEvidence(request);
        } catch (IOException | RuntimeException failure) {
            try { evidenceStorageService.delete(key); } catch (IOException cleanupFailure) { failure.addSuppressed(cleanupFailure); }
            throw failure;
        }
    }

    private String sha256(MultipartFile file) throws IOException {
        try {
            byte[] digest = java.security.MessageDigest.getInstance("SHA-256").digest(file.getBytes());
            return java.util.HexFormat.of().formatHex(digest);
        } catch (java.security.NoSuchAlgorithmException impossible) {
            throw new IllegalStateException("SHA-256 is unavailable", impossible);
        }
    }

    @PostMapping("/{evidenceId}/validation")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATIONS')")
    public EvidenceResponse validateEvidence(@PathVariable UUID evidenceId, @Valid @RequestBody EvidenceValidationRequest request) {
        return evidenceService.validateEvidence(evidenceId, request);
    }

    @Operation(summary = "Direct evidence metadata creation is disabled; upload the actual file instead", deprecated = true)
    @PostMapping
    public EvidenceResponse createEvidence(
            @Valid @RequestBody CreateEvidenceRequest request) {
        throw new com.propertypilot.web.exception.BusinessException(
                "Upload evidence files through /api/evidence/upload");
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

    @GetMapping("/{evidenceId}/file")
    public ResponseEntity<byte[]> getEvidenceFile(@PathVariable UUID evidenceId) throws IOException {
        EvidenceResponse evidence = evidenceService.getEvidence(evidenceId);
        String key = evidenceService.getFileStorageKey(evidenceId);
        MediaType mediaType;
        try { mediaType = MediaType.parseMediaType(evidence.getMediaType()); }
        catch (RuntimeException invalid) { mediaType = MediaType.APPLICATION_OCTET_STREAM; }
        return ResponseEntity.ok().contentType(mediaType).header("X-Content-Type-Options", "nosniff")
                .body(evidenceStorageService.read(key));
    }
}
