package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateEvidenceRequest;
import com.propertypilot.application.dto.EvidenceResponse;
import com.propertypilot.application.service.EvidenceService;
import com.propertypilot.infrastructure.persistence.entity.EvidenceEntity;
import com.propertypilot.infrastructure.persistence.entity.VisitEntity;
import com.propertypilot.infrastructure.persistence.repository.EvidenceRepository;
import com.propertypilot.infrastructure.persistence.repository.VisitRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import com.propertypilot.infrastructure.persistence.entity.EvidenceTypeEntity;
import com.propertypilot.infrastructure.persistence.repository.EvidenceTypeRepository;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class EvidenceServiceImpl
        implements EvidenceService {

    private final EvidenceRepository evidenceRepository;

    private final VisitRepository visitRepository;
private final EvidenceTypeRepository
        evidenceTypeRepository;
    public EvidenceServiceImpl(
        EvidenceRepository evidenceRepository,
        VisitRepository visitRepository,
        EvidenceTypeRepository evidenceTypeRepository) {

    this.evidenceRepository =
            evidenceRepository;

    this.visitRepository =
            visitRepository;

    this.evidenceTypeRepository =
            evidenceTypeRepository;
}
    @Override
    public EvidenceResponse createEvidence(
            CreateEvidenceRequest request) {

        VisitEntity visit =
                visitRepository.findById(
                                request.getVisitId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Visit not found"));

        EvidenceEntity evidence =
                new EvidenceEntity();

        evidence.setVisit(visit);

      EvidenceTypeEntity evidenceType =
        evidenceTypeRepository.findById(
                request.getEvidenceTypeId())
        .orElseThrow(() ->
                new ResourceNotFoundException(
                        "Evidence type not found"));

evidence.setEvidenceType(
        evidenceType);

        evidence.setFileUrl(
                request.getFileUrl());

        evidence.setMediaType(
                request.getMediaType());

        evidence.setChecksumSha256(
                request.getChecksumSha256());

        evidence.setCapturedAt(
                OffsetDateTime.now());

        return buildResponse(
                evidenceRepository.save(
                        evidence));
    }

    @Override
    public EvidenceResponse getEvidence(
            UUID evidenceId) {

        return buildResponse(
                evidenceRepository.findById(
                                evidenceId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Evidence not found")));
    }

    private EvidenceResponse buildResponse(
            EvidenceEntity evidence) {

        EvidenceResponse response =
                new EvidenceResponse();

        response.setEvidenceId(
                evidence.getEvidenceId());

        response.setVisitId(
                evidence.getVisit()
                        .getVisitId());

      response.setEvidenceTypeId(
        evidence.getEvidenceType()
                .getEvidenceTypeId());

response.setEvidenceTypeCode(
        evidence.getEvidenceType()
                .getCode());

response.setEvidenceTypeName(
        evidence.getEvidenceType()
                .getName());

        response.setFileUrl(
                evidence.getFileUrl());

        response.setMediaType(
                evidence.getMediaType());

        response.setChecksumSha256(
                evidence.getChecksumSha256());

        response.setCapturedAt(
                evidence.getCapturedAt());

        return response;
    }
}