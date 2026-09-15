package com.propertypilot.application.service;

import com.propertypilot.application.dto.AdminServiceRequestSummaryResponse;
import com.propertypilot.application.dto.AdminAssignServiceRequestRequest;
import com.propertypilot.application.dto.AdminDashboardResponse;
import com.propertypilot.application.dto.AdminServiceRequestDetailsResponse;
import com.propertypilot.application.dto.AdminServiceRequestStatusUpdateRequest;
import com.propertypilot.application.dto.DocumentChecklistResponse;
import com.propertypilot.application.dto.DocumentCreateRequest;
import com.propertypilot.application.dto.DocumentProcurementDetailsResponse;
import com.propertypilot.application.dto.DocumentProcurementRequest;
import com.propertypilot.application.dto.DocumentProcurementRequestSummaryResponse;
import com.propertypilot.application.dto.DocumentProcurementResponse;
import com.propertypilot.application.dto.DocumentProcurementStatusHistoryResponse;
import com.propertypilot.application.dto.DocumentProcurementStatusUpdateRequest;
import com.propertypilot.application.dto.DocumentResponse;
import com.propertypilot.application.dto.DocumentVersionCreateRequest;
import com.propertypilot.application.dto.DocumentVersionResponse;
import com.propertypilot.application.dto.ServiceRequestSummaryResponse;
import com.propertypilot.application.dto.ServiceRequestDetailsResponse;

import java.util.List;
import java.util.UUID;

public interface DocumentService {

    DocumentResponse createDocument(
            UUID propertyId,
            DocumentCreateRequest request);

    List<DocumentResponse> getDocumentsByProperty(
            UUID propertyId);

    DocumentResponse getDocument(
            UUID documentId);

    List<DocumentVersionResponse> getDocumentVersions(
            UUID documentId);

    DocumentResponse archiveDocument(
            UUID documentId);

    DocumentResponse restoreDocument(
            UUID documentId);
    DocumentVersionResponse createDocumentVersion(
            UUID documentId,
            DocumentVersionCreateRequest request);

    DocumentChecklistResponse getDocumentChecklist(
            UUID propertyId);
            DocumentProcurementResponse requestDocumentProcurement(
        UUID propertyId,
        DocumentProcurementRequest request);
        List<DocumentProcurementRequestSummaryResponse>
getDocumentProcurementRequests(
        UUID propertyId);
        DocumentProcurementResponse updateDocumentProcurementStatus(
        UUID serviceRequestId,
        DocumentProcurementStatusUpdateRequest request);
        DocumentProcurementDetailsResponse
getDocumentProcurementRequest(
        UUID serviceRequestId);
        List<DocumentProcurementStatusHistoryResponse>
getDocumentProcurementHistory(
        UUID serviceRequestId);
        List<ServiceRequestSummaryResponse>
getMyServiceRequests();

ServiceRequestDetailsResponse
getServiceRequest(
        UUID serviceRequestId);
        List<AdminServiceRequestSummaryResponse>
getAllServiceRequestsForAdmin();

AdminServiceRequestDetailsResponse
getAdminServiceRequest(
        UUID serviceRequestId);

        AdminServiceRequestDetailsResponse
updateAdminServiceRequestStatus(
        UUID serviceRequestId,
        AdminServiceRequestStatusUpdateRequest request);
        AdminDashboardResponse getAdminDashboard();

        AdminServiceRequestDetailsResponse
assignServiceRequest(
        UUID serviceRequestId,
        AdminAssignServiceRequestRequest request);
}
