package com.propertypilot.application.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import com.propertypilot.application.dto.AdminAssignServiceRequestRequest;
import com.propertypilot.application.dto.AdminServiceRequestDetailsResponse;
import com.propertypilot.application.dto.AdminServiceRequestStatusUpdateRequest;
import com.propertypilot.application.dto.DocumentCreateRequest;
import com.propertypilot.application.dto.DocumentProcurementRequest;
import com.propertypilot.application.dto.DocumentProcurementStatusUpdateRequest;
import com.propertypilot.application.dto.DocumentVersionCreateRequest;
import com.propertypilot.infrastructure.persistence.entity.AgentEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.Document;
import com.propertypilot.infrastructure.persistence.entity.DocumentServiceCatalog;
import com.propertypilot.infrastructure.persistence.entity.DocumentType;
import com.propertypilot.infrastructure.persistence.entity.DocumentVersion;
import com.propertypilot.infrastructure.persistence.entity.Property;
import com.propertypilot.infrastructure.persistence.entity.ServiceAssignmentEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestStatusHistoryEntity;
import com.propertypilot.infrastructure.persistence.entity.UserEntity;
import com.propertypilot.infrastructure.persistence.repository.AgentRepository;
import com.propertypilot.infrastructure.persistence.repository.DocumentRepository;
import com.propertypilot.infrastructure.persistence.repository.DocumentServiceCatalogRepository;
import com.propertypilot.infrastructure.persistence.repository.DocumentTypeRepository;
import com.propertypilot.infrastructure.persistence.repository.DocumentVersionRepository;
import com.propertypilot.infrastructure.persistence.repository.PropertyRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceAssignmentRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestStatusHistoryRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRepository;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.ResourceNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;


import java.time.Instant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceImplTest {
    @Mock DocumentRepository documentRepository;
    @Mock DocumentServiceCatalogRepository documentServiceCatalogRepository;
    @Mock DocumentTypeRepository documentTypeRepository;
    @Mock DocumentVersionRepository documentVersionRepository;
    @Mock PropertyRepository propertyRepository;
    @Mock SecurityService securityService;
@Mock
ServiceRequestRepository serviceRequestRepository;

@Mock
ServiceRequestStatusHistoryRepository
        serviceRequestStatusHistoryRepository;

@Mock
ServiceAssignmentRepository
        serviceAssignmentRepository;

@Mock
AgentRepository agentRepository;

@Mock
UserRepository userRepository;

    @InjectMocks DocumentServiceImpl service;

    @Test
    void checklistCountsUploadedTypesOnceAndExcludesArchivedAndEmptyDocuments() {
        Property property = property();
        when(propertyRepository.findById(property.getPropertyId()))
        .thenReturn(Optional.of(property));
        Document uploaded = document("SALE_DEED", "ACTIVE");
        Document duplicate = document("SALE_DEED", "ACTIVE");
        Document empty = document("APPROVED_LAYOUT_PLAN", "ACTIVE");
        Document archived = document("GIFT_DEED", "ARCHIVED");
        when(documentRepository.findByProperty_PropertyId(property.getPropertyId()))
                .thenReturn(List.of(uploaded, duplicate, empty, archived));
        for (Document document : List.of(uploaded, duplicate)) {
            when(documentVersionRepository.findByDocument_DocumentIdAndCurrentVersionTrue(document.getDocumentId()))
                    .thenReturn(Optional.of(new DocumentVersion()));
        }
        when(documentTypeRepository.findByPropertyCategoryInAndActiveTrue(List.of("COMMON", "PLOT")))
                .thenReturn(List.of(type("SALE_DEED"), type("APPROVED_LAYOUT_PLAN"), type("GIFT_DEED")));

        var response = service.getDocumentChecklist(property.getPropertyId());

        assertThat(response.getPropertyId()).isEqualTo(property.getPropertyId());
        assertThat(response.getPropertyCategory()).isEqualTo("PLOT");
        assertThat(response.getTotalDocuments()).isEqualTo(3);
        assertThat(response.getUploadedDocuments()).isEqualTo(1);
        assertThat(response.getCompletionPercentage()).isEqualTo(33);
        assertThat(response.getDocuments()).extracting("documentStatus")
                .containsExactly("UPLOADED", "MISSING", "MISSING");
        verify(securityService).validatePropertyOwnership(property);
        verify(documentVersionRepository, never())
                .findByDocument_DocumentIdAndCurrentVersionTrue(archived.getDocumentId());
    }

    @Test
    void emptyChecklistHasZeroCompletion() {
        Property property = property();
        when(propertyRepository.findById(property.getPropertyId()))
        .thenReturn(Optional.of(property));
        var response = service.getDocumentChecklist(property.getPropertyId());
        assertThat(response.getDocuments()).isEmpty();
        assertThat(response.getCompletionPercentage()).isZero();
        assertThat(response.getUploadedDocuments()).isZero();
    }

   @Test
void checklistRejectsUnauthorizedAccessBeforeReadingDocuments() {

    Property property = property();

    when(propertyRepository.findById(property.getPropertyId()))
            .thenReturn(Optional.of(property));

    doThrow(new AccessDeniedException("Forbidden"))
            .when(securityService)
            .validatePropertyOwnership(property);

    assertThatThrownBy(() ->
            service.getDocumentChecklist(property.getPropertyId()))
            .isInstanceOf(AccessDeniedException.class);

    verifyNoInteractions(
            documentRepository,
            documentTypeRepository,
            documentVersionRepository);
}
   private Property property() {
    Property property = new Property();
    property.setPropertyId(UUID.randomUUID());
    property.setPropertyType("plot");
    return property;
}

    private Document document(String code, String status) {
        Document document = new Document();
        document.setDocumentId(UUID.randomUUID());
        document.setDocumentType(code);
        document.setStatus(status);
        return document;
    }

    private DocumentType type(String code) {
        DocumentType type = new DocumentType();
        type.setDocumentCode(code);
        type.setDocumentName(code);
        type.setMandatory(true);
        return type;
    }
private ServiceRequestEntity serviceRequest(String status) {

    UserEntity user = new UserEntity();
    user.setUserId(UUID.randomUUID());
    user.setFullName("Test User");
    user.setEmail("test@test.com");
    user.setMobileNumber("9999999999");

    CustomerEntity customer = new CustomerEntity();
    customer.setCustomerId(UUID.randomUUID());
    customer.setUser(user);

    Property property = property();
    property.setCustomer(customer);

    ServiceRequestEntity entity =
            new ServiceRequestEntity();

    entity.setServiceRequestId(UUID.randomUUID());
    entity.setStatus(status);
    entity.setRequestType("DOCUMENT_PROCUREMENT");
    entity.setDescription("Document Code: EC");

    entity.setCustomer(customer);
    entity.setProperty(property);

    return entity;
}
@Test
void updateDocumentProcurementStatus_completed() {

    UUID id = UUID.randomUUID();

    ServiceRequestEntity request =
         adminServiceRequest("UNDER_REVIEW");

    when(serviceRequestRepository.findById(id))
            .thenReturn(Optional.of(request));

    when(securityService.isAdmin())
            .thenReturn(true);

    DocumentProcurementStatusUpdateRequest dto =
            new DocumentProcurementStatusUpdateRequest();

    dto.setStatus("COMPLETED");

    var response =
            service.updateDocumentProcurementStatus(
                    id,
                    dto);

    assertThat(response.getStatus())
            .isEqualTo("COMPLETED");

    assertThat(request.getCompletedAt())
            .isNotNull();

    verify(serviceRequestRepository)
            .save(request);

    verify(serviceRequestStatusHistoryRepository)
            .save(any());
}

@Test
void updateDocumentProcurementStatus_cancelledWithoutReason() {

    UUID id = UUID.randomUUID();

    ServiceRequestEntity request =
            serviceRequest(
                    "UNDER_REVIEW");

    when(serviceRequestRepository.findById(id))
            .thenReturn(Optional.of(request));

    when(securityService.isAdmin())
            .thenReturn(true);

    DocumentProcurementStatusUpdateRequest dto =
            new DocumentProcurementStatusUpdateRequest();

    dto.setStatus("CANCELLED");

    assertThatThrownBy(() ->
            service.updateDocumentProcurementStatus(
                    id,
                    dto))
            .isInstanceOf(
                    IllegalArgumentException.class)
            .hasMessageContaining(
                    "Cancellation reason");
}

@Test
void updateDocumentProcurementStatus_requiresAdmin() {

    UUID id = UUID.randomUUID();

    ServiceRequestEntity request =
            serviceRequest(
                    "UNDER_REVIEW");

    when(serviceRequestRepository.findById(id))
            .thenReturn(Optional.of(request));

    when(securityService.isAdmin())
            .thenReturn(false);

    DocumentProcurementStatusUpdateRequest dto =
            new DocumentProcurementStatusUpdateRequest();

    dto.setStatus("COMPLETED");

    assertThatThrownBy(() ->
            service.updateDocumentProcurementStatus(
                    id,
                    dto))
            .isInstanceOf(
                    org.springframework.security.access
                            .AccessDeniedException.class);
}

@Test
void updateDocumentProcurementStatus_invalidTransition() {

    UUID id = UUID.randomUUID();

    ServiceRequestEntity request =
            serviceRequest(
                    "NEW");

    when(serviceRequestRepository.findById(id))
            .thenReturn(Optional.of(request));

    when(securityService.isAdmin())
            .thenReturn(true);

    DocumentProcurementStatusUpdateRequest dto =
            new DocumentProcurementStatusUpdateRequest();

    dto.setStatus("COMPLETED");

    assertThatThrownBy(() ->
            service.updateDocumentProcurementStatus(
                    id,
                    dto))
            .isInstanceOf(
                    IllegalStateException.class);
}

@Test
void updateDocumentProcurementStatus_closedRequest() {

    UUID id = UUID.randomUUID();

    ServiceRequestEntity request =
            serviceRequest(
                    "COMPLETED");

    when(serviceRequestRepository.findById(id))
            .thenReturn(Optional.of(request));

    when(securityService.isAdmin())
            .thenReturn(true);

    DocumentProcurementStatusUpdateRequest dto =
            new DocumentProcurementStatusUpdateRequest();

    dto.setStatus("COMPLETED");

    assertThatThrownBy(() ->
            service.updateDocumentProcurementStatus(
                    id,
                    dto))
            .isInstanceOf(
                    IllegalStateException.class)
            .hasMessageContaining(
                    "already closed");
}

@Test
void requestDocumentProcurement_success() {

    Property property = property();
    when(propertyRepository.findById(property.getPropertyId()))
        .thenReturn(Optional.of(property));

    property.setStatus("ACTIVE");

    DocumentProcurementRequest request =
            new DocumentProcurementRequest();

    request.setDocumentCode("EC");
    request.setRemarks("Need urgently");

    DocumentType type = new DocumentType();
    type.setDocumentCode("EC");
    type.setPropertyCategory("PLOT");

    DocumentServiceCatalog catalog =
            new DocumentServiceCatalog();

    catalog.setDocumentCode("EC");
    catalog.setServiceAvailable(true);
    catalog.setActive(true);

    when(documentTypeRepository
            .findByDocumentCodeAndActiveTrue("EC"))
            .thenReturn(Optional.of(type));

    when(documentServiceCatalogRepository
            .findByDocumentCode("EC"))
            .thenReturn(Optional.of(catalog));

    when(serviceRequestRepository.save(any()))
            .thenAnswer(inv -> inv.getArgument(0));

    var response =
            service.requestDocumentProcurement(
                    property.getPropertyId(),
                    request);

    assertThat(response.getDocumentCode())
            .isEqualTo("EC");

    assertThat(response.getStatus())
            .isEqualTo("NEW");

    verify(serviceRequestRepository)
            .save(any(ServiceRequestEntity.class));

    verify(serviceRequestStatusHistoryRepository)
            .save(any());
}

@Test
void requestDocumentProcurement_archivedProperty() {

    Property property = property();
when(propertyRepository.findById(property.getPropertyId()))
        .thenReturn(Optional.of(property));
    property.setStatus("ARCHIVED");

    DocumentProcurementRequest request =
            new DocumentProcurementRequest();

    request.setDocumentCode("EC");

    assertThatThrownBy(() ->
            service.requestDocumentProcurement(
                    property.getPropertyId(),
                    request))
            .isInstanceOf(
                    IllegalStateException.class)
            .hasMessageContaining(
                    "archived property");
}

@Test
void requestDocumentProcurement_missingDocumentCode() {

    Property property = property();
when(propertyRepository.findById(property.getPropertyId()))
        .thenReturn(Optional.of(property));
    property.setStatus("ACTIVE");

    DocumentProcurementRequest request =
            new DocumentProcurementRequest();

    assertThatThrownBy(() ->
            service.requestDocumentProcurement(
                    property.getPropertyId(),
                    request))
            .isInstanceOf(
                    IllegalArgumentException.class)
            .hasMessageContaining(
                    "Document code");
}

private ServiceRequestEntity procurementRequest() {

    UserEntity user = new UserEntity();
    user.setUserId(UUID.randomUUID());
    user.setFullName("Test User");
    user.setEmail("test@test.com");
    user.setMobileNumber("9999999999");

    CustomerEntity customer = new CustomerEntity();
    customer.setCustomerId(UUID.randomUUID());
    customer.setUser(user);

    Property property = property();
    property.setCustomer(customer);

    ServiceRequestEntity request =
            new ServiceRequestEntity();

    request.setServiceRequestId(UUID.randomUUID());
    request.setStatus("NEW");
    request.setPriority("MEDIUM");
    request.setRequestType("DOCUMENT_PROCUREMENT");
    request.setDescription("Document Code: EC");
    request.setRequestedAt(Instant.now());

    request.setCustomer(customer);
    request.setProperty(property);

    return request;
}
@Test
void getDocumentProcurementRequest_success() {

    ServiceRequestEntity request =
            procurementRequest();

    when(serviceRequestRepository
            .findByIdWithProperty(
                    request.getServiceRequestId()))
            .thenReturn(Optional.of(request));

    var response =
            service.getDocumentProcurementRequest(
                    request.getServiceRequestId());

    assertThat(response.getDocumentCode())
            .isEqualTo("EC");

    assertThat(response.getStatus())
            .isEqualTo("NEW");
}

@Test
void getDocumentProcurementHistory_success() {

    ServiceRequestEntity request =
            procurementRequest();

    ServiceRequestStatusHistoryEntity history =
            new ServiceRequestStatusHistoryEntity();

    history.setPreviousStatus("NEW");
    history.setNewStatus("ASSIGNED");

    when(serviceRequestRepository
            .findByIdWithProperty(
                    request.getServiceRequestId()))
            .thenReturn(Optional.of(request));

    when(serviceRequestStatusHistoryRepository
            .findByServiceRequest_ServiceRequestIdOrderByChangedAtDesc(
                    request.getServiceRequestId()))
            .thenReturn(List.of(history));

    var result =
            service.getDocumentProcurementHistory(
                    request.getServiceRequestId());

    assertThat(result).hasSize(1);

    assertThat(result.get(0).getNewStatus())
            .isEqualTo("ASSIGNED");
}

@Test
void getDocumentProcurementRequests_success() {

    Property property = property();
when(propertyRepository.findById(property.getPropertyId()))
        .thenReturn(Optional.of(property));
    ServiceRequestEntity request =
            procurementRequest();

    when(serviceRequestRepository
            .findByProperty_PropertyId(
                    property.getPropertyId()))
            .thenReturn(List.of(request));

    var result =
            service.getDocumentProcurementRequests(
                    property.getPropertyId());

    assertThat(result).hasSize(1);
}

@Test
void getAdminDashboard_success() {

    when(serviceRequestRepository.findAll())
            .thenReturn(List.of(
                    serviceRequest("NEW"),
                    serviceRequest("ASSIGNED"),
                    serviceRequest("COMPLETED"),
                    serviceRequest("CANCELLED")
            ));

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    var dashboard =
            service.getAdminDashboard();

    assertThat(dashboard.getTotalRequests())
            .isEqualTo(4);

    assertThat(dashboard.getNewRequests())
            .isEqualTo(1);

    assertThat(dashboard.getAssigned())
            .isEqualTo(1);

    assertThat(dashboard.getCompleted())
            .isEqualTo(1);

    assertThat(dashboard.getCancelled())
            .isEqualTo(1);
}
@Test
void createDocument_success() {

    UUID propertyId = UUID.randomUUID();

    Property property = property();
    property.setStatus("ACTIVE");

    DocumentCreateRequest request =
            new DocumentCreateRequest();

    request.setDocumentName("Sale Deed");
    request.setDocumentType("SALE_DEED");

    DocumentType type = new DocumentType();
    type.setDocumentCode("SALE_DEED");
    type.setPropertyCategory("PLOT");

    when(propertyRepository.findById(propertyId))
            .thenReturn(Optional.of(property));

    when(documentTypeRepository
            .findByDocumentCodeAndActiveTrue("SALE_DEED"))
            .thenReturn(Optional.of(type));

    when(documentRepository.save(any()))
            .thenAnswer(i -> i.getArgument(0));

    var response =
            service.createDocument(
                    propertyId,
                    request);

    assertThat(response.getDocumentName())
            .isEqualTo("Sale Deed");

    verify(documentRepository)
            .save(any(Document.class));
}

@Test
void createDocument_archivedProperty() {

    UUID propertyId = UUID.randomUUID();

    Property property = property();
    property.setStatus("ARCHIVED");

    DocumentCreateRequest request =
            new DocumentCreateRequest();

    request.setDocumentType("SALE_DEED");

    DocumentType type = new DocumentType();
    type.setDocumentCode("SALE_DEED");
    type.setPropertyCategory("PLOT");

    when(propertyRepository.findById(propertyId))
            .thenReturn(Optional.of(property));

    when(documentTypeRepository
            .findByDocumentCodeAndActiveTrue("SALE_DEED"))
            .thenReturn(Optional.of(type));

    assertThatThrownBy(() ->
            service.createDocument(
                    propertyId,
                    request))
            .isInstanceOf(
                    IllegalStateException.class);
}




@Test
void createDocumentVersion_success() {

    UUID documentId = UUID.randomUUID();

    Document document =
            document("SALE_DEED", "ACTIVE");

    Property property = property();
    property.setStatus("ACTIVE");

    document.setProperty(property);

    when(documentRepository.findById(documentId))
            .thenReturn(Optional.of(document));

    when(documentVersionRepository
            .findByDocument_DocumentIdAndCurrentVersionTrue(documentId))
            .thenReturn(Optional.empty());

    when(documentVersionRepository
            .findByDocument_DocumentIdOrderByVersionNumberDesc(documentId))
            .thenReturn(List.of());

    when(documentVersionRepository.save(any()))
            .thenAnswer(i -> i.getArgument(0));

    DocumentVersionCreateRequest request =
            new DocumentVersionCreateRequest();

    request.setFileName("test.pdf");
    request.setFilePath("/tmp/test.pdf");
    request.setMimeType("application/pdf");
    request.setFileSize(100L);

    var response =
            service.createDocumentVersion(
                    documentId,
                    request);

    assertThat(response.getVersionNumber())
            .isEqualTo(1);
}




private DocumentVersion version() {

    DocumentVersion version =
            new DocumentVersion();

    version.setVersionId(
            UUID.randomUUID());

    version.setVersionNumber(1);

    return version;
}

@Test
void getDocument_success() {

    Document document = document("EC", "ACTIVE");
    Property property = property();
    document.setProperty(property);

    when(documentRepository.findById(document.getDocumentId()))
            .thenReturn(Optional.of(document));

    var response =
            service.getDocument(document.getDocumentId());

    assertThat(response.getDocumentId())
            .isEqualTo(document.getDocumentId());

    verify(securityService)
            .validateDocumentOwnership(document);
}

@Test
void getDocumentsByProperty_success() {

    Property property = property();

    Document doc1 = document("EC", "ACTIVE");
    doc1.setProperty(property);

    Document doc2 = document("SALE_DEED", "ACTIVE");
    doc2.setProperty(property);

    when(propertyRepository.findById(property.getPropertyId()))
            .thenReturn(Optional.of(property));

    when(documentRepository.findByProperty_PropertyId(
            property.getPropertyId()))
            .thenReturn(List.of(doc1, doc2));

    var result =
            service.getDocumentsByProperty(
                    property.getPropertyId());

    assertThat(result).hasSize(2);

    verify(securityService)
            .validatePropertyOwnership(property);
}

@Test
void getDocumentVersions_success() {

    Document document = document("EC", "ACTIVE");
    document.setProperty(property());

    DocumentVersion version =
            new DocumentVersion();

    version.setVersionId(UUID.randomUUID());
    version.setDocument(document);
    version.setVersionNumber(1);
    version.setCurrentVersion(true);

    when(documentRepository.findById(
            document.getDocumentId()))
            .thenReturn(Optional.of(document));

    when(documentVersionRepository
            .findByDocument_DocumentIdOrderByVersionNumberDesc(
                    document.getDocumentId()))
            .thenReturn(List.of(version));

    var result =
            service.getDocumentVersions(
                    document.getDocumentId());

    assertThat(result).hasSize(1);
}



@Test
void getAdminServiceRequest_success() {

    ServiceRequestEntity request =
            adminRequest();

    when(serviceRequestRepository
            .findByIdForAdmin(
                    request.getServiceRequestId()))
            .thenReturn(Optional.of(request));

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    var response =
            service.getAdminServiceRequest(
                    request.getServiceRequestId());

    assertThat(response.getCustomerName())
            .isEqualTo("Test User");

    assertThat(response.getPropertyTitle())
            .isEqualTo("Test Property");
}


@Test
void getMyServiceRequests_success() {

    CustomerEntity customer =
            new CustomerEntity();

    customer.setCustomerId(
            UUID.randomUUID());

    Property property = property();
    property.setTitle("My Plot");

    ServiceRequestEntity request =
            serviceRequest("NEW");

    request.setProperty(property);

    when(securityService.getCurrentCustomer())
            .thenReturn(customer);

    when(serviceRequestRepository
            .findByCustomerWithProperty(
                    customer.getCustomerId()))
            .thenReturn(List.of(request));

    var result =
            service.getMyServiceRequests();

    assertThat(result)
            .hasSize(1);

    assertThat(result.get(0)
            .getPropertyTitle())
            .isEqualTo("My Plot");
}

@Test
void getAllServiceRequestsForAdmin_success() {

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    UserEntity user =
            new UserEntity();

    user.setFullName("Test User");

    CustomerEntity customer =
            new CustomerEntity();

    customer.setCustomerId(
            UUID.randomUUID());

    customer.setUser(user);

    Property property =
            property();

    property.setTitle("Plot A");
    property.setCustomer(customer);

    ServiceRequestEntity request =
            serviceRequest("NEW");

    request.setProperty(property);

    when(serviceRequestRepository
            .findAllForAdminDashboard())
            .thenReturn(List.of(request));

    var result =
            service.getAllServiceRequestsForAdmin();

    assertThat(result)
            .hasSize(1);

    assertThat(result.get(0)
            .getCustomerName())
            .isEqualTo("Test User");
}

@Test
void updateAdminServiceRequestStatus_completed() {

    UUID id = UUID.randomUUID();

    ServiceRequestEntity request =
            serviceRequest("UNDER_REVIEW");

    when(serviceRequestRepository.findById(id))
            .thenReturn(Optional.of(request));

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    AdminServiceRequestStatusUpdateRequest dto =
            new AdminServiceRequestStatusUpdateRequest();

    dto.setStatus("COMPLETED");

    when(serviceRequestRepository.findByIdForAdmin(id))
            .thenReturn(Optional.of(request));

    var response =
            service.updateAdminServiceRequestStatus(
                    id,
                    dto);

    assertThat(response.getStatus())
            .isEqualTo("COMPLETED");

    verify(serviceRequestStatusHistoryRepository)
            .save(any());
}

@Test
void assignServiceRequest_success() {

    UUID serviceRequestId =
            UUID.randomUUID();

    UUID assignedUserId =
            UUID.randomUUID();

    ServiceRequestEntity request =
            adminServiceRequest("PENDING_ASSIGNMENT");

    when(serviceRequestRepository.findById(
            serviceRequestId))
            .thenReturn(Optional.of(request));

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    when(securityService.getCurrentUserId())
            .thenReturn(UUID.randomUUID());

    UserEntity user =
            new UserEntity();

    user.setUserId(assignedUserId);
    user.setFullName("Agent");

    AgentEntity agent =
            new AgentEntity();

    agent.setUser(user);

    when(agentRepository
            .findByUser_UserIdAndStatus(
                    assignedUserId,
                    "ACTIVE"))
            .thenReturn(Optional.of(agent));

    AdminAssignServiceRequestRequest dto =
            new AdminAssignServiceRequestRequest();

    dto.setAssignedTo(assignedUserId);

    when(serviceRequestRepository
            .findByIdForAdmin(serviceRequestId))
            .thenReturn(Optional.of(request));

    service.assignServiceRequest(
            serviceRequestId,
            dto);

    verify(serviceAssignmentRepository)
            .save(any());

    verify(serviceRequestRepository)
            .save(request);
}

@Test
void getAdminServiceRequest_withAssignment() {

    UUID id = UUID.randomUUID();

    UserEntity customerUser =
            new UserEntity();

    customerUser.setFullName("Customer");
    customerUser.setEmail("a@test.com");

    CustomerEntity customer =
            new CustomerEntity();

    customer.setUser(customerUser);

    Property property =
            property();

    property.setCustomer(customer);

    ServiceRequestEntity request =
            serviceRequest("ASSIGNED");

    request.setCustomer(customer);
    request.setProperty(property);

    when(serviceRequestRepository
            .findByIdForAdmin(id))
            .thenReturn(Optional.of(request));

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    UserEntity agentUser =
            new UserEntity();

    agentUser.setUserId(UUID.randomUUID());
    agentUser.setFullName("Agent");

    AgentEntity agent =
            new AgentEntity();

    agent.setUser(agentUser);

    ServiceAssignmentEntity assignment =
            new ServiceAssignmentEntity();

    assignment.setAgent(agent);
    assignment.setStatus("ACTIVE");
    assignment.setAssignedAt(
            java.time.OffsetDateTime.now());

   when(serviceAssignmentRepository
        .findByServiceRequest_ServiceRequestIdAndStatus(
                id,
                "ACTIVE"))
        .thenReturn(Optional.of(assignment));

    var result =
            service.getAdminServiceRequest(id);

    assertThat(result.getAssignedToName())
            .isEqualTo("Agent");
}

private ServiceRequestEntity adminServiceRequest(String status) {

    UserEntity user = new UserEntity();
    user.setFullName("Test User");
    user.setEmail("test@test.com");
    user.setMobileNumber("9999999999");

    CustomerEntity customer = new CustomerEntity();
    customer.setCustomerId(UUID.randomUUID());
    customer.setUser(user);

    Property property = property();
    property.setCustomer(customer);
    property.setTitle("Test Property");

    ServiceRequestEntity request =
            new ServiceRequestEntity();

    request.setServiceRequestId(UUID.randomUUID());
    request.setStatus(status);
    request.setRequestType("DOCUMENT_PROCUREMENT");
    request.setDescription("Document Code: EC");
    request.setCustomer(customer);
    request.setProperty(property);

    return request;
}

@Test
void archiveDocument_success() {

   Property property = new Property();
property.setPropertyId(UUID.randomUUID());

Document document = new Document();
document.setDocumentId(UUID.randomUUID());
document.setStatus("ACTIVE");
document.setProperty(property);
    when(documentRepository.findById(document.getDocumentId()))
            .thenReturn(Optional.of(document));

    when(documentRepository.save(any()))
            .thenAnswer(inv -> inv.getArgument(0));

    var response =
            service.archiveDocument(
                    document.getDocumentId());

    assertThat(response.getStatus())
            .isEqualTo("ARCHIVED");
}
@Test
void archiveDocument_alreadyArchived() {

    Document document = new Document();
    document.setDocumentId(UUID.randomUUID());
    document.setStatus("ARCHIVED");

    when(documentRepository.findById(document.getDocumentId()))
            .thenReturn(Optional.of(document));

    assertThatThrownBy(() ->
            service.archiveDocument(
                    document.getDocumentId()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("already archived");
}

@Test
void restoreDocument_success() {

   Property property = new Property();
property.setPropertyId(UUID.randomUUID());

Document document = new Document();
document.setDocumentId(UUID.randomUUID());
document.setStatus("ARCHIVED");
document.setProperty(property);

    when(documentRepository.findById(document.getDocumentId()))
            .thenReturn(Optional.of(document));

    when(documentRepository.save(any()))
            .thenAnswer(inv -> inv.getArgument(0));

    var response =
            service.restoreDocument(
                    document.getDocumentId());

    assertThat(response.getStatus())
            .isEqualTo("ACTIVE");
}

@Test
void restoreDocument_invalidState() {

    Document document = new Document();
    document.setDocumentId(UUID.randomUUID());
    document.setStatus("ACTIVE");

    when(documentRepository.findById(document.getDocumentId()))
            .thenReturn(Optional.of(document));

    assertThatThrownBy(() ->
            service.restoreDocument(
                    document.getDocumentId()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Only archived");
}

@Test
void extractDocumentCode_branches() throws Exception {

    Method method =
            DocumentServiceImpl.class.getDeclaredMethod(
                    "extractDocumentCode",
                    String.class);

    method.setAccessible(true);

    assertThat(
            method.invoke(service, (String) null))
            .isNull();

    assertThat(
            method.invoke(service, ""))
            .isNull();

    assertThat(
            method.invoke(service, "hello"))
            .isNull();

    assertThat(
            method.invoke(
                    service,
                    "Document Code: EC"))
            .isEqualTo("EC");

    assertThat(
            method.invoke(
                    service,
                    "Document Code: EC | Remarks: Urgent"))
            .isEqualTo("EC");
}
@Test
void validateTransition_allHappyPaths() throws Exception {

    Method method =
            DocumentServiceImpl.class.getDeclaredMethod(
                    "validateTransition",
                    String.class,
                    String.class);

    method.setAccessible(true);

    method.invoke(service, "NEW", "PENDING_PAYMENT");
    method.invoke(service, "NEW", "CANCELLED");
    method.invoke(service, "PENDING_PAYMENT", "PAYMENT_COMPLETED");
    method.invoke(service, "PAYMENT_COMPLETED", "PENDING_ASSIGNMENT");
    method.invoke(service, "PENDING_ASSIGNMENT", "ASSIGNED");
    method.invoke(service, "ASSIGNED", "ACCEPTED");
    method.invoke(service, "ACCEPTED", "IN_PROGRESS");
    method.invoke(service, "IN_PROGRESS", "REPORT_SUBMITTED");
    method.invoke(service, "REPORT_SUBMITTED", "UNDER_REVIEW");
    method.invoke(service, "UNDER_REVIEW", "COMPLETED");
}

@Test
void validateTransition_closedRequest() throws Exception {

    Method method =
            DocumentServiceImpl.class.getDeclaredMethod(
                    "validateTransition",
                    String.class,
                    String.class);

    method.setAccessible(true);

    assertThatThrownBy(() ->
            method.invoke(
                    service,
                    "COMPLETED",
                    "NEW"))
            .hasRootCauseInstanceOf(
                    IllegalStateException.class);

    assertThatThrownBy(() ->
            method.invoke(
                    service,
                    "CANCELLED",
                    "NEW"))
            .hasRootCauseInstanceOf(
                    IllegalStateException.class);
}

@Test
void getServiceRequest_success() {

    ServiceRequestEntity request =
            procurementRequest();

    ServiceRequestStatusHistoryEntity history =
            new ServiceRequestStatusHistoryEntity();

    history.setPreviousStatus("NEW");
    history.setNewStatus("ASSIGNED");

    when(serviceRequestRepository
            .findByIdWithProperty(
                    request.getServiceRequestId()))
            .thenReturn(Optional.of(request));

    when(serviceRequestStatusHistoryRepository
            .findByServiceRequest_ServiceRequestIdOrderByChangedAtDesc(
                    request.getServiceRequestId()))
            .thenReturn(List.of(history));

    var response =
            service.getServiceRequest(
                    request.getServiceRequestId());

    assertThat(response.getStatus())
            .isEqualTo("NEW");

    assertThat(response.getStatusHistory())
            .hasSize(1);
}

private ServiceRequestEntity adminRequest() {

    UserEntity user = new UserEntity();
    user.setFullName("Test User");
    user.setEmail("test@test.com");
    user.setMobileNumber("9999999999");

    CustomerEntity customer =
            new CustomerEntity();
    customer.setCustomerId(UUID.randomUUID());
    customer.setUser(user);

    Property property = property();
    property.setCustomer(customer);
    property.setTitle("Test Property");

    ServiceRequestEntity request =
            new ServiceRequestEntity();

    request.setServiceRequestId(
            UUID.randomUUID());

    request.setCustomer(customer);
    request.setProperty(property);
    request.setStatus("UNDER_REVIEW");
    request.setRequestType("DOCUMENT_PROCUREMENT");

    return request;
}

@Test
void validateTransition_unknownStatus() {

    assertThatThrownBy(() ->
            invokeValidateTransition(
                    "INVALID_STATUS",
                    "NEW"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Unknown current status");
}

@Test
void validateTransition_closedCancelled() {

    assertThatThrownBy(() ->
            invokeValidateTransition(
                    "CANCELLED",
                    "NEW"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("already closed");
}

@Test
void validateTransition_closedCompleted() {

    assertThatThrownBy(() ->
            invokeValidateTransition(
                    "COMPLETED",
                    "NEW"))
            .isInstanceOf(IllegalStateException.class);
}

@Test
void updateDocumentProcurementStatus_notProcurement() {

    UUID id = UUID.randomUUID();

    ServiceRequestEntity request =
            serviceRequest("UNDER_REVIEW");

    request.setRequestType("PROPERTY_VISIT");

    when(serviceRequestRepository.findById(id))
            .thenReturn(Optional.of(request));

    when(securityService.isAdmin())
            .thenReturn(true);

    DocumentProcurementStatusUpdateRequest dto =
            new DocumentProcurementStatusUpdateRequest();

    dto.setStatus("COMPLETED");

    assertThatThrownBy(() ->
            service.updateDocumentProcurementStatus(id, dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Not a document procurement");
}

@Test
void updateDocumentProcurementStatus_blankStatus() {

    UUID id = UUID.randomUUID();

    ServiceRequestEntity request =
            serviceRequest("UNDER_REVIEW");

    when(serviceRequestRepository.findById(id))
            .thenReturn(Optional.of(request));

    when(securityService.isAdmin())
            .thenReturn(true);

    DocumentProcurementStatusUpdateRequest dto =
            new DocumentProcurementStatusUpdateRequest();

    dto.setStatus(" ");

    assertThatThrownBy(() ->
            service.updateDocumentProcurementStatus(id, dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Status is required");
}
@Test
void updateDocumentProcurementStatus_cancelledWithReason() {

    UUID id = UUID.randomUUID();

    ServiceRequestEntity request =
            serviceRequest("UNDER_REVIEW");

    when(serviceRequestRepository.findById(id))
            .thenReturn(Optional.of(request));

    when(securityService.isAdmin())
            .thenReturn(true);

    DocumentProcurementStatusUpdateRequest dto =
            new DocumentProcurementStatusUpdateRequest();

    dto.setStatus("CANCELLED");
    dto.setCancellationReasonCode("CUSTOMER_REQUEST");

    service.updateDocumentProcurementStatus(id, dto);

    assertThat(request.getCancellationReasonCode())
            .isEqualTo("CUSTOMER_REQUEST");
}

@Test
void requestDocumentProcurement_serviceUnavailable() {

    Property property = property();
    property.setStatus("ACTIVE");

    when(propertyRepository.findById(property.getPropertyId()))
            .thenReturn(Optional.of(property));

    DocumentProcurementRequest request =
            new DocumentProcurementRequest();

    request.setDocumentCode("EC");

    DocumentType type = new DocumentType();
    type.setDocumentCode("EC");
    type.setPropertyCategory("PLOT");

    DocumentServiceCatalog catalog =
            new DocumentServiceCatalog();

    catalog.setDocumentCode("EC");
    catalog.setActive(false);
    catalog.setServiceAvailable(false);

    when(documentTypeRepository
            .findByDocumentCodeAndActiveTrue("EC"))
            .thenReturn(Optional.of(type));

    when(documentServiceCatalogRepository
            .findByDocumentCode("EC"))
            .thenReturn(Optional.of(catalog));

assertThatThrownBy(() ->
        service.requestDocumentProcurement(
                property.getPropertyId(),
                request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(
                "Document procurement service not available");
}

@Test
void requestDocumentProcurement_invalidPropertyCategory() {

    Property property = property();
    property.setStatus("ACTIVE");

    when(propertyRepository.findById(property.getPropertyId()))
            .thenReturn(Optional.of(property));

    DocumentProcurementRequest request =
            new DocumentProcurementRequest();

    request.setDocumentCode("EC");

    DocumentType type = new DocumentType();
    type.setDocumentCode("EC");
    type.setPropertyCategory("APARTMENT");

    when(documentTypeRepository
            .findByDocumentCodeAndActiveTrue("EC"))
            .thenReturn(Optional.of(type));

    assertThatThrownBy(() ->
            service.requestDocumentProcurement(
                    property.getPropertyId(),
                    request))
            .isInstanceOf(IllegalArgumentException.class);
}

private void invokeValidateTransition(
        String current,
        String target) throws Exception {

    Method method =
            DocumentServiceImpl.class.getDeclaredMethod(
                    "validateTransition",
                    String.class,
                    String.class);

    method.setAccessible(true);

    try {
        method.invoke(service, current, target);
    } catch (InvocationTargetException e) {
        throw (Exception) e.getCause();
    }
}

@Test
void updateAdminServiceRequestStatus_blankStatus() {

    UUID id = UUID.randomUUID();

    ServiceRequestEntity request =
            serviceRequest("UNDER_REVIEW");

    when(serviceRequestRepository.findById(id))
            .thenReturn(Optional.of(request));

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    AdminServiceRequestStatusUpdateRequest dto =
            new AdminServiceRequestStatusUpdateRequest();

    dto.setStatus(" ");

    assertThatThrownBy(() ->
            service.updateAdminServiceRequestStatus(
                    id,
                    dto))
            .isInstanceOf(
                    IllegalArgumentException.class)
            .hasMessageContaining(
                    "Status is mandatory");
}

@Test
void updateAdminServiceRequestStatus_cancelledWithoutReason() {

    UUID id = UUID.randomUUID();

    ServiceRequestEntity request =
            serviceRequest("UNDER_REVIEW");

    when(serviceRequestRepository.findById(id))
            .thenReturn(Optional.of(request));

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    AdminServiceRequestStatusUpdateRequest dto =
            new AdminServiceRequestStatusUpdateRequest();

    dto.setStatus("CANCELLED");

    assertThatThrownBy(() ->
            service.updateAdminServiceRequestStatus(
                    id,
                    dto))
            .isInstanceOf(
                    IllegalArgumentException.class)
            .hasMessageContaining(
                    "Cancellation reason");
}

@Test
void updateAdminServiceRequestStatus_cancelledSuccess() {

    UUID id = UUID.randomUUID();

    ServiceRequestEntity request =
            adminServiceRequest();

    request.setStatus("UNDER_REVIEW");

    when(serviceRequestRepository.findById(id))
            .thenReturn(Optional.of(request));

    when(serviceRequestRepository
            .findByIdForAdmin(id))
            .thenReturn(Optional.of(request));

    when(serviceRequestStatusHistoryRepository
            .findByServiceRequest_ServiceRequestIdOrderByChangedAtDesc(id))
            .thenReturn(List.of());

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    AdminServiceRequestStatusUpdateRequest dto =
            new AdminServiceRequestStatusUpdateRequest();

    dto.setStatus("CANCELLED");
    dto.setReason("Customer cancelled");

    var response =
            service.updateAdminServiceRequestStatus(
                    id,
                    dto);

    assertThat(response.getStatus())
            .isEqualTo("CANCELLED");

    assertThat(request.getCancellationReasonCode())
            .isEqualTo("Customer cancelled");

    assertThat(request.getCompletedAt())
            .isNotNull();

    verify(serviceRequestStatusHistoryRepository)
            .save(any());
}
@Test
void getAdminServiceRequest_createdByNull() {

    UUID id = UUID.randomUUID();

    ServiceRequestEntity request =
            adminServiceRequest();

    request.setServiceRequestId(id);

    ServiceAssignmentEntity assignment =
            new ServiceAssignmentEntity();

    assignment.setStatus("ACTIVE");

    assignment.setAssignedAt(
        OffsetDateTime.now());

    assignment.setCreatedBy(null);

    AgentEntity agent = new AgentEntity();

    UserEntity user = new UserEntity();
    user.setUserId(UUID.randomUUID());
    user.setFullName("Agent One");

    agent.setUser(user);

    assignment.setAgent(agent);

    when(serviceRequestRepository
            .findByIdForAdmin(id))
            .thenReturn(Optional.of(request));

    when(serviceAssignmentRepository
            .findByServiceRequest_ServiceRequestIdAndStatus(
                    id,
                    "ACTIVE"))
            .thenReturn(Optional.of(assignment));

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    var response =
            service.getAdminServiceRequest(id);

    assertThat(response.getAssignedToName())
            .isEqualTo("Agent One");

    assertThat(response.getAssignedByName())
            .isNull();
}

private ServiceRequestEntity adminServiceRequest() {

    ServiceRequestEntity request =
            new ServiceRequestEntity();

    request.setServiceRequestId(
            UUID.randomUUID());

    request.setStatus("UNDER_REVIEW");

    request.setRequestType(
            "DOCUMENT_PROCUREMENT");

    request.setDescription(
            "Document Code: EC");

    request.setProperty(property());

    CustomerEntity customer =
            new CustomerEntity();

    customer.setCustomerId(
            UUID.randomUUID());

    UserEntity user =
            new UserEntity();

    user.setFullName("Test User");
    user.setEmail("test@test.com");
    user.setMobileNumber("9999999999");

    customer.setUser(user);

    request.setCustomer(customer);

    return request;
}

@Test
void assignServiceRequest_assignedUserMissing() {

    UUID requestId = UUID.randomUUID();

    ServiceRequestEntity request =
            adminServiceRequest();

    request.setStatus("PENDING_ASSIGNMENT");

    when(serviceRequestRepository.findById(requestId))
            .thenReturn(Optional.of(request));

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    AdminAssignServiceRequestRequest dto =
            new AdminAssignServiceRequestRequest();

    assertThatThrownBy(() ->
            service.assignServiceRequest(
                    requestId,
                    dto))
            .isInstanceOf(
                    IllegalArgumentException.class)
            .hasMessageContaining(
                    "Assigned user is required");
}
@Test
void updateAdminServiceRequestStatus_sameStatus() {

    UUID id = UUID.randomUUID();

    ServiceRequestEntity request =
            serviceRequest("ASSIGNED");

    when(serviceRequestRepository.findById(id))
            .thenReturn(Optional.of(request));

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    AdminServiceRequestStatusUpdateRequest dto =
            new AdminServiceRequestStatusUpdateRequest();

    dto.setStatus("ASSIGNED");

    assertThatThrownBy(() ->
            service.updateAdminServiceRequestStatus(id, dto))
            .isInstanceOf(IllegalStateException.class);
}

@Test
void updateAdminServiceRequestStatus_nullStatus() {

    UUID id = UUID.randomUUID();

    ServiceRequestEntity request =
            serviceRequest("ASSIGNED");

    when(serviceRequestRepository.findById(id))
            .thenReturn(Optional.of(request));

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    AdminServiceRequestStatusUpdateRequest dto =
            new AdminServiceRequestStatusUpdateRequest();

    assertThatThrownBy(() ->
            service.updateAdminServiceRequestStatus(id, dto))
            .isInstanceOf(IllegalArgumentException.class);
}

@Test
void assignServiceRequest_missingUser() {

    UUID id = UUID.randomUUID();

    ServiceRequestEntity request =
            serviceRequest("PENDING_ASSIGNMENT");

    when(serviceRequestRepository.findById(id))
            .thenReturn(Optional.of(request));

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    AdminAssignServiceRequestRequest dto =
            new AdminAssignServiceRequestRequest();

    assertThatThrownBy(() ->
            service.assignServiceRequest(id, dto))
            .isInstanceOf(IllegalArgumentException.class);
}

@Test
void assignServiceRequest_agentNotFound() {

    UUID id = UUID.randomUUID();

    ServiceRequestEntity request =
            serviceRequest("PENDING_ASSIGNMENT");

    when(serviceRequestRepository.findById(id))
            .thenReturn(Optional.of(request));

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    UUID userId = UUID.randomUUID();

    AdminAssignServiceRequestRequest dto =
            new AdminAssignServiceRequestRequest();

    dto.setAssignedTo(userId);

    when(agentRepository
            .findByUser_UserIdAndStatus(userId, "ACTIVE"))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.assignServiceRequest(id, dto))
            .isInstanceOf(RuntimeException.class);
}

@Test
void requestDocumentProcurement_documentTypeNotFound() {

    Property property = property();
    property.setStatus("ACTIVE");

    when(propertyRepository.findById(property.getPropertyId()))
            .thenReturn(Optional.of(property));

    DocumentProcurementRequest dto =
            new DocumentProcurementRequest();

    dto.setDocumentCode("XYZ");

    when(documentTypeRepository
            .findByDocumentCodeAndActiveTrue("XYZ"))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.requestDocumentProcurement(
                    property.getPropertyId(),
                    dto))
            .isInstanceOf(RuntimeException.class);
}

@Test
void requestDocumentProcurement_catalogMissing() {

    Property property = property();
    property.setStatus("ACTIVE");

    when(propertyRepository.findById(property.getPropertyId()))
            .thenReturn(Optional.of(property));

    DocumentType type = new DocumentType();
    type.setDocumentCode("EC");
    type.setPropertyCategory("PLOT");

    when(documentTypeRepository
            .findByDocumentCodeAndActiveTrue("EC"))
            .thenReturn(Optional.of(type));

    when(documentServiceCatalogRepository
            .findByDocumentCode("EC"))
            .thenReturn(Optional.empty());

    DocumentProcurementRequest dto =
            new DocumentProcurementRequest();
    dto.setDocumentCode("EC");

    assertThatThrownBy(() ->
            service.requestDocumentProcurement(
                    property.getPropertyId(),
                    dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining(
                    "PropertyPilot cannot procure document");
}

@Test
void documentChecklist_commonPropertyType() {

    Property property = property();

    property.setPropertyType(null);

    when(propertyRepository.findById(property.getPropertyId()))
            .thenReturn(Optional.of(property));

    var response =
            service.getDocumentChecklist(
                    property.getPropertyId());

    assertThat(response).isNotNull();
}

@Test
void restoreDocument_alreadyActive() {

    Document document =
            document("SALE_DEED", "ACTIVE");

    document.setProperty(property());

    when(documentRepository.findById(document.getDocumentId()))
            .thenReturn(Optional.of(document));

    assertThatThrownBy(() ->
            service.restoreDocument(
                    document.getDocumentId()))
            .isInstanceOf(IllegalStateException.class);
}

@Test
void getAdminDashboard_empty() {

    when(serviceRequestRepository.findAll())
            .thenReturn(List.of());

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    var response =
            service.getAdminDashboard();

    assertThat(response.getTotalRequests())
            .isZero();
}

private String mapPropertyCategory(String type) {
    if ("PLOT".equalsIgnoreCase(type)) {
        return "PLOT";
    }
    if ("AGRICULTURAL_LAND".equalsIgnoreCase(type)) {
        return "AGRICULTURAL_LAND";
    }
    if ("HOUSE".equalsIgnoreCase(type)) {
        return "HOUSE";
        
    }

    return "COMMON";
}

@Test
void requestDocumentProcurement_blankCode() {

    Property property = property();
    property.setStatus("ACTIVE");

    when(propertyRepository.findById(
            property.getPropertyId()))
            .thenReturn(Optional.of(property));

    DocumentProcurementRequest dto =
            new DocumentProcurementRequest();

    dto.setDocumentCode(" ");

    assertThatThrownBy(() ->
            service.requestDocumentProcurement(
                    property.getPropertyId(),
                    dto))
            .isInstanceOf(
                    IllegalArgumentException.class);
}

@Test
void requestDocumentProcurement_propertyCategoryMismatch() {

    Property property = property();
    property.setStatus("ACTIVE");
    property.setPropertyType("HOUSE");

    when(propertyRepository.findById(
            property.getPropertyId()))
            .thenReturn(Optional.of(property));

    DocumentType type =
            new DocumentType();

    type.setDocumentCode("EC");
    type.setPropertyCategory("PLOT");

    when(documentTypeRepository
            .findByDocumentCodeAndActiveTrue("EC"))
            .thenReturn(Optional.of(type));

    DocumentProcurementRequest dto =
            new DocumentProcurementRequest();

    dto.setDocumentCode("EC");

    assertThatThrownBy(() ->
            service.requestDocumentProcurement(
                    property.getPropertyId(),
                    dto))
            .isInstanceOf(
                    IllegalArgumentException.class);
}
@Test
void getAdminServiceRequest_noAssignment() {

    UUID id = UUID.randomUUID();

    ServiceRequestEntity request =
            serviceRequest("NEW");

    UserEntity user =
            new UserEntity();

    user.setFullName("John");

    CustomerEntity customer =
            new CustomerEntity();

    customer.setCustomerId(
            UUID.randomUUID());

    customer.setUser(user);

    request.setCustomer(customer);

    Property property =
            property();

    property.setTitle("Plot");

    request.setProperty(property);

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    when(serviceRequestRepository
            .findByIdForAdmin(id))
            .thenReturn(Optional.of(request));

    when(serviceAssignmentRepository
            .findByServiceRequest_ServiceRequestIdAndStatus(
                    id,
                    "ACTIVE"))
            .thenReturn(Optional.empty());

    var response =
            service.getAdminServiceRequest(id);

    assertThat(response)
            .isNotNull();

    assertThat(response.getAssignedToUserId())
            .isNull();
}

@Test
void getAdminServiceRequest_assignmentWithoutAgent() {

    UUID id = UUID.randomUUID();

    ServiceRequestEntity request =
            serviceRequest("ASSIGNED");

    UserEntity user =
            new UserEntity();

    user.setFullName("John");

    CustomerEntity customer =
            new CustomerEntity();

    customer.setCustomerId(
            UUID.randomUUID());

    customer.setUser(user);

    request.setCustomer(customer);

    Property property =
            property();

    property.setTitle("Plot");

    request.setProperty(property);

    ServiceAssignmentEntity assignment =
            new ServiceAssignmentEntity();

    assignment.setAssignedAt(
            OffsetDateTime.now());
            UserEntity agentUser =
        new UserEntity();

agentUser.setUserId(
        UUID.randomUUID());

agentUser.setFullName(
        "Agent John");

AgentEntity agent =
        new AgentEntity();

agent.setUser(
        agentUser);

assignment.setAgent(
        agent);

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    when(serviceRequestRepository
            .findByIdForAdmin(id))
            .thenReturn(Optional.of(request));

    when(serviceAssignmentRepository
            .findByServiceRequest_ServiceRequestIdAndStatus(
                    id,
                    "ACTIVE"))
            .thenReturn(Optional.of(assignment));

    var response =
            service.getAdminServiceRequest(id);

    assertThat(response)
            .isNotNull();

    assertThat(response.getAssignedAt())
            .isNotNull();
}

@Test
void validateTransition_allCancelPaths() throws Exception {

    invokeValidateTransition(
            "PENDING_PAYMENT",
            "CANCELLED");

    invokeValidateTransition(
            "PAYMENT_COMPLETED",
            "CANCELLED");

    invokeValidateTransition(
            "PENDING_ASSIGNMENT",
            "CANCELLED");

    invokeValidateTransition(
            "ASSIGNED",
            "CANCELLED");

    invokeValidateTransition(
            "ACCEPTED",
            "CANCELLED");

    invokeValidateTransition(
            "IN_PROGRESS",
            "CANCELLED");

    invokeValidateTransition(
            "UNDER_REVIEW",
            "CANCELLED");
}

@Test
void allowTransition_invalidTarget() throws Exception {

    Method method =
            DocumentServiceImpl.class
                    .getDeclaredMethod(
                            "allowTransition",
                            String.class,
                            String[].class);

    method.setAccessible(true);

    assertThatThrownBy(() ->
            method.invoke(
                    service,
                    "INVALID",
                    new String[]{
                            "NEW",
                            "COMPLETED"
                    }))
            .hasRootCauseInstanceOf(
                    IllegalStateException.class);
}

@Test
void createStatusHistory_populatesFields()
        throws Exception {

    ServiceRequestEntity request =
            serviceRequest("NEW");

    Method method =
            DocumentServiceImpl.class
                    .getDeclaredMethod(
                            "createStatusHistory",
                            ServiceRequestEntity.class,
                            String.class,
                            String.class,
                            String.class);

    method.setAccessible(true);

    method.invoke(
            service,
            request,
            "NEW",
            "ASSIGNED",
            "Assigned");

    ArgumentCaptor<ServiceRequestStatusHistoryEntity>
            captor =
            ArgumentCaptor.forClass(
                    ServiceRequestStatusHistoryEntity.class);

    verify(
            serviceRequestStatusHistoryRepository)
            .save(captor.capture());

    assertThat(
            captor.getValue()
                    .getPreviousStatus())
            .isEqualTo("NEW");
}

@Test
void getDocumentProcurementRequest_noDocumentCode() {

    ServiceRequestEntity request =
            procurementRequest();

    request.setDescription(
            "Random Description");

    when(serviceRequestRepository
            .findByIdWithProperty(
                    request.getServiceRequestId()))
            .thenReturn(Optional.of(request));

    var response =
            service.getDocumentProcurementRequest(
                    request.getServiceRequestId());

    assertThat(
            response.getDocumentCode())
            .isNull();
}

@Test
void getDocumentProcurementHistory_empty() {

    ServiceRequestEntity request =
            procurementRequest();

    when(serviceRequestRepository
            .findByIdWithProperty(
                    request.getServiceRequestId()))
            .thenReturn(Optional.of(request));

    when(serviceRequestStatusHistoryRepository
            .findByServiceRequest_ServiceRequestIdOrderByChangedAtDesc(
                    request.getServiceRequestId()))
            .thenReturn(List.of());

    var result =
            service.getDocumentProcurementHistory(
                    request.getServiceRequestId());

    assertThat(result)
            .isEmpty();
}

@Test
void getMyServiceRequests_empty() {

    CustomerEntity customer =
            new CustomerEntity();

    customer.setCustomerId(
            UUID.randomUUID());

    when(securityService
            .getCurrentCustomer())
            .thenReturn(customer);

    when(serviceRequestRepository
            .findByCustomerWithProperty(
                    customer.getCustomerId()))
            .thenReturn(List.of());

    assertThat(
            service.getMyServiceRequests())
            .isEmpty();
}

@Test
void getAllServiceRequestsForAdmin_empty() {

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    when(serviceRequestRepository
            .findAllForAdminDashboard())
            .thenReturn(List.of());

    assertThat(
            service.getAllServiceRequestsForAdmin())
            .isEmpty();
}

@Test
void getAdminServiceRequest_assignedByFound() {

    UUID requestId = UUID.randomUUID();
    UUID adminId = UUID.randomUUID();

    ServiceRequestEntity request =
            adminServiceRequest();

    request.setServiceRequestId(requestId);

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    when(serviceRequestRepository
            .findByIdForAdmin(requestId))
            .thenReturn(Optional.of(request));

    UserEntity agentUser =
            new UserEntity();

    agentUser.setUserId(UUID.randomUUID());
    agentUser.setFullName("Agent One");

    AgentEntity agent =
            new AgentEntity();

    agent.setUser(agentUser);

    ServiceAssignmentEntity assignment =
            new ServiceAssignmentEntity();

    assignment.setStatus("ACTIVE");
    assignment.setAssignedAt(
            OffsetDateTime.now());

    assignment.setAgent(agent);

    assignment.setCreatedBy(adminId);

    when(serviceAssignmentRepository
            .findByServiceRequest_ServiceRequestIdAndStatus(
                    requestId,
                    "ACTIVE"))
            .thenReturn(Optional.of(assignment));

    UserEntity adminUser =
            new UserEntity();

    adminUser.setUserId(adminId);
    adminUser.setFullName("Admin User");

    when(userRepository.findById(adminId))
            .thenReturn(Optional.of(adminUser));

    AdminServiceRequestDetailsResponse response =
            service.getAdminServiceRequest(
                    requestId);

    assertThat(response.getAssignedByName())
            .isEqualTo("Admin User");

    assertThat(response.getAssignedToName())
            .isEqualTo("Agent One");
}
@Test
void assignServiceRequest_invalidTransition() {

    UUID id =
            UUID.randomUUID();

    ServiceRequestEntity request =
            serviceRequest("NEW");

    when(serviceRequestRepository
            .findById(id))
            .thenReturn(Optional.of(request));

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    AdminAssignServiceRequestRequest dto =
            new AdminAssignServiceRequestRequest();

    dto.setAssignedTo(
            UUID.randomUUID());

    assertThatThrownBy(() ->
            service.assignServiceRequest(
                    id,
                    dto))
            .isInstanceOf(
                    IllegalStateException.class);
}


@Test
void assignServiceRequest_notFound() {

    UUID id =
            UUID.randomUUID();

    when(serviceRequestRepository
            .findById(id))
            .thenReturn(Optional.empty());

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    assertThatThrownBy(() ->
            service.assignServiceRequest(
                    id,
                    new AdminAssignServiceRequestRequest()))
            .isInstanceOf(
                    ResourceNotFoundException.class);
}

@Test
void requestDocumentProcurement_agriculturalLandMatch() {

    Property property = property();

    property.setStatus("ACTIVE");
    property.setPropertyType(
            "AGRICULTURAL_LAND");

}
@Test
void updateAdminServiceRequestStatus_notFound() {

    UUID id = UUID.randomUUID();

    when(serviceRequestRepository.findById(id))
            .thenReturn(Optional.empty());

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    assertThatThrownBy(() ->
            service.updateAdminServiceRequestStatus(
                    id,
                    new AdminServiceRequestStatusUpdateRequest()))
            .isInstanceOf(
                    ResourceNotFoundException.class);
}
@Test
void assignServiceRequest_requestNotFound() {

    UUID id = UUID.randomUUID();

    when(serviceRequestRepository.findById(id))
            .thenReturn(Optional.empty());

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    assertThatThrownBy(() ->
            service.assignServiceRequest(
                    id,
                    new AdminAssignServiceRequestRequest()))
            .isInstanceOf(
                    ResourceNotFoundException.class);
}


@Test
void createDocumentVersion_existingCurrentVersion() {

    UUID documentId = UUID.randomUUID();

    Document document =
            document("SALE_DEED", "ACTIVE");

    document.setProperty(property());

    DocumentVersion current =
            new DocumentVersion();

    current.setVersionId(UUID.randomUUID());
    current.setVersionNumber(1);
    current.setCurrentVersion(true);

    when(documentRepository.findById(documentId))
            .thenReturn(Optional.of(document));

    when(documentVersionRepository
            .findByDocument_DocumentIdAndCurrentVersionTrue(
                    documentId))
            .thenReturn(Optional.of(current));

    when(documentVersionRepository
            .findByDocument_DocumentIdOrderByVersionNumberDesc(
                    documentId))
            .thenReturn(List.of(current));

    when(documentVersionRepository.save(
            any(DocumentVersion.class)))
            .thenAnswer(inv -> {

                DocumentVersion v =
                        inv.getArgument(0);

                if (v.getVersionId() == null) {
                    v.setVersionId(
                            UUID.randomUUID());
                }

                return v;
            });

    DocumentVersionCreateRequest request =
            new DocumentVersionCreateRequest();

    request.setFileName("v2.pdf");
    request.setFilePath("/tmp/v2.pdf");
    request.setMimeType("application/pdf");
    request.setFileSize(100L);

    var response =
            service.createDocumentVersion(
                    documentId,
                    request);

    assertThat(response.getVersionNumber())
            .isEqualTo(2);

    assertThat(current.getCurrentVersion())
            .isFalse();
}

@Test
void mapPropertyCategory_allBranches() throws Exception {

    Method method =
            DocumentServiceImpl.class
                    .getDeclaredMethod(
                            "mapPropertyCategory",
                            String.class);

    method.setAccessible(true);

    assertThat(method.invoke(service, "PLOT"))
            .isEqualTo("PLOT");

   assertThat(method.invoke(service,
        "AGRICULTURAL"))
        .isEqualTo("AGRICULTURAL");

    assertThat(method.invoke(service,
            "HOUSE"))
            .isEqualTo("HOUSE");

    assertThat(method.invoke(service,
            "UNKNOWN"))
            .isEqualTo("COMMON");

    assertThat(method.invoke(
            service,
            new Object[]{null}))
            .isEqualTo("COMMON");
}

@Test
void createDocument_documentTypeNotFound() {

    UUID propertyId =
            UUID.randomUUID();

    Property property =
            property();

    property.setStatus("ACTIVE");

    when(propertyRepository.findById(propertyId))
            .thenReturn(Optional.of(property));

    when(documentTypeRepository
            .findByDocumentCodeAndActiveTrue(
                    "SALE_DEED"))
            .thenReturn(Optional.empty());

    DocumentCreateRequest request =
            new DocumentCreateRequest();

    request.setDocumentType(
            "SALE_DEED");

    assertThatThrownBy(() ->
            service.createDocument(
                    propertyId,
                    request))
            .isInstanceOf(
                    RuntimeException.class);
}

@Test
void getAdminServiceRequest_createdByUserMissing() {

    UUID id = UUID.randomUUID();

    ServiceRequestEntity request =
            adminServiceRequest();

    ServiceAssignmentEntity assignment =
            new ServiceAssignmentEntity();

    assignment.setStatus("ACTIVE");
    assignment.setAssignedAt(
            OffsetDateTime.now());

    UUID createdBy =
            UUID.randomUUID();

    assignment.setCreatedBy(createdBy);

    AgentEntity agent =
            new AgentEntity();

    UserEntity agentUser =
            new UserEntity();

    agentUser.setUserId(UUID.randomUUID());

    agent.setUser(agentUser);

    assignment.setAgent(agent);

    when(serviceRequestRepository
            .findByIdForAdmin(id))
            .thenReturn(Optional.of(request));

    when(serviceAssignmentRepository
            .findByServiceRequest_ServiceRequestIdAndStatus(
                    id,
                    "ACTIVE"))
            .thenReturn(Optional.of(assignment));

    when(userRepository.findById(createdBy))
            .thenReturn(Optional.empty());

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    var response =
            service.getAdminServiceRequest(id);

    assertThat(response.getAssignedByName())
            .isNull();
}

@Test
void mapPropertyCategory_flatAndCommercial()
        throws Exception {

    Method method =
            DocumentServiceImpl.class
                    .getDeclaredMethod(
                            "mapPropertyCategory",
                            String.class);

    method.setAccessible(true);

    assertThat(
            method.invoke(
                    service,
                    "APARTMENT"))
            .isEqualTo("FLAT");

    assertThat(
            method.invoke(
                    service,
                    "FLAT"))
            .isEqualTo("FLAT");

    assertThat(
            method.invoke(
                    service,
                    "COMMERCIAL"))
            .isEqualTo("COMMERCIAL");

    assertThat(
            method.invoke(
                    service,
                    "COMMERCIAL BUILDING"))
            .isEqualTo("COMMERCIAL");
}

@Test
void createDocumentVersion_archivedProperty() {

    UUID documentId = UUID.randomUUID();

    Property property = property();
    property.setStatus("ARCHIVED");

    Document document =
            document("SALE_DEED",
                    "ACTIVE");

    document.setProperty(property);

    when(documentRepository.findById(
            documentId))
            .thenReturn(Optional.of(document));

    DocumentVersionCreateRequest request =
            new DocumentVersionCreateRequest();

    request.setFileName("test.pdf");

    assertThatThrownBy(() ->
            service.createDocumentVersion(
                    documentId,
                    request))
            .isInstanceOf(
                    IllegalStateException.class)
            .hasMessageContaining(
                    "Cannot upload version");
}

@Test
void requestDocumentProcurement_withoutRemarks() {

    Property property = property();
    property.setStatus("ACTIVE");

    when(propertyRepository.findById(
            property.getPropertyId()))
            .thenReturn(Optional.of(property));

    DocumentType type =
            new DocumentType();

    type.setDocumentCode("EC");
    type.setPropertyCategory("PLOT");

    when(documentTypeRepository
            .findByDocumentCodeAndActiveTrue("EC"))
            .thenReturn(Optional.of(type));

    DocumentServiceCatalog catalog =
            new DocumentServiceCatalog();

    catalog.setActive(true);
    catalog.setServiceAvailable(true);

    when(documentServiceCatalogRepository
            .findByDocumentCode("EC"))
            .thenReturn(Optional.of(catalog));

    when(serviceRequestRepository.save(any()))
            .thenAnswer(i -> i.getArgument(0));

    DocumentProcurementRequest request =
            new DocumentProcurementRequest();

    request.setDocumentCode("EC");
    request.setRemarks(null);

    service.requestDocumentProcurement(
            property.getPropertyId(),
            request);

    verify(serviceRequestRepository)
            .save(any());
}
@Test
void getAdminServiceRequest_emptyHistory() {

    UUID id = UUID.randomUUID();

    ServiceRequestEntity request =
            adminServiceRequest();

    when(serviceRequestRepository
            .findByIdForAdmin(id))
            .thenReturn(Optional.of(request));

    when(serviceRequestStatusHistoryRepository
            .findByServiceRequest_ServiceRequestIdOrderByChangedAtDesc(id))
            .thenReturn(List.of());

    when(serviceAssignmentRepository
            .findByServiceRequest_ServiceRequestIdAndStatus(
                    id,
                    "ACTIVE"))
            .thenReturn(Optional.empty());

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    var response =
            service.getAdminServiceRequest(id);

    assertThat(response.getStatusHistory())
            .isEmpty();
}

@Test
void requestDocumentProcurement_propertyNotFound() {

    UUID propertyId = UUID.randomUUID();

    when(propertyRepository.findById(propertyId))
            .thenReturn(Optional.empty());

    DocumentProcurementRequest dto =
            new DocumentProcurementRequest();

    dto.setDocumentCode("EC");

    assertThatThrownBy(() ->
            service.requestDocumentProcurement(
                    propertyId,
                    dto))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Property not found");
}

@Test
void requestDocumentProcurement_catalogActiveAndAvailable() {

    Property property = property();
    property.setStatus("ACTIVE");

    when(propertyRepository.findById(
            property.getPropertyId()))
            .thenReturn(Optional.of(property));

    DocumentType type = new DocumentType();
    type.setDocumentCode("EC");
    type.setPropertyCategory("PLOT");

    DocumentServiceCatalog catalog =
            new DocumentServiceCatalog();

    catalog.setDocumentCode("EC");
    catalog.setServiceAvailable(true);
    catalog.setActive(true);

    when(documentTypeRepository
            .findByDocumentCodeAndActiveTrue("EC"))
            .thenReturn(Optional.of(type));

    when(documentServiceCatalogRepository
            .findByDocumentCode("EC"))
            .thenReturn(Optional.of(catalog));

    when(serviceRequestRepository.save(any()))
            .thenAnswer(i -> i.getArgument(0));

    DocumentProcurementRequest dto =
            new DocumentProcurementRequest();

    dto.setDocumentCode("EC");

    service.requestDocumentProcurement(
            property.getPropertyId(),
            dto);

    verify(serviceRequestRepository)
            .save(any());
}

@Test
void createDocumentVersion_documentArchived() {

    UUID documentId = UUID.randomUUID();

    Document document =
            document("SALE_DEED", "ARCHIVED");

    document.setProperty(property());

    when(documentRepository.findById(documentId))
            .thenReturn(Optional.of(document));

    DocumentVersionCreateRequest dto =
            new DocumentVersionCreateRequest();

    assertThatThrownBy(() ->
            service.createDocumentVersion(
                    documentId,
                    dto))
            .isInstanceOf(
                    IllegalStateException.class)
            .hasMessageContaining(
                    "Cannot upload version");
}

@Test
void createDocumentVersion_propertyArchived() {

    UUID documentId = UUID.randomUUID();

    Property property = property();
    property.setStatus("ARCHIVED");

    Document document =
            document("SALE_DEED", "ACTIVE");

    document.setProperty(property);

    when(documentRepository.findById(documentId))
            .thenReturn(Optional.of(document));

    DocumentVersionCreateRequest dto =
            new DocumentVersionCreateRequest();

    assertThatThrownBy(() ->
            service.createDocumentVersion(
                    documentId,
                    dto))
            .isInstanceOf(
                    IllegalStateException.class);
}

@Test
void requestDocumentProcurement_withRemarks() {

    Property property = property();
    property.setStatus("ACTIVE");

    when(propertyRepository.findById(
            property.getPropertyId()))
            .thenReturn(Optional.of(property));

    DocumentType type = new DocumentType();
    type.setDocumentCode("EC");
    type.setPropertyCategory("PLOT");

    DocumentServiceCatalog catalog =
            new DocumentServiceCatalog();

    catalog.setDocumentCode("EC");
    catalog.setServiceAvailable(true);
    catalog.setActive(true);

    when(documentTypeRepository
            .findByDocumentCodeAndActiveTrue("EC"))
            .thenReturn(Optional.of(type));

    when(documentServiceCatalogRepository
            .findByDocumentCode("EC"))
            .thenReturn(Optional.of(catalog));

    when(serviceRequestRepository.save(any()))
            .thenAnswer(i -> i.getArgument(0));

    DocumentProcurementRequest dto =
            new DocumentProcurementRequest();

    dto.setDocumentCode("EC");
    dto.setRemarks("Urgent");

    service.requestDocumentProcurement(
            property.getPropertyId(),
            dto);

    ArgumentCaptor<ServiceRequestEntity> captor =
            ArgumentCaptor.forClass(
                    ServiceRequestEntity.class);

    verify(serviceRequestRepository)
            .save(captor.capture());

    assertThat(captor.getValue()
            .getDescription())
            .contains("Urgent");
}

@Test
void getAdminServiceRequest_assignedByUserFound() {

    UUID id = UUID.randomUUID();
    UUID adminId = UUID.randomUUID();

    ServiceRequestEntity request =
            adminServiceRequest();

    request.setServiceRequestId(id);

    UserEntity adminUser =
            new UserEntity();

    adminUser.setUserId(adminId);
    adminUser.setFullName("Admin User");

    ServiceAssignmentEntity assignment =
            new ServiceAssignmentEntity();

    assignment.setStatus("ACTIVE");

    assignment.setAssignedAt(
            OffsetDateTime.now());

    assignment.setCreatedBy(adminId);

    AgentEntity agent =
            new AgentEntity();

    UserEntity agentUser =
            new UserEntity();

    agentUser.setUserId(
            UUID.randomUUID());

    agentUser.setFullName(
            "Agent");

    agent.setUser(agentUser);

    assignment.setAgent(agent);

    when(serviceRequestRepository
            .findByIdForAdmin(id))
            .thenReturn(Optional.of(request));

    when(serviceAssignmentRepository
            .findByServiceRequest_ServiceRequestIdAndStatus(
                    id,
                    "ACTIVE"))
            .thenReturn(Optional.of(assignment));

    when(userRepository.findById(adminId))
            .thenReturn(Optional.of(adminUser));

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    var response =
            service.getAdminServiceRequest(id);

    assertThat(response)
            .isNotNull();

    assertThat(response.getAssignedByName())
            .isEqualTo("Admin User");

    assertThat(response.getAssignedToName())
            .isEqualTo("Agent");

    assertThat(response.getAssignedAt())
            .isNotNull();
}
@Test
void requestDocumentProcurement_commonCategoryAllowed() {

    Property property = property();
    property.setStatus("ACTIVE");
    property.setPropertyType("ANYTHING");

    when(propertyRepository.findById(
            property.getPropertyId()))
            .thenReturn(Optional.of(property));

    DocumentType type =
            new DocumentType();

    type.setDocumentCode("EC");
    type.setPropertyCategory("COMMON");

    when(documentTypeRepository
            .findByDocumentCodeAndActiveTrue("EC"))
            .thenReturn(Optional.of(type));

    DocumentServiceCatalog catalog =
            new DocumentServiceCatalog();

    catalog.setActive(true);
    catalog.setServiceAvailable(true);

    when(documentServiceCatalogRepository
            .findByDocumentCode("EC"))
            .thenReturn(Optional.of(catalog));

    when(serviceRequestRepository.save(any()))
            .thenAnswer(i -> i.getArgument(0));

    DocumentProcurementRequest dto =
            new DocumentProcurementRequest();

    dto.setDocumentCode("EC");

    service.requestDocumentProcurement(
            property.getPropertyId(),
            dto);

    verify(serviceRequestRepository)
            .save(any());
}

@Test
void updateDocumentProcurementStatus_requestStatusNull() {

    UUID id = UUID.randomUUID();

    ServiceRequestEntity request =
            serviceRequest(null);

    when(serviceRequestRepository.findById(id))
            .thenReturn(Optional.of(request));

    when(securityService.isAdmin())
            .thenReturn(true);

    DocumentProcurementStatusUpdateRequest dto =
            new DocumentProcurementStatusUpdateRequest();

    dto.setStatus("COMPLETED");

    assertThatThrownBy(() ->
            service.updateDocumentProcurementStatus(id, dto))
            .isInstanceOf(NullPointerException.class);
}

@Test
void getServiceRequest_historyMapping() {

    ServiceRequestEntity request =
            procurementRequest();

    ServiceRequestStatusHistoryEntity history =
            new ServiceRequestStatusHistoryEntity();

    history.setPreviousStatus("NEW");
    history.setNewStatus("ASSIGNED");
    history.setChangeReason("Auto");
    history.setChangedAt(
        OffsetDateTime.now().toInstant());

    when(serviceRequestRepository
            .findByIdWithProperty(
                    request.getServiceRequestId()))
            .thenReturn(Optional.of(request));

    when(serviceRequestStatusHistoryRepository
            .findByServiceRequest_ServiceRequestIdOrderByChangedAtDesc(
                    request.getServiceRequestId()))
            .thenReturn(List.of(history));

    var response =
            service.getServiceRequest(
                    request.getServiceRequestId());

    assertThat(response.getStatusHistory())
            .hasSize(1);

    assertThat(response.getStatusHistory().get(0)
            .getChangeReason())
            .isEqualTo("Auto");
}
@Test
void getDocumentChecklist_propertyNotFound() {

    UUID id = UUID.randomUUID();

    when(propertyRepository.findById(id))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.getDocumentChecklist(id))
            .isInstanceOf(ResourceNotFoundException.class);
}

@Test
void getDocumentsByProperty_propertyNotFound() {

    UUID id = UUID.randomUUID();

    when(propertyRepository.findById(id))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.getDocumentsByProperty(id))
            .isInstanceOf(ResourceNotFoundException.class);
}

@Test
void getDocument_notFound() {

    UUID id = UUID.randomUUID();

    when(documentRepository.findById(id))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.getDocument(id))
            .isInstanceOf(ResourceNotFoundException.class);
}
@Test
void createDocumentVersion_documentNotFound() {

    UUID id = UUID.randomUUID();

    when(documentRepository.findById(id))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.createDocumentVersion(
                    id,
                    new DocumentVersionCreateRequest()))
            .isInstanceOf(ResourceNotFoundException.class);
}

@Test
void archiveDocument_notFound() {

    UUID id = UUID.randomUUID();

    when(documentRepository.findById(id))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.archiveDocument(id))
            .isInstanceOf(ResourceNotFoundException.class);
}

@Test
void restoreDocument_notFound() {

    UUID id = UUID.randomUUID();

    when(documentRepository.findById(id))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.restoreDocument(id))
            .isInstanceOf(ResourceNotFoundException.class);
}
@Test
void getAdminServiceRequest_notFound() {

    UUID id = UUID.randomUUID();

    doNothing()
            .when(securityService)
            .validateAdminAccess();

    when(serviceRequestRepository.findByIdForAdmin(id))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.getAdminServiceRequest(id))
            .isInstanceOf(ResourceNotFoundException.class);
}


}
