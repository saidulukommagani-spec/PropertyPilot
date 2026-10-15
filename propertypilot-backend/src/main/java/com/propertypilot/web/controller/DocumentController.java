package com.propertypilot.web.controller;

import com.propertypilot.application.dto.AdminServiceRequestSummaryResponse;
import com.propertypilot.application.dto.AdminAssignServiceRequestRequest;
import com.propertypilot.application.dto.AdminDashboardResponse;
import com.propertypilot.application.dto.AdminServiceRequestDetailsResponse;
import com.propertypilot.application.dto.AdminServiceRequestStatusUpdateRequest;
import com.propertypilot.application.dto.DocumentChecklistResponse;
import com.propertypilot.application.dto.DocumentCreateRequest;
import com.propertypilot.application.dto.DocumentResponse;
import com.propertypilot.application.dto.DocumentVersionCreateRequest;
import com.propertypilot.application.dto.DocumentVersionResponse;
import com.propertypilot.application.dto.ServiceRequestDetailsResponse;
import com.propertypilot.application.dto.ServiceRequestSummaryResponse;
import com.propertypilot.application.service.DocumentService;
import com.propertypilot.application.service.PropertyDocumentStorageService;
import org.springframework.beans.factory.ObjectProvider;
import com.propertypilot.application.dto.DocumentProcurementRequest;
import com.propertypilot.application.dto.DocumentProcurementResponse;
import com.propertypilot.application.dto.DocumentProcurementStatusHistoryResponse;
import com.propertypilot.application.dto.DocumentProcurementStatusUpdateRequest;
import com.propertypilot.application.dto.DocumentProcurementDetailsResponse;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ContentDisposition;
import org.springframework.web.bind.annotation.*;
import com.propertypilot.application.dto.DocumentProcurementRequestSummaryResponse;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class DocumentController {

    private final DocumentService documentService;
    private final ObjectProvider<PropertyDocumentStorageService> documentStorageProvider;

    public DocumentController(
            DocumentService documentService,
            ObjectProvider<PropertyDocumentStorageService> documentStorageProvider) {
        this.documentService = documentService;
        this.documentStorageProvider = documentStorageProvider;
    }

    @PostMapping(value = "/properties/{propertyId}/documents/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentResponse uploadPropertyDocument(
            @PathVariable UUID propertyId,
            @RequestParam String documentName,
            @RequestParam String documentType,
            @RequestPart MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("Choose a document to upload");
        String originalFileName = file.getOriginalFilename() == null ? "Property document" : file.getOriginalFilename().replace('\\', '/');
        String fileName = originalFileName.substring(originalFileName.lastIndexOf('/') + 1).replaceAll("[\\r\\n\\p{Cntrl}]", "").trim();
        if (fileName.isBlank()) fileName = "Property document";
        fileName = fileName.substring(0, Math.min(255, fileName.length()));
        String displayName = documentName == null || documentName.isBlank() ? fileName : documentName.trim();
        displayName = displayName.substring(0, Math.min(255, displayName.length()));
        PropertyDocumentStorageService documentStorage = documentStorageProvider.getObject();
        String key = documentStorage.store(file);
        try {
            return documentService.createPropertyDocumentWithFile(propertyId, displayName, documentType, fileName, key, file.getContentType(), file.getSize());
        } catch (RuntimeException failure) {
            try { documentStorage.delete(key); } catch (IOException cleanupFailure) { failure.addSuppressed(cleanupFailure); }
            throw failure;
        }
    }

    @GetMapping("/documents/{documentId}/file")
    public ResponseEntity<byte[]> getCurrentDocumentFile(@PathVariable UUID documentId) throws IOException {
        DocumentResponse document = documentService.getDocument(documentId);
        if (!Boolean.TRUE.equals(document.getHasFile())) throw new com.propertypilot.web.exception.ResourceNotFoundException("No file is attached to this document");
        DocumentVersionResponse version = documentService.getDocumentVersions(documentId).stream()
                .filter(item -> Boolean.TRUE.equals(item.getCurrentVersion())).findFirst()
                .orElseThrow(() -> new com.propertypilot.web.exception.ResourceNotFoundException("No current file is attached to this document"));
        String key = documentService.getCurrentDocumentStorageKey(documentId);
        MediaType mediaType;
        try { mediaType = MediaType.parseMediaType(version.getMimeType()); } catch (RuntimeException invalid) { mediaType = MediaType.APPLICATION_OCTET_STREAM; }
        ContentDisposition disposition = ContentDisposition.inline().filename(version.getFileName(), StandardCharsets.UTF_8).build();
        return ResponseEntity.ok().contentType(mediaType).header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .header("X-Content-Type-Options", "nosniff").body(documentStorageProvider.getObject().read(key));
    }

    @PostMapping("/properties/{propertyId}/documents")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentResponse createDocument(
            @PathVariable UUID propertyId,
            @Valid @RequestBody
            DocumentCreateRequest request) {

        return documentService.createDocument(
                propertyId,
                request);
    }

    @GetMapping("/properties/{propertyId}/documents")
    public List<DocumentResponse> getDocumentsByProperty(
            @PathVariable UUID propertyId) {

        return documentService.getDocumentsByProperty(
                propertyId);
    }

    @GetMapping("/documents/{documentId}")
    public DocumentResponse getDocument(
            @PathVariable UUID documentId) {

        return documentService.getDocument(
                documentId);
    }

    @PostMapping("/documents/{documentId}/versions")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentVersionResponse createDocumentVersion(
            @PathVariable UUID documentId,
            @Valid @RequestBody
            DocumentVersionCreateRequest request) {

        return documentService.createDocumentVersion(
                documentId,
                request);
    }

    @GetMapping("/documents/{documentId}/versions")
    public List<DocumentVersionResponse> getDocumentVersions(
            @PathVariable UUID documentId) {

        return documentService.getDocumentVersions(
                documentId);
    }

    @PatchMapping("/documents/{documentId}/archive")
    public DocumentResponse archiveDocument(
            @PathVariable UUID documentId) {

        return documentService.archiveDocument(
                documentId);
    }

    @PatchMapping("/documents/{documentId}/restore")
    public DocumentResponse restoreDocument(
            @PathVariable UUID documentId) {

        return documentService.restoreDocument(
                documentId);
    }
    @GetMapping("/properties/{propertyId}/document-checklist")
    public DocumentChecklistResponse getDocumentChecklist(
            @PathVariable UUID propertyId) {

        return documentService.getDocumentChecklist(propertyId);
    }
    @PostMapping("/properties/{propertyId}/document-procurement")
@ResponseStatus(HttpStatus.CREATED)
public DocumentProcurementResponse requestDocumentProcurement(
        @PathVariable UUID propertyId,
        @RequestBody DocumentProcurementRequest request) {

    return documentService.requestDocumentProcurement(
            propertyId,
            request);
}
@GetMapping(
        "/properties/{propertyId}/document-procurement-requests")
public List<DocumentProcurementRequestSummaryResponse>
getDocumentProcurementRequests(
        @PathVariable UUID propertyId) {

    return documentService
            .getDocumentProcurementRequests(
                    propertyId);
}
@PatchMapping(
    "/document-procurement-requests/{serviceRequestId}/status")
public DocumentProcurementResponse updateStatus(
        @PathVariable UUID serviceRequestId,
        @RequestBody
        DocumentProcurementStatusUpdateRequest request) {

    return documentService
            .updateDocumentProcurementStatus(
                    serviceRequestId,
                    request);
}
@GetMapping(
        "/document-procurement-requests/{serviceRequestId}")
public DocumentProcurementDetailsResponse
getDocumentProcurementRequest(
        @PathVariable UUID serviceRequestId) {

    return documentService
            .getDocumentProcurementRequest(
                    serviceRequestId);
}
@GetMapping(
        "/document-procurement-requests/{serviceRequestId}/history")
public List<DocumentProcurementStatusHistoryResponse>
getDocumentProcurementHistory(
        @PathVariable UUID serviceRequestId) {

    return documentService
            .getDocumentProcurementHistory(
                    serviceRequestId);
}
@GetMapping("/service-requests")
public List<ServiceRequestSummaryResponse>
getMyServiceRequests() {

    return documentService
            .getMyServiceRequests();
}
@GetMapping("/service-requests/{serviceRequestId}")
public ServiceRequestDetailsResponse
getServiceRequest(
        @PathVariable UUID serviceRequestId) {

    return documentService
            .getServiceRequest(serviceRequestId);
}
@GetMapping("/admin/service-requests")
public List<AdminServiceRequestSummaryResponse>
getAllServiceRequestsForAdmin() {

    return documentService
            .getAllServiceRequestsForAdmin();
}
@GetMapping(
        "/admin/service-requests/{serviceRequestId}")
public AdminServiceRequestDetailsResponse
getAdminServiceRequest(
        @PathVariable UUID serviceRequestId) {

    return documentService
            .getAdminServiceRequest(
                    serviceRequestId);
}
@PatchMapping(
        "/admin/service-requests/{serviceRequestId}/status")
public AdminServiceRequestDetailsResponse
updateAdminServiceRequestStatus(
        @PathVariable UUID serviceRequestId,
        @RequestBody
        AdminServiceRequestStatusUpdateRequest request) {

    return documentService
            .updateAdminServiceRequestStatus(
                    serviceRequestId,
                    request);
}
@GetMapping("/admin/dashboard")
public AdminDashboardResponse
getAdminDashboard() {

    return documentService
            .getAdminDashboard();
}

@PatchMapping(
        "/service-requests/{serviceRequestId}/assign")
public ResponseEntity<
        AdminServiceRequestDetailsResponse>
assignServiceRequest(
        @PathVariable UUID serviceRequestId,
        @RequestBody
        AdminAssignServiceRequestRequest request) {

    return ResponseEntity.ok(
            documentService.assignServiceRequest(
                    serviceRequestId,
                    request));
}

}
