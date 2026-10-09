package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateEvidenceRequest;
import com.propertypilot.infrastructure.persistence.entity.EvidenceEntity;
import com.propertypilot.infrastructure.persistence.entity.EvidenceTypeEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.entity.VisitEntity;
import com.propertypilot.infrastructure.persistence.repository.EvidenceRepository;
import com.propertypilot.infrastructure.persistence.repository.EvidenceTypeRepository;
import com.propertypilot.infrastructure.persistence.repository.EvidenceValidationHistoryRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestRepository;
import com.propertypilot.infrastructure.persistence.repository.VisitRepository;
import com.propertypilot.security.SecurityService;
import com.propertypilot.application.service.ServiceRequestService;
import com.propertypilot.application.service.EvidenceStorageService;
import com.propertypilot.web.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EvidenceServiceImplTest {
    private final EvidenceRepository evidenceRepository = mock(EvidenceRepository.class);
    private final VisitRepository visitRepository = mock(VisitRepository.class);
    private final ServiceRequestRepository serviceRequestRepository = mock(ServiceRequestRepository.class);
    private final EvidenceTypeRepository evidenceTypeRepository = mock(EvidenceTypeRepository.class);
    private final EvidenceValidationHistoryRepository validationHistoryRepository = mock(EvidenceValidationHistoryRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final EvidenceStorageService evidenceStorageService = mock(EvidenceStorageService.class);
    private final SecurityService securityService = mock(SecurityService.class);
    private final ServiceRequestService serviceRequestService = mock(ServiceRequestService.class);
    private final EvidenceServiceImpl service = new EvidenceServiceImpl(
            evidenceRepository, visitRepository, serviceRequestRepository,
            evidenceTypeRepository, validationHistoryRepository, userRepository, evidenceStorageService, securityService, serviceRequestService);

    @Test
    void getEvidenceForServiceRequestRequiresRequestAccessAndReturnsRequestEvidence() {
        UUID requestId = UUID.randomUUID();
        ServiceRequestEntity request = new ServiceRequestEntity();
        when(serviceRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(evidenceRepository.findAllForServiceRequest(requestId)).thenReturn(List.of());

        var response = service.getEvidenceForServiceRequest(requestId);

        assertThat(response).isEmpty();
        org.mockito.Mockito.verify(securityService).validateServiceRequestAccess(request);
        org.mockito.Mockito.verify(evidenceRepository).findAllForServiceRequest(requestId);
    }

    @Test
    void createEvidenceSupportsNoVisitServiceRequest() {
        UUID requestId = UUID.randomUUID();
        UUID typeId = UUID.randomUUID();
        ServiceRequestEntity request = new ServiceRequestEntity();
        request.setServiceRequestId(requestId);
        request.setStatus("IN_PROGRESS");
        EvidenceTypeEntity type = new EvidenceTypeEntity();
        type.setEvidenceTypeId(typeId);
        type.setCode("DOCUMENT");
        type.setName("Document");
        type.setActiveFlag(true);
        when(serviceRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(evidenceTypeRepository.findById(typeId)).thenReturn(Optional.of(type));
        when(evidenceStorageService.exists("private-object-reference")).thenReturn(true);
        when(evidenceRepository.save(any(EvidenceEntity.class))).thenAnswer(invocation -> {
            EvidenceEntity evidence = invocation.getArgument(0);
            evidence.setEvidenceId(UUID.randomUUID());
            return evidence;
        });
        CreateEvidenceRequest input = new CreateEvidenceRequest();
        input.setServiceRequestId(requestId);
        input.setEvidenceTypeId(typeId);
        input.setFileUrl("storage://private-object-reference");
        input.setMediaType("application/pdf");

        var response = service.createEvidence(input);

        assertThat(response.getVisitId()).isNull();
        assertThat(response.getServiceRequestId()).isEqualTo(requestId);
        verify(securityService).validateServiceRequestExecutionAccess(request);
        verify(serviceRequestService).completeRequestIfReady(
                requestId, "Configured fulfillment requirements satisfied");
    }

    @Test
    void createVisitEvidenceAlsoLinksItToParentServiceRequest() {
        UUID requestId = UUID.randomUUID();
        UUID visitId = UUID.randomUUID();
        UUID typeId = UUID.randomUUID();
        ServiceRequestEntity request = new ServiceRequestEntity();
        request.setServiceRequestId(requestId);
        request.setStatus("IN_PROGRESS");
        VisitEntity visit = new VisitEntity();
        visit.setVisitId(visitId);
        visit.setServiceRequest(request);
        visit.setStatus("IN_PROGRESS");
        EvidenceTypeEntity type = new EvidenceTypeEntity();
        type.setEvidenceTypeId(typeId);
        type.setCode("AFTER_PHOTO");
        type.setName("After photo");
        type.setActiveFlag(true);
        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));
        when(evidenceTypeRepository.findById(typeId)).thenReturn(Optional.of(type));
        when(evidenceStorageService.exists("visit-evidence-object")).thenReturn(true);
        when(evidenceRepository.save(any(EvidenceEntity.class))).thenAnswer(invocation -> {
            EvidenceEntity evidence = invocation.getArgument(0);
            evidence.setEvidenceId(UUID.randomUUID());
            return evidence;
        });
        CreateEvidenceRequest input = new CreateEvidenceRequest();
        input.setVisitId(visitId);
        input.setEvidenceTypeId(typeId);
        input.setFileUrl("storage://visit-evidence-object");
        input.setMediaType("image/jpeg");

        var response = service.createEvidence(input);

        assertThat(response.getVisitId()).isEqualTo(visitId);
        assertThat(response.getServiceRequestId()).isEqualTo(requestId);
        org.mockito.ArgumentCaptor<EvidenceEntity> savedEvidence =
                org.mockito.ArgumentCaptor.forClass(EvidenceEntity.class);
        verify(evidenceRepository).save(savedEvidence.capture());
        assertThat(savedEvidence.getValue().getVisit()).isSameAs(visit);
        assertThat(savedEvidence.getValue().getServiceRequest()).isSameAs(request);
        verify(serviceRequestService).completeRequestIfReady(
                requestId, "Configured fulfillment requirements satisfied");
    }

    @Test
    void createEvidenceRejectsInactiveEvidenceType() {
        UUID requestId = UUID.randomUUID();
        UUID typeId = UUID.randomUUID();
        ServiceRequestEntity request = new ServiceRequestEntity();
        request.setServiceRequestId(requestId);
        request.setStatus("IN_PROGRESS");
        EvidenceTypeEntity type = new EvidenceTypeEntity();
        type.setEvidenceTypeId(typeId);
        type.setActiveFlag(false);
        when(serviceRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(evidenceTypeRepository.findById(typeId)).thenReturn(Optional.of(type));
        when(evidenceStorageService.exists("private-object-reference")).thenReturn(true);
        CreateEvidenceRequest input = new CreateEvidenceRequest();
        input.setServiceRequestId(requestId);
        input.setEvidenceTypeId(typeId);
        input.setFileUrl("storage://private-object-reference");

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> service.createEvidence(input))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Evidence type is not active");

        org.mockito.Mockito.verify(evidenceRepository, org.mockito.Mockito.never()).save(any());
    }

    @Test
    void createEvidenceRejectsExternalFileReferences() {
        CreateEvidenceRequest input = new CreateEvidenceRequest();
        input.setServiceRequestId(UUID.randomUUID());
        input.setEvidenceTypeId(UUID.randomUUID());
        input.setFileUrl("https://example.invalid/fake-evidence.jpg");

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> service.createEvidence(input))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Evidence files must be uploaded through secure evidence storage");

        org.mockito.Mockito.verifyNoInteractions(evidenceStorageService);
        org.mockito.Mockito.verify(evidenceRepository, org.mockito.Mockito.never()).save(any());
    }

    @Test
    void getDashboardCountsMissingEvidenceForNoVisitRequestsAndVisitLinkedEvidenceByRequest() {
        UUID firstRequestId = UUID.randomUUID();
        UUID secondRequestId = UUID.randomUUID();
        UUID thirdRequestId = UUID.randomUUID();
        ServiceEntity beforePhotoService = new ServiceEntity();
        beforePhotoService.setServiceName("Inspection");
        beforePhotoService.setRequiresBeforePhoto(true);
        ServiceEntity afterPhotoService = new ServiceEntity();
        afterPhotoService.setServiceName("Remote document service");
        afterPhotoService.setRequiresAfterPhoto(true);
        ServiceEntity documentService = new ServiceEntity();
        documentService.setServiceName("Remote document review");
        documentService.setRequiresDocumentUpload(true);

        ServiceRequestEntity firstRequest = new ServiceRequestEntity();
        firstRequest.setServiceRequestId(firstRequestId);
        firstRequest.setStatus("COMPLETED");
        firstRequest.setService(beforePhotoService);
        ServiceRequestEntity secondRequest = new ServiceRequestEntity();
        secondRequest.setServiceRequestId(secondRequestId);
        secondRequest.setStatus("IN_PROGRESS");
        secondRequest.setService(afterPhotoService);
        ServiceRequestEntity thirdRequest = new ServiceRequestEntity();
        thirdRequest.setServiceRequestId(thirdRequestId);
        thirdRequest.setStatus("IN_PROGRESS");
        thirdRequest.setService(documentService);

        EvidenceTypeEntity beforeType = new EvidenceTypeEntity();
        beforeType.setCode("BEFORE_PHOTO");
        beforeType.setName("Before photo");
        EvidenceEntity approvedForVisit = new EvidenceEntity();
        approvedForVisit.setValidationStatus("APPROVED");
        approvedForVisit.setEvidenceType(beforeType);
        VisitEntity completedVisit = new VisitEntity();
        completedVisit.setServiceRequest(firstRequest);
        approvedForVisit.setVisit(completedVisit);

        EvidenceTypeEntity afterType = new EvidenceTypeEntity();
        afterType.setCode("AFTER_PHOTO");
        afterType.setName("After photo");
        EvidenceEntity approvedForRemoteRequest = new EvidenceEntity();
        approvedForRemoteRequest.setValidationStatus("APPROVED");
        approvedForRemoteRequest.setEvidenceType(afterType);
        approvedForRemoteRequest.setServiceRequest(secondRequest);

        when(evidenceRepository.findAll()).thenReturn(List.of(approvedForVisit, approvedForRemoteRequest));
        when(serviceRequestRepository.findAll()).thenReturn(List.of(firstRequest, secondRequest, thirdRequest));

        var dashboard = service.getDashboard();

        assertThat(dashboard.missingEvidence()).isEqualTo(1);
        verify(securityService).validateOperationsAccess();
    }
}
