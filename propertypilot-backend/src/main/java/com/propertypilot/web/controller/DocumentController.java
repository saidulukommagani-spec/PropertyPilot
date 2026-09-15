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
import com.propertypilot.application.dto.DocumentProcurementRequest;
import com.propertypilot.application.dto.DocumentProcurementResponse;
import com.propertypilot.application.dto.DocumentProcurementStatusHistoryResponse;
import com.propertypilot.application.dto.DocumentProcurementStatusUpdateRequest;
import com.propertypilot.application.dto.DocumentProcurementDetailsResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.propertypilot.application.dto.DocumentProcurementRequestSummaryResponse;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

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
