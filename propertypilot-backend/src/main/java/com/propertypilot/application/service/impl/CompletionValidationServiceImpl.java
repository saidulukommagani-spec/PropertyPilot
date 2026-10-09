package com.propertypilot.application.service.impl;

import com.propertypilot.application.service.CompletionValidationService;
import com.propertypilot.domain.enums.VisitStatus;
import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.entity.VisitEntity;
import com.propertypilot.infrastructure.persistence.repository.EvidenceRepository;
import com.propertypilot.infrastructure.persistence.repository.GpsVerificationRepository;
import com.propertypilot.infrastructure.persistence.repository.VisitRepository;
import com.propertypilot.web.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompletionValidationServiceImpl
        implements CompletionValidationService {

    private final VisitRepository visitRepository;
    private final EvidenceRepository evidenceRepository;
    private final GpsVerificationRepository gpsVerificationRepository;

    public CompletionValidationServiceImpl(
            VisitRepository visitRepository,
            EvidenceRepository evidenceRepository,
            GpsVerificationRepository gpsVerificationRepository) {

        this.visitRepository =
                visitRepository;

        this.evidenceRepository =
                evidenceRepository;

        this.gpsVerificationRepository =
                gpsVerificationRepository;
    }

    @Override
    public void validateCompletionRequirements(
            ServiceRequestEntity request) {

        ServiceEntity service =
                request.getService();

        if (service == null) {
            throw new BusinessException(
                    "Service configuration not found");
        }

        List<VisitEntity> visits =
                visitRepository
                        .findByServiceRequest_ServiceRequestId(
                                request.getServiceRequestId());

        validateVisitRequirements(
                service,
                visits);

        validateEvidenceRequirements(
                service,
                request,
                visits);

        validateGpsRequirements(
                service,
                visits);
    }

    @Override
    public boolean isCompletionReady(ServiceRequestEntity request) {
        try {
            validateCompletionRequirements(request);
            return true;
        } catch (BusinessException notReady) {
            return false;
        }
    }

    private void validateVisitRequirements(
            ServiceEntity service,
            List<VisitEntity> visits) {

        if (!Boolean.TRUE.equals(service.getRequiresVisit())
                && !Boolean.TRUE.equals(service.getVisitMandatory())) {
            validateNoOpenVisits(visits);
            return;
        }

        if (visits.isEmpty()) {

            throw new BusinessException(
                    "At least one visit is required");
        }

        boolean completedVisitExists =
                visits.stream()
                        .anyMatch(v ->
                                VisitStatus.COMPLETED.name()
                                        .equalsIgnoreCase(
                                                v.getStatus()));

        if (!completedVisitExists) {

            throw new BusinessException(
                    "At least one completed visit is required");
        }

        validateNoOpenVisits(visits);
    }

    private void validateNoOpenVisits(List<VisitEntity> visits) {
        boolean unfinishedVisitExists = visits.stream()
                .anyMatch(v -> !VisitStatus.COMPLETED.name().equalsIgnoreCase(v.getStatus())
                        && !VisitStatus.CANCELLED.name().equalsIgnoreCase(v.getStatus()));
        if (unfinishedVisitExists) {
            throw new BusinessException("All scheduled visits must be completed or cancelled");
        }
    }

    private void validateEvidenceRequirements(
            ServiceEntity service,
            ServiceRequestEntity request,
            List<VisitEntity> visits) {

        if (Boolean.TRUE.equals(
                service.getRequiresBeforePhoto())) {

                    validateEvidenceExists(
                    request,
                    visits,
                    "BEFORE_PHOTO",
                    "Before photo is required");
        }

        if (Boolean.TRUE.equals(
                service.getRequiresAfterPhoto())) {

                    validateEvidenceExists(
                    request,
                    visits,
                    "AFTER_PHOTO",
                    "After photo is required");
        }

        if (Boolean.TRUE.equals(
                service.getRequiresSignature())) {

                    validateEvidenceExists(
                    request,
                    visits,
                    "SIGNATURE",
                    "Customer signature is required");
        }

        if (Boolean.TRUE.equals(
                service.getRequiresDocumentUpload())) {

                    validateEvidenceExists(
                    request,
                    visits,
                    "DOCUMENT",
                    "Document upload is required");
        }
    }

    private void validateGpsRequirements(
            ServiceEntity service,
            List<VisitEntity> visits) {

        if (!Boolean.TRUE.equals(
                service.getRequiresGps())) {
            return;
        }

        List<VisitEntity> completedVisits = visits.stream()
                .filter(visit -> VisitStatus.COMPLETED.name().equalsIgnoreCase(visit.getStatus()))
                .toList();
        boolean gpsValidated = !completedVisits.isEmpty()
                && completedVisits.stream().allMatch(visit -> gpsVerificationRepository
                        .existsByVisit_VisitIdAndValidationStatus(visit.getVisitId(), "VERIFIED"));

        if (!gpsValidated) {

            throw new BusinessException(
                    "Validated GPS verification is required");
        }
    }

    private void validateEvidenceExists(
            ServiceRequestEntity request,
            List<VisitEntity> visits,
            String evidenceCode,
            String errorMessage) {

        boolean exists = evidenceRepository
                .existsByServiceRequest_ServiceRequestIdAndEvidenceType_CodeAndValidationStatus(
                        request.getServiceRequestId(), evidenceCode, "APPROVED")
                || visits.stream()
                        .anyMatch(visit ->
                                evidenceRepository
                                        .existsByVisit_VisitIdAndEvidenceType_CodeAndValidationStatus(
                                                visit.getVisitId(),
                                                evidenceCode, "APPROVED"));

        if (!exists) {

            throw new BusinessException(
                    errorMessage);
        }
    }
}
