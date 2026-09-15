package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.ServiceRequestStatusHistoryResponse;
import com.propertypilot.application.dto.AdminServiceRequestSummaryResponse;
import com.propertypilot.application.dto.AdminAssignServiceRequestRequest;
import com.propertypilot.application.dto.AdminDashboardResponse;
import com.propertypilot.application.dto.AdminServiceRequestDetailsResponse;
import com.propertypilot.application.dto.AdminServiceRequestStatusUpdateRequest;
import com.propertypilot.application.dto.DocumentChecklistItemResponse;
import com.propertypilot.application.dto.DocumentChecklistResponse;
import com.propertypilot.application.dto.DocumentCreateRequest;
import com.propertypilot.application.dto.DocumentProcurementDetailsResponse;
import com.propertypilot.application.dto.DocumentProcurementRequest;
import com.propertypilot.application.dto.DocumentProcurementResponse;
import com.propertypilot.application.dto.DocumentProcurementStatusHistoryResponse;
import com.propertypilot.application.dto.DocumentProcurementStatusUpdateRequest;
import com.propertypilot.application.dto.DocumentResponse;
import com.propertypilot.application.dto.DocumentVersionCreateRequest;
import com.propertypilot.application.dto.DocumentVersionResponse;
import com.propertypilot.application.dto.ServiceRequestDetailsResponse;

import com.propertypilot.application.service.DocumentService;
import com.propertypilot.infrastructure.persistence.entity.Document;
import com.propertypilot.infrastructure.persistence.entity.DocumentServiceCatalog;
import com.propertypilot.infrastructure.persistence.entity.DocumentVersion;
import com.propertypilot.infrastructure.persistence.entity.Property;
import com.propertypilot.infrastructure.persistence.entity.ServiceAssignmentEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.repository.AgentRepository;
import com.propertypilot.infrastructure.persistence.repository.DocumentRepository;
import com.propertypilot.infrastructure.persistence.repository.DocumentTypeRepository;
import com.propertypilot.infrastructure.persistence.repository.DocumentVersionRepository;
import com.propertypilot.infrastructure.persistence.repository.PropertyRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceAssignmentRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRepository;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.propertypilot.infrastructure.persistence.repository.DocumentServiceCatalogRepository;

import com.propertypilot.application.dto.DocumentProcurementRequestSummaryResponse;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestStatusHistoryEntity;
import com.propertypilot.infrastructure.persistence.entity.UserEntity;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestStatusHistoryRepository;
import com.propertypilot.application.dto.ServiceRequestSummaryResponse;
import com.propertypilot.infrastructure.persistence.entity.AgentEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@org.springframework.transaction.annotation.Transactional
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl
        implements DocumentService {

    private final DocumentRepository documentRepository;

    private final DocumentTypeRepository documentTypeRepository;

    private final DocumentVersionRepository documentVersionRepository;

    private final PropertyRepository propertyRepository;

    private final SecurityService securityService;
    
    private final DocumentServiceCatalogRepository documentServiceCatalogRepository;
   
    private final ServiceRequestRepository serviceRequestRepository;

private final ServiceRequestStatusHistoryRepository serviceRequestStatusHistoryRepository;

private final ServiceAssignmentRepository serviceAssignmentRepository;

private final AgentRepository agentRepository;

private final UserRepository userRepository;
        
    @Override
    public DocumentResponse createDocument(
            UUID propertyId,
            DocumentCreateRequest request) {

        Property property =
                propertyRepository.findById(propertyId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Property not found"));

        securityService.validatePropertyOwnership(
                property);
                validateDocumentTypeForProperty(
        property,
        request.getDocumentType());
        if ("ARCHIVED".equalsIgnoreCase(
                property.getStatus())) {

            throw new IllegalStateException(
                    "Cannot add documents to archived property");
        }

        Document document =
                new Document();

        document.setProperty(
                property);

        document.setDocumentName(
                request.getDocumentName());

        document.setDocumentType(
                request.getDocumentType());

        document.setStatus(
                "ACTIVE");

        Document savedDocument =
                documentRepository.save(
                        document);

        return buildDocumentResponse(
                savedDocument);
    }

    @Override
    public List<DocumentResponse> getDocumentsByProperty(
            UUID propertyId) {

        Property property =
                propertyRepository.findById(propertyId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Property not found"));

        securityService.validatePropertyOwnership(
                property);

        return documentRepository
                .findByProperty_PropertyId(
                        propertyId)
                .stream()
                .map(this::buildDocumentResponse)
                .toList();
    }

    @Override
    public DocumentChecklistResponse getDocumentChecklist(UUID propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        securityService.validatePropertyOwnership(property);

       String category =
        mapPropertyCategory(
                property.getPropertyType());
        Set<String> uploadedTypes = documentRepository.findByProperty_PropertyId(propertyId)
                .stream()
                .filter(document -> "ACTIVE".equalsIgnoreCase(document.getStatus()))
                .filter(document -> documentVersionRepository
                        .findByDocument_DocumentIdAndCurrentVersionTrue(document.getDocumentId())
                        .isPresent())
                .map(Document::getDocumentType)
                .collect(Collectors.toSet());

        List<DocumentChecklistItemResponse> items = documentTypeRepository
                .findByPropertyCategoryInAndActiveTrue(List.of("COMMON", category))
                .stream()
                .map(type -> {

    boolean uploaded =
            uploadedTypes.contains(
                    type.getDocumentCode());

    boolean canBeProcured =
            documentServiceCatalogRepository
                    .findByDocumentCode(
                            type.getDocumentCode())
                    .map(service ->
                            Boolean.TRUE.equals(
                                    service.getServiceAvailable()) && Boolean.TRUE.equals(service.getActive()))
                    .orElse(false);

    DocumentChecklistItemResponse item =
            new DocumentChecklistItemResponse();

    item.setDocumentCode(
            type.getDocumentCode());

    item.setDocumentName(
            type.getDocumentName());

    item.setMandatory(
            type.getMandatory());

    item.setUploaded(
            uploaded);

    item.setDocumentStatus(
            uploaded
                    ? "UPLOADED"
                    : "MISSING");

    item.setAvailableWithOwner(
            false);

    item.setCanBeProcuredByPropertyPilot(
            canBeProcured);

    return item;
})
                .toList();

        int uploaded = (int) items.stream().filter(DocumentChecklistItemResponse::getUploaded).count();
        DocumentChecklistResponse response = new DocumentChecklistResponse();
        response.setPropertyId(propertyId);
        response.setPropertyCategory(category);
        response.setTotalDocuments(items.size());
        response.setUploadedDocuments(uploaded);
        response.setCompletionPercentage(items.isEmpty() ? 0 : uploaded * 100 / items.size());
        response.setDocuments(items);
        return response;
    }
    private String mapPropertyCategory(
        String propertyType) {

    if (propertyType == null) {
        return "COMMON";
    }

    return switch (
            propertyType.trim()
                    .toUpperCase(Locale.ROOT)) {

        case "APARTMENT",
             "FLAT" -> "FLAT";

        case "HOUSE",
             "INDEPENDENT HOUSE" -> "HOUSE";

        case "PLOT",
             "RESIDENTIAL PLOT" -> "PLOT";

        case "COMMERCIAL",
             "COMMERCIAL BUILDING" -> "COMMERCIAL";

        case "AGRICULTURAL",
             "AGRICULTURAL LAND" -> "AGRICULTURAL";

        default -> "COMMON";
    };
}

    @Override
    public DocumentResponse getDocument(
            UUID documentId) {

        Document document =
                documentRepository.findById(
                                documentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Document not found"));

        securityService.validateDocumentOwnership(
                document);

        return buildDocumentResponse(
                document);
    }

    @Override
    public List<DocumentVersionResponse> getDocumentVersions(
            UUID documentId) {

        Document document =
                documentRepository.findById(
                                documentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Document not found"));

        securityService.validateDocumentOwnership(
                document);

        return documentVersionRepository
                .findByDocument_DocumentIdOrderByVersionNumberDesc(
                        documentId)
                .stream()
                .map(this::buildDocumentVersionResponse)
                .toList();
    }

    @Override
    public DocumentVersionResponse createDocumentVersion(
            UUID documentId,
            DocumentVersionCreateRequest request) {

        Document document =
                documentRepository.findById(
                                documentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Document not found"));

        securityService.validateDocumentOwnership(
                document);

        if ("ARCHIVED".equalsIgnoreCase(
                document.getStatus()) || "ARCHIVED".equalsIgnoreCase(document.getProperty().getStatus())) {

            throw new IllegalStateException(
                    "Cannot upload version to archived document");
        }

        documentVersionRepository
                .findByDocument_DocumentIdAndCurrentVersionTrue(
                        documentId)
                .ifPresent(existingVersion -> {

                    existingVersion.setCurrentVersion(
                            false);

                    documentVersionRepository.save(
                            existingVersion);
                });

        int nextVersionNumber =
                documentVersionRepository
                        .findByDocument_DocumentIdOrderByVersionNumberDesc(
                                documentId)
                        .stream()
                        .findFirst()
                        .map(version ->
                                version.getVersionNumber() + 1)
                        .orElse(1);

        DocumentVersion version =
                new DocumentVersion();

        version.setDocument(
                document);

        version.setVersionNumber(
                nextVersionNumber);

        version.setFileName(
                request.getFileName());

        version.setFilePath(
                request.getFilePath());

        version.setMimeType(
                request.getMimeType());

        version.setFileSize(
                request.getFileSize());

        version.setCurrentVersion(
                true);

        DocumentVersion savedVersion =
                documentVersionRepository.save(
                        version);

        return buildDocumentVersionResponse(
                savedVersion);
    }

    @Override
    public DocumentResponse archiveDocument(
            UUID documentId) {

        Document document =
                documentRepository.findById(
                                documentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Document not found"));

        securityService.validateDocumentOwnership(
                document);

        if ("ARCHIVED".equalsIgnoreCase(
                document.getStatus())) {

            throw new IllegalStateException(
                    "Document is already archived");
        }

        document.setStatus(
                "ARCHIVED");

        Document savedDocument =
                documentRepository.save(
                        document);

        return buildDocumentResponse(
                savedDocument);
    }

    @Override
    public DocumentResponse restoreDocument(
            UUID documentId) {

        Document document =
                documentRepository.findById(
                                documentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Document not found"));

        securityService.validateDocumentOwnership(
                document);

        if (!"ARCHIVED".equalsIgnoreCase(
                document.getStatus())) {

            throw new IllegalStateException(
                    "Only archived documents can be restored");
        }

        document.setStatus(
                "ACTIVE");

        Document savedDocument =
                documentRepository.save(
                        document);

        return buildDocumentResponse(
                savedDocument);
    }
    private void validateDocumentTypeForProperty(
        Property property,
        String documentType) {

    String category =
            mapPropertyCategory(
                    property.getPropertyType());

    var type =
            documentTypeRepository
                    .findByDocumentCodeAndActiveTrue(
                            documentType)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Invalid document type: "
                                            + documentType));

    boolean allowed =
            "COMMON".equalsIgnoreCase(
                    type.getPropertyCategory())
                    ||
                    category.equalsIgnoreCase(
                            type.getPropertyCategory());

    if (!allowed) {

        throw new IllegalArgumentException(
                "Document type "
                        + documentType
                        + " is not valid for property category "
                        + category);
    }
}
    private DocumentResponse buildDocumentResponse(
            Document document) {

        DocumentResponse response =
                new DocumentResponse();

        response.setDocumentId(
                document.getDocumentId());

        response.setPropertyId(
                document.getProperty()
                        .getPropertyId());

        response.setDocumentName(
                document.getDocumentName());

        response.setDocumentType(
                document.getDocumentType());

        response.setStatus(
                document.getStatus());

        return response;
    }

    private DocumentVersionResponse buildDocumentVersionResponse(
            DocumentVersion version) {

        DocumentVersionResponse response =
                new DocumentVersionResponse();

        response.setVersionId(
                version.getVersionId());

        response.setDocumentId(
                version.getDocument()
                        .getDocumentId());

        response.setVersionNumber(
                version.getVersionNumber());

        response.setFileName(
                version.getFileName());

        response.setMimeType(
                version.getMimeType());

        response.setFileSize(
                version.getFileSize());

        response.setCurrentVersion(
                version.getCurrentVersion());

        return response;
    }
    @Override
public DocumentProcurementResponse requestDocumentProcurement(
        UUID propertyId,
        DocumentProcurementRequest request) {

    Property property =
            propertyRepository.findById(propertyId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Property not found"));

    securityService.validatePropertyOwnership(
            property);

    if ("ARCHIVED".equalsIgnoreCase(property.getStatus())) {
        throw new IllegalStateException("Cannot procure documents for archived property");
    }
    if (request.getDocumentCode() == null || request.getDocumentCode().isBlank()) {
        throw new IllegalArgumentException("Document code is required");
    }
    String documentCode =
            request.getDocumentCode()
                    .trim()
                    .toUpperCase(Locale.ROOT);

    validateDocumentTypeForProperty(property, documentCode);
    DocumentServiceCatalog catalog =
            documentServiceCatalogRepository
                    .findByDocumentCode(documentCode)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "PropertyPilot cannot procure document: "
                                            + documentCode));

    if (!Boolean.TRUE.equals(
            catalog.getServiceAvailable()) || !Boolean.TRUE.equals(catalog.getActive())) {

        throw new IllegalArgumentException(
                "Document procurement service not available for "
                        + documentCode);
    }

    ServiceRequestEntity serviceRequest =
            new ServiceRequestEntity();

    serviceRequest.setCustomer(
            property.getCustomer());

    serviceRequest.setProperty(
            property);

    serviceRequest.setRequestType(
            "DOCUMENT_PROCUREMENT");

    serviceRequest.setPriority(
            "MEDIUM");

    serviceRequest.setStatus(
            "NEW");
            serviceRequest.setRequestedAt(
        Instant.now());

    serviceRequest.setDescription(
            "Document Code: "
                    + documentCode
                    + (request.getRemarks() != null
                    ? " | Remarks: " + request.getRemarks()
                    : ""));

    ServiceRequestEntity savedRequest =
            serviceRequestRepository.save(
                    serviceRequest);
        createStatusHistory(
        savedRequest,
        null,
        "NEW",
        "Document procurement request created");
    DocumentProcurementResponse response =
            new DocumentProcurementResponse();

    response.setServiceRequestId(
            savedRequest.getServiceRequestId());

    response.setDocumentCode(
            documentCode);

    response.setStatus(
            savedRequest.getStatus());

    response.setMessage(
            "Document procurement request created successfully");

    return response;
}

@Override
public ServiceRequestDetailsResponse
getServiceRequest(
        UUID serviceRequestId) {

    ServiceRequestEntity serviceRequest =
            serviceRequestRepository
                    .findByIdWithProperty(
                            serviceRequestId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Service request not found"));

    securityService.validatePropertyOwnership(
            serviceRequest.getProperty());

    ServiceRequestDetailsResponse response =
            new ServiceRequestDetailsResponse();

    response.setServiceRequestId(
            serviceRequest.getServiceRequestId());

    response.setPropertyId(
            serviceRequest.getProperty()
                    .getPropertyId());

    response.setPropertyTitle(
            serviceRequest.getProperty()
                    .getTitle());

    response.setRequestType(
            serviceRequest.getRequestType());

    response.setStatus(
            serviceRequest.getStatus());

    response.setPriority(
            serviceRequest.getPriority());

    response.setAmount(
            serviceRequest.getAmount());

    response.setDescription(
            serviceRequest.getDescription());

    response.setRequestedAt(
            serviceRequest.getRequestedAt());

    response.setScheduledAt(
            serviceRequest.getScheduledAt());

    response.setCompletedAt(
            serviceRequest.getCompletedAt());

    response.setCancellationReasonCode(
            serviceRequest.getCancellationReasonCode());
            List<ServiceRequestStatusHistoryResponse>
        historyResponses =
        serviceRequestStatusHistoryRepository
                .findByServiceRequest_ServiceRequestIdOrderByChangedAtDesc(
                        serviceRequestId)
                .stream()
                .map(history -> {

                    ServiceRequestStatusHistoryResponse dto =
                            new ServiceRequestStatusHistoryResponse();

                    dto.setPreviousStatus(
                            history.getPreviousStatus());

                    dto.setNewStatus(
                            history.getNewStatus());

                    dto.setChangeReason(
                            history.getChangeReason());

                    dto.setChangedAt(
                            history.getChangedAt());

                    return dto;
                })
                .toList();

response.setStatusHistory(
        historyResponses);

    return response;
}

@Override
public List<DocumentProcurementRequestSummaryResponse>
getDocumentProcurementRequests(
        UUID propertyId) {

    Property property =
            propertyRepository.findById(propertyId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Property not found"));

    securityService.validatePropertyOwnership(
            property);

    return serviceRequestRepository
            .findByProperty_PropertyId(propertyId)
            .stream()
            .filter(request ->
                    "DOCUMENT_PROCUREMENT"
                            .equalsIgnoreCase(
                                    request.getRequestType()))
            .map(request -> {

                DocumentProcurementRequestSummaryResponse response =
                        new DocumentProcurementRequestSummaryResponse();

                response.setServiceRequestId(
                        request.getServiceRequestId());

                response.setStatus(
                        request.getStatus());

                response.setRequestedAt(
                        request.getRequestedAt());

                response.setRemarks(
                        request.getDescription());

                return response;
            })
            .toList();
}
@Override
public DocumentProcurementResponse updateDocumentProcurementStatus(
        UUID serviceRequestId,
        DocumentProcurementStatusUpdateRequest request) {

    ServiceRequestEntity serviceRequest =
            serviceRequestRepository.findById(serviceRequestId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Service request not found"));

    if (!securityService.isAdmin()) {
        throw new org.springframework.security.access.AccessDeniedException("Only administrators can update procurement status");
    }
    if (!"DOCUMENT_PROCUREMENT".equals(serviceRequest.getRequestType())) {
        throw new IllegalArgumentException("Not a document procurement request");
    }
    if (request.getStatus() == null || request.getStatus().isBlank()) {
        throw new IllegalArgumentException("Status is required");
    }
    request.setStatus(request.getStatus().trim().toUpperCase(Locale.ROOT));
    validateTransition(serviceRequest.getStatus(), request.getStatus());

String previousStatus =
        serviceRequest.getStatus();

String newStatus =
        request.getStatus();

serviceRequest.setStatus(
        newStatus);

if ("COMPLETED".equalsIgnoreCase(
        request.getStatus())) {

    serviceRequest.setCompletedAt(
            Instant.now());
}

if ("CANCELLED".equalsIgnoreCase(
        request.getStatus())) {

    if (request.getCancellationReasonCode() == null
            || request.getCancellationReasonCode().isBlank()) {

        throw new IllegalArgumentException(
                "Cancellation reason is mandatory");
    }

    serviceRequest.setCancellationReasonCode(
            request.getCancellationReasonCode());
}

serviceRequestRepository.save(
        serviceRequest);
        createStatusHistory(
        serviceRequest,
        previousStatus,
        newStatus,
        request.getReason());
    DocumentProcurementResponse response =
            new DocumentProcurementResponse();

    response.setServiceRequestId(
            serviceRequest.getServiceRequestId());

    response.setDocumentCode(
            extractDocumentCode(
                    serviceRequest.getDescription()));

    response.setStatus(
            serviceRequest.getStatus());

    response.setMessage(
            "Status updated successfully");

    return response;
}

private String extractDocumentCode(String description) {
        if (description == null || description.isBlank()) {
                return null;
        }

        String prefix = "Document Code:";
        int prefixIndex = description.indexOf(prefix);
        if (prefixIndex < 0) {
                return null;
        }

        String code = description.substring(prefixIndex + prefix.length());
        int remarksIndex = code.indexOf(" | Remarks:");
        if (remarksIndex >= 0) {
                code = code.substring(0, remarksIndex);
        }

        return code.trim();
}
private void validateTransition(
        String currentStatus,
        String newStatus) {

    switch (currentStatus.toUpperCase(Locale.ROOT)) {

        case "NEW" ->
                allowTransition(
                        newStatus,
                        "PENDING_PAYMENT",
                        "CANCELLED");

        case "PENDING_PAYMENT" ->
                allowTransition(
                        newStatus,
                        "PAYMENT_COMPLETED",
                        "CANCELLED");

        case "PAYMENT_COMPLETED" ->
                allowTransition(
                        newStatus,
                        "PENDING_ASSIGNMENT",
                        "CANCELLED");

        case "PENDING_ASSIGNMENT" ->
                allowTransition(
                        newStatus,
                        "ASSIGNED",
                        "CANCELLED");

        case "ASSIGNED" ->
                allowTransition(
                        newStatus,
                        "ACCEPTED",
                        "CANCELLED");

        case "ACCEPTED" ->
                allowTransition(
                        newStatus,
                        "IN_PROGRESS",
                        "CANCELLED");

        case "IN_PROGRESS" ->
                allowTransition(
                        newStatus,
                        "REPORT_SUBMITTED",
                        "CANCELLED");

        case "REPORT_SUBMITTED" ->
                allowTransition(
                        newStatus,
                        "UNDER_REVIEW");

        case "UNDER_REVIEW" ->
                allowTransition(
                        newStatus,
                        "COMPLETED",
                        "CANCELLED");

        case "COMPLETED",
             "CANCELLED" ->
                throw new IllegalStateException(
                        "Request is already closed");

        default ->
                throw new IllegalStateException(
                        "Unknown current status: "
                                + currentStatus);
    }
}
private void allowTransition(
        String targetStatus,
        String... allowedStatuses) {

    for (String status : allowedStatuses) {

        if (status.equalsIgnoreCase(
                targetStatus)) {

            return;
        }
    }

    throw new IllegalStateException(
            "Transition not allowed from current state to "
                    + targetStatus);
}
private void createStatusHistory(
        ServiceRequestEntity serviceRequest,
        String previousStatus,
        String newStatus,
        String reason) {

    ServiceRequestStatusHistoryEntity history =
            new ServiceRequestStatusHistoryEntity();

    history.setServiceRequest(
            serviceRequest);

    history.setPreviousStatus(
            previousStatus);

    history.setNewStatus(
            newStatus);

    history.setChangeReason(
            reason);

    history.setChangedAt(
            Instant.now());

    serviceRequestStatusHistoryRepository.save(
            history);
}
@Override
public DocumentProcurementDetailsResponse
getDocumentProcurementRequest(
        UUID serviceRequestId) {

    ServiceRequestEntity serviceRequest =
        serviceRequestRepository.findByIdWithProperty(
        serviceRequestId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Service request not found"));

    securityService.validatePropertyOwnership(
            serviceRequest.getProperty());

    DocumentProcurementDetailsResponse response =
            new DocumentProcurementDetailsResponse();

    response.setServiceRequestId(
            serviceRequest.getServiceRequestId());

    response.setPropertyId(
            serviceRequest.getProperty()
                    .getPropertyId());

    response.setRequestType(
            serviceRequest.getRequestType());

    response.setPriority(
            serviceRequest.getPriority());

    response.setStatus(
            serviceRequest.getStatus());

    response.setAmount(
            serviceRequest.getAmount());

    response.setRequestedAt(
            serviceRequest.getRequestedAt());

    response.setScheduledAt(
            serviceRequest.getScheduledAt());

    response.setCompletedAt(
            serviceRequest.getCompletedAt());

    response.setDescription(
            serviceRequest.getDescription());

    response.setCancellationReasonCode(
            serviceRequest.getCancellationReasonCode());

    response.setDocumentCode(
            extractDocumentCode(
                    serviceRequest.getDescription()));

    return response;
}

@Override
public List<DocumentProcurementStatusHistoryResponse>
getDocumentProcurementHistory(
        UUID serviceRequestId) {

    ServiceRequestEntity serviceRequest =
        serviceRequestRepository
                .findByIdWithProperty(
                        serviceRequestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Service request not found"));

    securityService.validatePropertyOwnership(
            serviceRequest.getProperty());

    return serviceRequestStatusHistoryRepository
            .findByServiceRequest_ServiceRequestIdOrderByChangedAtDesc(
                    serviceRequestId)
            .stream()
            .map(history -> {

                DocumentProcurementStatusHistoryResponse response =
                        new DocumentProcurementStatusHistoryResponse();

                response.setPreviousStatus(
                        history.getPreviousStatus());

                response.setNewStatus(
                        history.getNewStatus());

                response.setReason(
                        history.getChangeReason());

                response.setChangedAt(
                        history.getChangedAt());

                return response;
            })
            .toList();
}

@Override
public List<ServiceRequestSummaryResponse>
getMyServiceRequests() {

    CustomerEntity customer =
            securityService.getCurrentCustomer();

    return serviceRequestRepository
        .findByCustomerWithProperty(
                customer.getCustomerId())
            .stream()
            .map(serviceRequest -> {

                ServiceRequestSummaryResponse response =
                        new ServiceRequestSummaryResponse();

                response.setServiceRequestId(
                        serviceRequest.getServiceRequestId());

                response.setPropertyId(
                        serviceRequest.getProperty()
                                .getPropertyId());

                response.setPropertyTitle(
                        serviceRequest.getProperty()
                                .getTitle());

                response.setRequestType(
                        serviceRequest.getRequestType());

                response.setStatus(
                        serviceRequest.getStatus());

                response.setPriority(
                        serviceRequest.getPriority());

                response.setRequestedAt(
                        serviceRequest.getRequestedAt());

                return response;
            })
            .toList();
}

@Override
public List<AdminServiceRequestSummaryResponse>
getAllServiceRequestsForAdmin() {

    securityService.validateAdminAccess();

    return serviceRequestRepository
            .findAllForAdminDashboard()
            .stream()
            .map(request -> {

                AdminServiceRequestSummaryResponse response =
                        new AdminServiceRequestSummaryResponse();

                response.setServiceRequestId(
                        request.getServiceRequestId());

                response.setPropertyId(
                        request.getProperty()
                                .getPropertyId());

                response.setPropertyTitle(
                        request.getProperty()
                                .getTitle());

                response.setCustomerId(
                        request.getProperty()
                                .getCustomer()
                                .getCustomerId());

                UserEntity user =
        request.getProperty()
                .getCustomer()
                .getUser();

response.setCustomerName(
        user.getFullName());

                response.setRequestType(
                        request.getRequestType());

                response.setStatus(
                        request.getStatus());

                response.setPriority(
                        request.getPriority());

                response.setRequestedAt(
                        request.getRequestedAt());

                return response;
            })
            .toList();
}
@Override
public AdminServiceRequestDetailsResponse
getAdminServiceRequest(
        UUID serviceRequestId) {

    securityService.validateAdminAccess();

    ServiceRequestEntity serviceRequest =
            serviceRequestRepository
                    .findByIdForAdmin(
                            serviceRequestId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Service request not found"));

    AdminServiceRequestDetailsResponse response =
            new AdminServiceRequestDetailsResponse();

    response.setServiceRequestId(
            serviceRequest.getServiceRequestId());

    response.setCustomerId(
            serviceRequest.getCustomer()
                    .getCustomerId());

    response.setCustomerName(
            serviceRequest.getCustomer()
                    .getUser()
                    .getFullName());

    response.setCustomerEmail(
            serviceRequest.getCustomer()
                    .getUser()
                    .getEmail());

    response.setCustomerMobile(
            serviceRequest.getCustomer()
                    .getUser()
                    .getMobileNumber());

    response.setPropertyId(
            serviceRequest.getProperty()
                    .getPropertyId());

    response.setPropertyTitle(
            serviceRequest.getProperty()
                    .getTitle());

    response.setRequestType(
            serviceRequest.getRequestType());

    response.setStatus(
            serviceRequest.getStatus());

    response.setPriority(
            serviceRequest.getPriority());

    response.setAmount(
            serviceRequest.getAmount());

    response.setDescription(
            serviceRequest.getDescription());

    response.setRequestedAt(
            serviceRequest.getRequestedAt());

    response.setScheduledAt(
            serviceRequest.getScheduledAt());

    response.setCompletedAt(
            serviceRequest.getCompletedAt());

    response.setCancellationReasonCode(
            serviceRequest.getCancellationReasonCode());

    List<ServiceRequestStatusHistoryResponse>
            historyResponses =
            serviceRequestStatusHistoryRepository
                    .findByServiceRequest_ServiceRequestIdOrderByChangedAtDesc(
                            serviceRequestId)
                    .stream()
                    .map(history -> {

                        ServiceRequestStatusHistoryResponse dto =
                                new ServiceRequestStatusHistoryResponse();

                        dto.setPreviousStatus(
                                history.getPreviousStatus());

                        dto.setNewStatus(
                                history.getNewStatus());

                        dto.setChangeReason(
                                history.getChangeReason());

                        dto.setChangedAt(
                                history.getChangedAt());

                        return dto;
                    })
                    .toList();

    ServiceAssignmentEntity assignment =
            serviceAssignmentRepository
                    .findByServiceRequest_ServiceRequestIdAndStatus(
                            serviceRequestId,
                            "ACTIVE")
                    .orElse(null);

    if (assignment != null) {

        response.setAssignedToUserId(
                assignment.getAgent()
                        .getUser()
                        .getUserId());

        response.setAssignedToName(
                assignment.getAgent()
                        .getUser()
                        .getFullName());

        response.setAssignedAt(
                assignment.getAssignedAt()
                        .toInstant());

        response.setAssignedByUserId(
                assignment.getCreatedBy());

         if (assignment.getCreatedBy() != null) {

        userRepository
                .findById(
                        assignment.getCreatedBy())
                .ifPresent(user ->
                        response.setAssignedByName(
                                user.getFullName()));
    }

        
    }

    response.setStatusHistory(
            historyResponses);

    return response;
}

@Override
public AdminServiceRequestDetailsResponse
updateAdminServiceRequestStatus(
        UUID serviceRequestId,
        AdminServiceRequestStatusUpdateRequest request) {

    securityService.validateAdminAccess();

    ServiceRequestEntity serviceRequest =
            serviceRequestRepository
                    .findById(serviceRequestId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Service request not found"));

    String oldStatus =
            serviceRequest.getStatus();

    String newStatus =
            request.getStatus();

    if (newStatus == null
            || newStatus.isBlank()) {

        throw new IllegalArgumentException(
                "Status is mandatory");
    }

    if ("CANCELLED".equalsIgnoreCase(newStatus)
            && (request.getReason() == null
            || request.getReason().isBlank())) {

        throw new IllegalArgumentException(
                "Cancellation reason is mandatory");
    }

    validateTransition(
            oldStatus,
            newStatus);

    serviceRequest.setStatus(
            newStatus);

    if ("COMPLETED".equalsIgnoreCase(
            newStatus)) {

        serviceRequest.setCompletedAt(
                Instant.now());
    }

    if ("CANCELLED".equalsIgnoreCase(
            newStatus)) {

        serviceRequest.setCancellationReasonCode(
                request.getReason());

        serviceRequest.setCompletedAt(
                Instant.now());
    }

    serviceRequestRepository.save(
            serviceRequest);

    createStatusHistory(
            serviceRequest,
            oldStatus,
            newStatus,
            request.getReason());

    return getAdminServiceRequest(
            serviceRequestId);
}
@Override
public AdminDashboardResponse
getAdminDashboard() {

    securityService.validateAdminAccess();

    List<ServiceRequestEntity> requests =
            serviceRequestRepository.findAll();

    AdminDashboardResponse response =
            new AdminDashboardResponse();

    response.setTotalRequests(
            requests.size());

    response.setNewRequests(
            requests.stream()
                    .filter(r -> "NEW".equalsIgnoreCase(
                            r.getStatus()))
                    .count());

    response.setPendingPayment(
            requests.stream()
                    .filter(r ->
                            "PENDING_PAYMENT"
                                    .equalsIgnoreCase(
                                            r.getStatus()))
                    .count());

    response.setPaymentCompleted(
            requests.stream()
                    .filter(r ->
                            "PAYMENT_COMPLETED"
                                    .equalsIgnoreCase(
                                            r.getStatus()))
                    .count());

    response.setPendingAssignment(
            requests.stream()
                    .filter(r ->
                            "PENDING_ASSIGNMENT"
                                    .equalsIgnoreCase(
                                            r.getStatus()))
                    .count());

    response.setAssigned(
            requests.stream()
                    .filter(r ->
                            "ASSIGNED"
                                    .equalsIgnoreCase(
                                            r.getStatus()))
                    .count());

    response.setAccepted(
            requests.stream()
                    .filter(r ->
                            "ACCEPTED"
                                    .equalsIgnoreCase(
                                            r.getStatus()))
                    .count());

    response.setInProgress(
            requests.stream()
                    .filter(r ->
                            "IN_PROGRESS"
                                    .equalsIgnoreCase(
                                            r.getStatus()))
                    .count());

    response.setReportSubmitted(
            requests.stream()
                    .filter(r ->
                            "REPORT_SUBMITTED"
                                    .equalsIgnoreCase(
                                            r.getStatus()))
                    .count());

    response.setUnderReview(
            requests.stream()
                    .filter(r ->
                            "UNDER_REVIEW"
                                    .equalsIgnoreCase(
                                            r.getStatus()))
                    .count());

    response.setCompleted(
            requests.stream()
                    .filter(r ->
                            "COMPLETED"
                                    .equalsIgnoreCase(
                                            r.getStatus()))
                    .count());

    response.setCancelled(
            requests.stream()
                    .filter(r ->
                            "CANCELLED"
                                    .equalsIgnoreCase(
                                            r.getStatus()))
                    .count());

    return response;
}
@Override
public AdminServiceRequestDetailsResponse
assignServiceRequest(
        UUID serviceRequestId,
        AdminAssignServiceRequestRequest request) {

    securityService.validateAdminAccess();

    ServiceRequestEntity serviceRequest =
            serviceRequestRepository
                    .findById(serviceRequestId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Service request not found"));

    String currentStatus =
            serviceRequest.getStatus();

    if (request.getAssignedTo() == null) {
        throw new IllegalArgumentException(
                "Assigned user is required");
    }

    validateTransition(
            currentStatus,
            "ASSIGNED");

    AgentEntity agent =
            agentRepository
                    .findByUser_UserIdAndStatus(
                            request.getAssignedTo(),
                            "ACTIVE")
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Active agent not found for the assigned user"));

    ServiceAssignmentEntity assignment =
            new ServiceAssignmentEntity();

    assignment.setServiceRequest(
            serviceRequest);

    assignment.setAssigneeType(
            "AGENT");

    assignment.setAgent(
            agent);

    assignment.setAssignedAt(
            OffsetDateTime.now());

    assignment.setStatus(
            "ACTIVE");

    assignment.setAssignmentReason(
            "Assigned by admin");

    assignment.setCreatedBy(
            securityService.getCurrentUserId());

    serviceAssignmentRepository.save(
            assignment);

    serviceRequest.setStatus(
            "ASSIGNED");

    serviceRequestRepository.save(
            serviceRequest);

    createStatusHistory(
            serviceRequest,
            currentStatus,
            "ASSIGNED",
            "Assigned by admin");

    return getAdminServiceRequest(
            serviceRequestId);
}
}
