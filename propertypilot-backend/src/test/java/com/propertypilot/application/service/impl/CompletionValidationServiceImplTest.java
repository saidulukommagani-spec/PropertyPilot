package com.propertypilot.application.service.impl;

import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.repository.EvidenceRepository;
import com.propertypilot.infrastructure.persistence.repository.GpsVerificationRepository;
import com.propertypilot.infrastructure.persistence.repository.VisitRepository;
import com.propertypilot.web.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CompletionValidationServiceImplTest {
    private final VisitRepository visits = mock(VisitRepository.class);
    private final EvidenceRepository evidence = mock(EvidenceRepository.class);
    private final GpsVerificationRepository gps = mock(GpsVerificationRepository.class);
    private final CompletionValidationServiceImpl service =
            new CompletionValidationServiceImpl(visits, evidence, gps);

    @Test
    void noVisitDocumentServiceCanCompleteWithRequestEvidence() {
        UUID requestId = UUID.randomUUID();
        ServiceEntity catalogService = new ServiceEntity();
        catalogService.setRequiresDocumentUpload(true);
        ServiceRequestEntity request = request(requestId, catalogService);
        when(visits.findByServiceRequest_ServiceRequestId(requestId)).thenReturn(List.of());
        when(evidence.existsByServiceRequest_ServiceRequestIdAndEvidenceType_CodeAndValidationStatus(
                requestId, "DOCUMENT", "APPROVED"))
                .thenReturn(true);

        assertThatCode(() -> service.validateCompletionRequirements(request)).doesNotThrowAnyException();
    }

    @Test
    void requiredRequestDocumentCannotBeSkipped() {
        UUID requestId = UUID.randomUUID();
        ServiceEntity catalogService = new ServiceEntity();
        catalogService.setRequiresDocumentUpload(true);
        ServiceRequestEntity request = request(requestId, catalogService);
        when(visits.findByServiceRequest_ServiceRequestId(requestId)).thenReturn(List.of());
        when(evidence.existsByServiceRequest_ServiceRequestIdAndEvidenceType_CodeAndValidationStatus(
                requestId, "DOCUMENT", "APPROVED"))
                .thenReturn(false);

        assertThatThrownBy(() -> service.validateCompletionRequirements(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Document upload is required");
    }

    private ServiceRequestEntity request(UUID id, ServiceEntity service) {
        ServiceRequestEntity request = new ServiceRequestEntity();
        request.setServiceRequestId(id);
        request.setService(service);
        return request;
    }
}
