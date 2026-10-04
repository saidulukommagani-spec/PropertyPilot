package com.propertypilot.web.controller;
import com.propertypilot.application.dto.DocumentProcurementResponse;
import com.propertypilot.application.dto.DocumentProcurementDetailsResponse;
import com.propertypilot.application.dto.DocumentProcurementStatusHistoryResponse;
import com.propertypilot.application.dto.DocumentProcurementStatusUpdateRequest;
import com.propertypilot.application.dto.ServiceRequestSummaryResponse;
import com.propertypilot.application.dto.ServiceRequestDetailsResponse;
import com.propertypilot.application.dto.AdminServiceRequestSummaryResponse;
import com.propertypilot.application.dto.AdminServiceRequestDetailsResponse;
import com.propertypilot.application.dto.AdminServiceRequestStatusUpdateRequest;
import com.propertypilot.application.dto.AdminAssignServiceRequestRequest;
import com.propertypilot.application.dto.AdminDashboardResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.DocumentCreateRequest;
import com.propertypilot.application.dto.DocumentResponse;
import com.propertypilot.application.dto.DocumentVersionCreateRequest;
import com.propertypilot.application.dto.DocumentVersionResponse;
import com.propertypilot.application.dto.DocumentChecklistResponse;
import com.propertypilot.application.dto.DocumentChecklistItemResponse;
import com.propertypilot.application.service.DocumentService;
import com.propertypilot.security.CustomUserDetailsService;
import com.propertypilot.security.JwtService;
import com.propertypilot.web.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DocumentService documentService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Create Document")
    void createDocument_success() throws Exception {

        UUID propertyId = UUID.randomUUID();

        when(documentService.createDocument(
                eq(propertyId),
                any(DocumentCreateRequest.class)))
                .thenReturn(buildDocumentResponse());

        DocumentCreateRequest request =
                new DocumentCreateRequest();

        request.setDocumentName(
                "Sale Deed");

        request.setDocumentType(
                "LEGAL");

        mockMvc.perform(
                        post(
                                "/api/v1/properties/{propertyId}/documents",
                                propertyId)
                                .contentType("application/json")
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.documentName")
                        .value("Sale Deed"));
    }

    @Test
    @DisplayName("Create Document Validation Failure")
    void createDocument_validationFailure()
            throws Exception {

        String request = """
                {
                  "documentName":"",
                  "documentType":""
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/properties/{propertyId}/documents",
                                UUID.randomUUID())
                                .contentType("application/json")
                                .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"));
    }

    @Test
    @DisplayName("Get Documents By Property")
    void getDocumentsByProperty_success()
            throws Exception {

        UUID propertyId = UUID.randomUUID();

        when(documentService
                .getDocumentsByProperty(
                        propertyId))
                .thenReturn(
                        List.of(
                                buildDocumentResponse()));

        mockMvc.perform(
                        get(
                                "/api/v1/properties/{propertyId}/documents",
                                propertyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].documentName")
                        .value("Sale Deed"));
    }

    @Test
    @DisplayName("Get Document")
    void getDocument_success()
            throws Exception {

        UUID documentId = UUID.randomUUID();

        when(documentService.getDocument(
                documentId))
                .thenReturn(
                        buildDocumentResponse());

        mockMvc.perform(
                        get(
                                "/api/v1/documents/{documentId}",
                                documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentName")
                        .value("Sale Deed"));
    }

    @Test
    @DisplayName("Create Document Version")
    void createDocumentVersion_success()
            throws Exception {

        UUID documentId = UUID.randomUUID();

        when(documentService.createDocumentVersion(
                eq(documentId),
                any(DocumentVersionCreateRequest.class)))
                .thenReturn(
                        buildDocumentVersionResponse());

        DocumentVersionCreateRequest request =
                new DocumentVersionCreateRequest();

        request.setFileName(
                "sale-deed.pdf");

        request.setFilePath(
                "/docs/sale-deed.pdf");

        request.setMimeType(
                "application/pdf");

        request.setFileSize(
                5000L);

        mockMvc.perform(
                        post(
                                "/api/v1/documents/{documentId}/versions",
                                documentId)
                                .contentType("application/json")
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileName")
                        .value("sale-deed.pdf"));
    }

    @Test
    @DisplayName("Create Document Version Validation Failure")
    void createDocumentVersion_validationFailure()
            throws Exception {

        UUID documentId = UUID.randomUUID();

        String request = """
                {
                  "fileName":"",
                  "filePath":""
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/documents/{documentId}/versions",
                                documentId)
                                .contentType("application/json")
                                .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"));
    }

    @Test
    @DisplayName("Get Document Versions")
    void getDocumentVersions_success()
            throws Exception {

        UUID documentId = UUID.randomUUID();

        when(documentService.getDocumentVersions(
                documentId))
                .thenReturn(
                        List.of(
                                buildDocumentVersionResponse()));

        mockMvc.perform(
                        get(
                                "/api/v1/documents/{documentId}/versions",
                                documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fileName")
                        .value("sale-deed.pdf"));
    }

    @Test
    @DisplayName("Archive Document")
    void archiveDocument_success()
            throws Exception {

        UUID documentId = UUID.randomUUID();

        DocumentResponse response =
                buildDocumentResponse();

        response.setStatus(
                "ARCHIVED");

        when(documentService.archiveDocument(
                documentId))
                .thenReturn(response);

        mockMvc.perform(
                        patch(
                                "/api/v1/documents/{documentId}/archive",
                                documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("ARCHIVED"));
    }

    @Test
    @DisplayName("Restore Document")
    void restoreDocument_success()
            throws Exception {

        UUID documentId = UUID.randomUUID();

        DocumentResponse response =
                buildDocumentResponse();

        response.setStatus(
                "ACTIVE");

        when(documentService.restoreDocument(
                documentId))
                .thenReturn(response);

        mockMvc.perform(
                        patch(
                                "/api/v1/documents/{documentId}/restore",
                                documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("ACTIVE"));
    }

    @Test
    @DisplayName("Get Document Checklist")
    void getDocumentChecklist_success()
            throws Exception {

        UUID propertyId = UUID.randomUUID();

        when(documentService.getDocumentChecklist(
                propertyId))
                .thenReturn(
                        buildChecklistResponse());

        mockMvc.perform(
                        get(
                                "/api/v1/properties/{propertyId}/document-checklist",
                                propertyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.propertyCategory")
                        .value("RESIDENTIAL"));
    }

    private DocumentResponse buildDocumentResponse() {

        DocumentResponse response =
                new DocumentResponse();

        response.setDocumentId(
                UUID.randomUUID());

        response.setPropertyId(
                UUID.randomUUID());

        response.setDocumentName(
                "Sale Deed");

        response.setDocumentType(
                "LEGAL");

        response.setStatus(
                "ACTIVE");

        return response;
    }

    private DocumentVersionResponse
    buildDocumentVersionResponse() {

        DocumentVersionResponse response =
                new DocumentVersionResponse();

        response.setVersionId(
                UUID.randomUUID());

        response.setDocumentId(
                UUID.randomUUID());

        response.setVersionNumber(1);

        response.setFileName(
                "sale-deed.pdf");

        response.setMimeType(
                "application/pdf");

        response.setFileSize(
                5000L);

        response.setCurrentVersion(
                true);

        return response;
    }

    private DocumentChecklistResponse
    buildChecklistResponse() {

        DocumentChecklistItemResponse item =
                new DocumentChecklistItemResponse();

        item.setDocumentCode(
                "SALE_DEED");

        item.setDocumentName(
                "Sale Deed");

        item.setMandatory(
                true);

        item.setUploaded(
                true);

        DocumentChecklistResponse response =
                new DocumentChecklistResponse();

        response.setPropertyId(
                UUID.randomUUID());

        response.setPropertyCategory(
                "RESIDENTIAL");

        response.setCompletionPercentage(
                100);

        response.setTotalDocuments(
                1);

        response.setUploadedDocuments(
                1);

        response.setDocuments(
                List.of(item));

        return response;
    }

    @Test
@DisplayName("Update Procurement Status")
void updateProcurementStatus_success() throws Exception {

    UUID serviceRequestId = UUID.randomUUID();

    DocumentProcurementResponse response =
            new DocumentProcurementResponse();

    response.setServiceRequestId(serviceRequestId);
    response.setStatus("IN_PROGRESS");

    when(documentService.updateDocumentProcurementStatus(
            eq(serviceRequestId),
            any(DocumentProcurementStatusUpdateRequest.class)))
            .thenReturn(response);

    mockMvc.perform(
                    patch("/api/v1/document-procurement-requests/{id}/status",
                            serviceRequestId)
                            .contentType("application/json")
                            .content("""
                                    {
                                      "status":"IN_PROGRESS",
                                      "reason":"Started"
                                    }
                                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status")
                    .value("IN_PROGRESS"));
}

@Test
@DisplayName("Get Procurement Request Details")
void getProcurementRequest_success() throws Exception {

    UUID serviceRequestId = UUID.randomUUID();

    DocumentProcurementDetailsResponse response =
            new DocumentProcurementDetailsResponse();

    response.setServiceRequestId(serviceRequestId);
    response.setStatus("REQUESTED");

    when(documentService.getDocumentProcurementRequest(
            serviceRequestId))
            .thenReturn(response);

    mockMvc.perform(
                    get("/api/v1/document-procurement-requests/{id}",
                            serviceRequestId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status")
                    .value("REQUESTED"));
}

@Test
@DisplayName("Get Procurement History")
void getProcurementHistory_success() throws Exception {

    UUID serviceRequestId = UUID.randomUUID();

    DocumentProcurementStatusHistoryResponse history =
            new DocumentProcurementStatusHistoryResponse();

    history.setPreviousStatus("NEW");
    history.setNewStatus("IN_PROGRESS");

    when(documentService.getDocumentProcurementHistory(
            serviceRequestId))
            .thenReturn(List.of(history));

    mockMvc.perform(
                    get("/api/v1/document-procurement-requests/{id}/history",
                            serviceRequestId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].newStatus")
                    .value("IN_PROGRESS"));
}

@Test
@DisplayName("Get My Service Requests")
void getMyServiceRequests_success() throws Exception {

    ServiceRequestSummaryResponse response =
            new ServiceRequestSummaryResponse();

    response.setRequestType("DOCUMENT_PROCUREMENT");
    response.setStatus("REQUESTED");

    when(documentService.getMyServiceRequests())
            .thenReturn(List.of(response));

    mockMvc.perform(
                    get("/api/v1/service-requests"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].status")
                    .value("REQUESTED"));
}

@Test
@DisplayName("Get Service Request Details")
void getServiceRequest_success() throws Exception {

    UUID serviceRequestId = UUID.randomUUID();

    ServiceRequestDetailsResponse response =
            new ServiceRequestDetailsResponse();

    response.setServiceRequestId(serviceRequestId);
    response.setStatus("REQUESTED");

    when(documentService.getServiceRequest(
            serviceRequestId))
            .thenReturn(response);

    mockMvc.perform(
                    get("/api/v1/service-requests/{id}",
                            serviceRequestId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status")
                    .value("REQUESTED"));
}

@Test
@DisplayName("Get All Service Requests For Admin")
void getAllServiceRequestsForAdmin_success() throws Exception {

    AdminServiceRequestSummaryResponse response =
            new AdminServiceRequestSummaryResponse();

    response.setCustomerName("John");
    response.setStatus("ASSIGNED");

    when(documentService.getAllServiceRequestsForAdmin())
            .thenReturn(List.of(response));

    mockMvc.perform(
                    get("/api/v1/admin/service-requests"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].status")
                    .value("ASSIGNED"));
}

@Test
@DisplayName("Get Admin Service Request Details")
void getAdminServiceRequest_success() throws Exception {

    UUID serviceRequestId = UUID.randomUUID();

    AdminServiceRequestDetailsResponse response =
            new AdminServiceRequestDetailsResponse();

    response.setServiceRequestId(serviceRequestId);
    response.setStatus("ASSIGNED");

    when(documentService.getAdminServiceRequest(
            serviceRequestId))
            .thenReturn(response);

    mockMvc.perform(
                    get("/api/v1/admin/service-requests/{id}",
                            serviceRequestId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status")
                    .value("ASSIGNED"));
}

@Test
@DisplayName("Update Admin Service Request Status")
void updateAdminServiceRequestStatus_success() throws Exception {

    UUID serviceRequestId = UUID.randomUUID();

    AdminServiceRequestDetailsResponse response =
            new AdminServiceRequestDetailsResponse();

    response.setServiceRequestId(serviceRequestId);
    response.setStatus("IN_PROGRESS");

    when(documentService.updateAdminServiceRequestStatus(
            eq(serviceRequestId),
            any(AdminServiceRequestStatusUpdateRequest.class)))
            .thenReturn(response);

    mockMvc.perform(
                    patch("/api/v1/admin/service-requests/{id}/status",
                            serviceRequestId)
                            .contentType("application/json")
                            .content("""
                                    {
                                      "status":"IN_PROGRESS",
                                      "reason":"Started"
                                    }
                                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status")
                    .value("IN_PROGRESS"));
}

@Test
@DisplayName("Get Admin Dashboard")
void getAdminDashboard_success() throws Exception {

    AdminDashboardResponse response =
            new AdminDashboardResponse();

    response.setTotalRequests(100);
    response.setCompleted(50);

    when(documentService.getAdminDashboard())
            .thenReturn(response);

    mockMvc.perform(
                    get("/api/v1/admin/dashboard"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalRequests")
                    .value(100));
}

@Test
@DisplayName("Assign Service Request")
void assignServiceRequest_success() throws Exception {

    UUID serviceRequestId = UUID.randomUUID();

    AdminServiceRequestDetailsResponse response =
            new AdminServiceRequestDetailsResponse();

    response.setServiceRequestId(serviceRequestId);
    response.setStatus("ASSIGNED");

    when(documentService.assignServiceRequest(
            eq(serviceRequestId),
            any(AdminAssignServiceRequestRequest.class)))
            .thenReturn(response);

    mockMvc.perform(
                    patch("/api/v1/service-requests/{id}/assign",
                            serviceRequestId)
                            .contentType("application/json")
                            .content("""
                                    {
                                      "assignedTo":"550e8400-e29b-41d4-a716-446655440000"
                                    }
                                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status")
                    .value("ASSIGNED"));
}
}