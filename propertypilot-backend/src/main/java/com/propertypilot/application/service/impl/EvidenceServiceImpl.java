package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateEvidenceRequest;
import com.propertypilot.application.dto.EvidenceResponse;
import com.propertypilot.application.dto.EvidenceTypeResponse;
import com.propertypilot.application.dto.EvidenceValidationRequest;
import com.propertypilot.application.dto.EvidenceValidationHistoryResponse;
import com.propertypilot.application.dto.VisitEvidenceSummaryResponse;
import com.propertypilot.application.dto.EvidenceDashboardResponse;
import com.propertypilot.application.service.EvidenceService;
import com.propertypilot.application.service.EvidenceStorageService;
import com.propertypilot.application.service.ServiceRequestService;
import com.propertypilot.infrastructure.persistence.entity.EvidenceEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.infrastructure.persistence.entity.VisitEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.repository.EvidenceRepository;
import com.propertypilot.infrastructure.persistence.repository.VisitRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestRepository;
import com.propertypilot.web.exception.BusinessException;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.propertypilot.infrastructure.persistence.entity.EvidenceTypeEntity;
import com.propertypilot.infrastructure.persistence.repository.EvidenceTypeRepository;
import com.propertypilot.infrastructure.persistence.repository.EvidenceValidationHistoryRepository;
import com.propertypilot.infrastructure.persistence.entity.EvidenceValidationHistoryEntity;
import com.propertypilot.infrastructure.persistence.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.List;
import com.propertypilot.security.SecurityService;

@Service
@Transactional
public class EvidenceServiceImpl
        implements EvidenceService {

    private final SecurityService securityService;

    private final EvidenceRepository evidenceRepository;
    private final EvidenceStorageService evidenceStorageService;

    private final VisitRepository visitRepository;
    private final ServiceRequestRepository serviceRequestRepository;
private final EvidenceTypeRepository
        evidenceTypeRepository;
    private final ServiceRequestService serviceRequestService;
    private final EvidenceValidationHistoryRepository validationHistoryRepository;
    private final UserRepository userRepository;
    public EvidenceServiceImpl(
        EvidenceRepository evidenceRepository,
        VisitRepository visitRepository,
        ServiceRequestRepository serviceRequestRepository,
        EvidenceTypeRepository evidenceTypeRepository,
        EvidenceValidationHistoryRepository validationHistoryRepository,
        UserRepository userRepository,
        EvidenceStorageService evidenceStorageService,
        SecurityService securityService,
        ServiceRequestService serviceRequestService) {

    this.evidenceRepository =
            evidenceRepository;
    this.evidenceStorageService = evidenceStorageService;

    this.visitRepository =
            visitRepository;

    this.serviceRequestRepository = serviceRequestRepository;

    this.evidenceTypeRepository =
            evidenceTypeRepository;
    this.validationHistoryRepository = validationHistoryRepository;
    this.userRepository = userRepository;
    this.securityService = securityService;
    this.serviceRequestService = serviceRequestService;
}
    @Override
    public EvidenceResponse createEvidence(
            CreateEvidenceRequest request) {
        if ((request.getVisitId() == null) == (request.getServiceRequestId() == null)) {
            throw new BusinessException("Provide exactly one of visitId or serviceRequestId");
        }
        if (request.getFileUrl() == null || !request.getFileUrl().startsWith("storage://")) {
            throw new BusinessException("Evidence files must be uploaded through secure evidence storage");
        }
        String storageKey = request.getFileUrl().substring("storage://".length());
        if (!evidenceStorageService.exists(storageKey)) {
            throw new ResourceNotFoundException("Uploaded evidence file not found");
        }

        VisitEntity visit = null;
        ServiceRequestEntity serviceRequest;
        if (request.getVisitId() != null) {
            visit = visitRepository.findById(request.getVisitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Visit not found"));
            securityService.validateVisitExecutionAccess(visit);
            if (!"IN_PROGRESS".equals(visit.getStatus()) && !"COMPLETED".equals(visit.getStatus())) {
                throw new BusinessException("Evidence can only be uploaded for active visits");
            }
            serviceRequest = visit.getServiceRequest();
        } else {
            serviceRequest = serviceRequestRepository.findById(request.getServiceRequestId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service request not found"));
            securityService.validateServiceRequestExecutionAccess(serviceRequest);
        }

        if (!"IN_PROGRESS".equals(serviceRequest.getStatus())) {
            throw new BusinessException("Evidence can only be uploaded for active service requests");
        }


      EvidenceTypeEntity evidenceType =
        evidenceTypeRepository.findById(
                request.getEvidenceTypeId())
        .orElseThrow(() ->
                new ResourceNotFoundException(
                        "Evidence type not found"));
        if (!Boolean.TRUE.equals(evidenceType.getActiveFlag())) {
            throw new BusinessException("Evidence type is not active");
        }

EvidenceEntity evidence =
        new EvidenceEntity();
evidence.setVisit(
        visit);
evidence.setServiceRequest(serviceRequest);

evidence.setEvidenceType(
        evidenceType);


        evidence.setFileUrl(
                request.getFileUrl());

        evidence.setMediaType(
                request.getMediaType());

        evidence.setChecksumSha256(
                request.getChecksumSha256());

        evidence.setCapturedAt(request.getCapturedAt() == null ? OffsetDateTime.now() : request.getCapturedAt());
        evidence.setLatitude(request.getLatitude());
        evidence.setLongitude(request.getLongitude());
        evidence.setDeviceInfo(request.getDeviceInfo());
        evidence.setFileName(request.getFileName());
        evidence.setRemarks(request.getRemarks());
        evidence.setValidationStatus("PENDING");
        evidence.setCapturedBy(securityService.getCurrentUser());

        EvidenceEntity saved = evidenceRepository.save(evidence);
        saveHistory(saved, "UPLOADED", "Evidence uploaded");
        serviceRequestService.completeRequestIfReady(
                serviceRequest.getServiceRequestId(), "Configured fulfillment requirements satisfied");
        return buildResponse(saved);
    }

    @Override
    public EvidenceResponse getEvidence(
            UUID evidenceId) {
        EvidenceEntity evidence = evidenceRepository.findById(
                                evidenceId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Evidence not found"));
        securityService.validateEvidenceAccess(evidence);
        return buildResponse(evidence);
    }

    @Override
    public List<EvidenceResponse> getEvidenceForServiceRequest(UUID serviceRequestId) {
        ServiceRequestEntity serviceRequest = serviceRequestRepository.findById(serviceRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Service request not found"));
        securityService.validateServiceRequestAccess(serviceRequest);
        return evidenceRepository.findAllForServiceRequest(serviceRequestId)
                .stream().map(this::buildResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvidenceResponse> getAllEvidenceForOperations() {
        securityService.validateOperationsAccess();
        return evidenceRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(EvidenceEntity::getCapturedAt,
                        java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())))
                .map(this::buildResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvidenceTypeResponse> getActiveEvidenceTypes() {
        return evidenceTypeRepository.findByActiveFlagTrue().stream()
                .sorted(java.util.Comparator.comparing(EvidenceTypeEntity::getCategory).thenComparing(EvidenceTypeEntity::getName))
                .map(type -> new EvidenceTypeResponse(type.getEvidenceTypeId(), type.getCode(), type.getName(), type.getCategory(), type.getDescription()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvidenceResponse> getEvidenceForVisit(UUID visitId) {
        VisitEntity visit = visitRepository.findById(visitId).orElseThrow(() -> new ResourceNotFoundException("Visit not found"));
        securityService.validateVisitAccess(visit);
        return evidenceRepository.findByVisit_VisitIdOrderByCapturedAtDesc(visitId).stream().map(this::buildResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvidenceResponse> getValidationQueue() {
        securityService.validateOperationsAccess();
        return evidenceRepository.findByValidationStatusInOrderByCapturedAtAsc(List.of("PENDING", "REJECTED", "REUPLOAD_REQUESTED"))
                .stream().map(this::buildResponse).toList();
    }

    @Override
    public EvidenceResponse validateEvidence(UUID evidenceId, EvidenceValidationRequest request) {
        securityService.validateOperationsAccess();
        EvidenceEntity evidence = evidenceRepository.findById(evidenceId).orElseThrow(() -> new ResourceNotFoundException("Evidence not found"));
        if (!List.of("PENDING", "REJECTED", "REUPLOAD_REQUESTED").contains(evidence.getValidationStatus())) {
            throw new BusinessException("Only pending or returned evidence can be reviewed");
        }
        if ("APPROVED".equals(request.status()) && "GPS".equalsIgnoreCase(evidence.getEvidenceType().getCode())
                && (evidence.getLatitude() == null || evidence.getLongitude() == null)) {
            throw new BusinessException("GPS evidence must include latitude and longitude before approval");
        }
        evidence.setValidationStatus(request.status());
        evidence.setValidationComment(request.comment().trim());
        evidence.setValidatedBy(securityService.getCurrentUserId());
        evidence.setValidatedAt(OffsetDateTime.now());
        EvidenceEntity saved = evidenceRepository.save(evidence);
        saveHistory(saved, request.status(), request.comment().trim());
        if ("APPROVED".equals(request.status())) {
            serviceRequestService.completeRequestIfReady(saved.getServiceRequest().getServiceRequestId(), "Configured fulfillment requirements satisfied");
        }
        return buildResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public VisitEvidenceSummaryResponse getVisitEvidenceSummary(UUID visitId) {
        VisitEntity visit = visitRepository.findById(visitId).orElseThrow(() -> new ResourceNotFoundException("Visit not found"));
        securityService.validateVisitAccess(visit);
        List<EvidenceResponse> uploaded = evidenceRepository.findByVisit_VisitIdOrderByCapturedAtDesc(visitId).stream().map(this::buildResponse).toList();
        var service = visit.getServiceRequest().getService();
        List<String> required = requiredEvidenceCodes(service);
        List<String> missing = required.stream().filter(code -> uploaded.stream().noneMatch(item -> code.equals(item.getEvidenceTypeCode()) && "APPROVED".equals(item.getValidationStatus()))).toList();
        int percent = required.isEmpty() ? 100 : (required.size() - missing.size()) * 100 / required.size();
        return new VisitEvidenceSummaryResponse(visitId, List.copyOf(required), uploaded, missing, percent);
    }

    @Override
    @Transactional(readOnly = true)
    public String getFileStorageKey(UUID evidenceId) {
        EvidenceEntity evidence = evidenceRepository.findById(evidenceId).orElseThrow(() -> new ResourceNotFoundException("Evidence not found"));
        securityService.validateEvidenceAccess(evidence);
        if (evidence.getFileUrl() == null || !evidence.getFileUrl().startsWith("storage://")) throw new ResourceNotFoundException("Stored evidence file not found");
        return evidence.getFileUrl().substring("storage://".length());
    }

    @Override
    @Transactional(readOnly = true)
    public EvidenceDashboardResponse getDashboard() {
        securityService.validateOperationsAccess();
        List<EvidenceEntity> all = evidenceRepository.findAll();
        var today = OffsetDateTime.now().toLocalDate();
        long pending = all.stream().filter(item -> "PENDING".equals(item.getValidationStatus())).count();
        long approved = all.stream().filter(item -> "APPROVED".equals(item.getValidationStatus())).count();
        long rejected = all.stream().filter(item -> "REJECTED".equals(item.getValidationStatus())).count();
        long uploadedToday = all.stream().filter(item -> item.getCapturedAt() != null && item.getCapturedAt().toLocalDate().equals(today)).count();
        var approvedCodesByRequest = all.stream()
                .filter(item -> "APPROVED".equals(item.getValidationStatus()))
                .filter(item -> serviceRequestIdFor(item) != null)
                .collect(java.util.stream.Collectors.groupingBy(this::serviceRequestIdFor,
                        java.util.stream.Collectors.mapping(item -> item.getEvidenceType().getCode(), java.util.stream.Collectors.toSet())));
        long missing = serviceRequestRepository.findAll().stream()
                .filter(request -> List.of("IN_PROGRESS", "COMPLETED").contains(request.getStatus()))
                .filter(request -> request.getService() != null)
                .filter(request -> {
                    List<String> required = requiredEvidenceCodes(request.getService());
                    return !required.isEmpty() && !approvedCodesByRequest
                            .getOrDefault(request.getServiceRequestId(), java.util.Set.of()).containsAll(required);
                })
                .count();
        var byType = all.stream().collect(java.util.stream.Collectors.groupingBy(item -> item.getEvidenceType().getName(), java.util.TreeMap::new, java.util.stream.Collectors.counting()));
        var byService = all.stream().collect(java.util.stream.Collectors.groupingBy(item -> {
            ServiceRequestEntity request = serviceRequestFor(item);
            return request != null && request.getService() != null
                    ? request.getService().getServiceName() : "Unspecified service";
        }, java.util.TreeMap::new, java.util.stream.Collectors.counting()));
        return new EvidenceDashboardResponse(all.size(), pending, approved, rejected, missing, uploadedToday, byType, byService);
    }

    private UUID serviceRequestIdFor(EvidenceEntity evidence) {
        ServiceRequestEntity request = serviceRequestFor(evidence);
        return request == null ? null : request.getServiceRequestId();
    }

    private ServiceRequestEntity serviceRequestFor(EvidenceEntity evidence) {
        if (evidence.getServiceRequest() != null) return evidence.getServiceRequest();
        return evidence.getVisit() == null ? null : evidence.getVisit().getServiceRequest();
    }

    private List<String> requiredEvidenceCodes(ServiceEntity service) {
        if (service == null) return List.of();
        List<String> required = new java.util.ArrayList<>();
        if (Boolean.TRUE.equals(service.getRequiresBeforePhoto())) required.add("BEFORE_PHOTO");
        if (Boolean.TRUE.equals(service.getRequiresAfterPhoto())) required.add("AFTER_PHOTO");
        if (Boolean.TRUE.equals(service.getRequiresSignature())) required.add("SIGNATURE");
        if (Boolean.TRUE.equals(service.getRequiresDocumentUpload())) required.add("DOCUMENT");
        if (Boolean.TRUE.equals(service.getRequiresGps())) required.add("GPS");
        return List.copyOf(required);
    }

    private void saveHistory(EvidenceEntity evidence, String status, String comment) {
        EvidenceValidationHistoryEntity item = new EvidenceValidationHistoryEntity();
        item.setEvidence(evidence); item.setStatus(status); item.setComment(comment); item.setUserId(securityService.getCurrentUserId()); item.setOccurredAt(OffsetDateTime.now());
        validationHistoryRepository.save(item);
    }

    private EvidenceResponse buildResponse(
            EvidenceEntity evidence) {

        EvidenceResponse response =
                new EvidenceResponse();

        response.setEvidenceId(
                evidence.getEvidenceId());

        response.setVisitId(evidence.getVisit() == null ? null : evidence.getVisit().getVisitId());
        response.setServiceRequestId(evidence.getServiceRequest() == null
                ? null : evidence.getServiceRequest().getServiceRequestId());

      response.setEvidenceTypeId(
        evidence.getEvidenceType()
                .getEvidenceTypeId());

response.setEvidenceTypeCode(
        evidence.getEvidenceType()
                .getCode());

response.setEvidenceTypeName(
        evidence.getEvidenceType()
                .getName());

        response.setFileUrl(evidence.getFileUrl() != null && evidence.getFileUrl().startsWith("storage://")
                ? "/api/evidence/" + evidence.getEvidenceId() + "/file" : evidence.getFileUrl());

        response.setMediaType(
                evidence.getMediaType());

        response.setChecksumSha256(
                evidence.getChecksumSha256());

        response.setCapturedAt(
                evidence.getCapturedAt());
        response.setCreatedAt(evidence.getCreatedAt());

        response.setLatitude(evidence.getLatitude());
        response.setLongitude(evidence.getLongitude());
        response.setFileName(evidence.getFileName());
        response.setRemarks(evidence.getRemarks());
        response.setDeviceInfo(evidence.getDeviceInfo());
        response.setValidationStatus(evidence.getValidationStatus());
        response.setValidationComment(evidence.getValidationComment());
        response.setValidatedAt(evidence.getValidatedAt());
        if (evidence.getCapturedBy() != null) response.setUploadedByName(evidence.getCapturedBy().getFullName());
        else if (evidence.getCreatedBy() != null) userRepository.findById(evidence.getCreatedBy()).ifPresent(user -> response.setUploadedByName(user.getFullName()));
        if (evidence.getValidatedBy() != null) userRepository.findById(evidence.getValidatedBy()).ifPresent(user -> response.setValidatedByName(user.getFullName()));
        if (evidence.getServiceRequest() != null) {
            if (evidence.getServiceRequest().getProperty() != null) response.setPropertyName(evidence.getServiceRequest().getProperty().getTitle());
            if (evidence.getServiceRequest().getCustomer() != null && evidence.getServiceRequest().getCustomer().getUser() != null) response.setCustomerName(evidence.getServiceRequest().getCustomer().getUser().getFullName());
            if (evidence.getServiceRequest().getService() != null) response.setServiceName(evidence.getServiceRequest().getService().getServiceName());
        }
        if (evidence.getVisit() != null) {
            response.setVisitStatus(evidence.getVisit().getStatus());
            response.setVisitScheduledAt(evidence.getVisit().getScheduledAt());
            if (evidence.getVisit().getAgent() != null && evidence.getVisit().getAgent().getUser() != null) response.setAgentName(evidence.getVisit().getAgent().getUser().getFullName());
        }
        if (validationHistoryRepository != null) {
            response.setValidationHistory(validationHistoryRepository.findByEvidence_EvidenceIdOrderByOccurredAtDesc(evidence.getEvidenceId()).stream()
                    .map(item -> new EvidenceValidationHistoryResponse(item.getHistoryId(), item.getStatus(), item.getComment(),
                            item.getUserId() == null ? null : userRepository.findById(item.getUserId()).map(user -> user.getFullName()).orElse(null), item.getOccurredAt())).toList());
        }

        return response;
    }
}
