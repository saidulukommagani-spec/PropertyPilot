package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateVisitEvidenceRequest;
import com.propertypilot.application.dto.VisitEvidenceResponse;
import com.propertypilot.application.service.VisitEvidenceService;
import com.propertypilot.infrastructure.persistence.entity.VisitEntity;
import com.propertypilot.infrastructure.persistence.entity.VisitEvidenceEntity;
import com.propertypilot.infrastructure.persistence.repository.VisitEvidenceRepository;
import com.propertypilot.infrastructure.persistence.repository.VisitRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class VisitEvidenceServiceImpl
        implements VisitEvidenceService {

    private final VisitRepository visitRepository;

    private final VisitEvidenceRepository visitEvidenceRepository;

    public VisitEvidenceServiceImpl(
            VisitRepository visitRepository,
            VisitEvidenceRepository visitEvidenceRepository) {

        this.visitRepository = visitRepository;
        this.visitEvidenceRepository = visitEvidenceRepository;
    }

    @Override
    public VisitEvidenceResponse uploadEvidence(
            CreateVisitEvidenceRequest request) {

        VisitEntity visit =
                visitRepository.findById(
                                request.getVisitId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Visit not found"));

        VisitEvidenceEntity evidence =
                new VisitEvidenceEntity();

        evidence.setVisit(visit);

        evidence.setEvidenceType(
                request.getEvidenceType());

        evidence.setFileName(
                request.getFileName());

        evidence.setFileUrl(
                request.getFileUrl());

        evidence.setRemarks(
                request.getRemarks());

        evidence.setCapturedAt(
                OffsetDateTime.now());

        VisitEvidenceEntity saved =
                visitEvidenceRepository.save(
                        evidence);

        return buildResponse(saved);
    }

    @Override
    public List<VisitEvidenceResponse> getVisitEvidence(
            UUID visitId) {

        if (!visitRepository.existsById(
                visitId)) {

            throw new ResourceNotFoundException(
                    "Visit not found");
        }

        return visitEvidenceRepository
                .findByVisit_VisitId(visitId)
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    private VisitEvidenceResponse buildResponse(
            VisitEvidenceEntity evidence) {

        VisitEvidenceResponse response =
                new VisitEvidenceResponse();

        response.setEvidenceId(
                evidence.getEvidenceId());

        response.setVisitId(
                evidence.getVisit()
                        .getVisitId());

        response.setEvidenceType(
                evidence.getEvidenceType());

        response.setFileName(
                evidence.getFileName());

        response.setFileUrl(
                evidence.getFileUrl());

        response.setRemarks(
                evidence.getRemarks());

        response.setCapturedAt(
                evidence.getCapturedAt());

        return response;
    }
}