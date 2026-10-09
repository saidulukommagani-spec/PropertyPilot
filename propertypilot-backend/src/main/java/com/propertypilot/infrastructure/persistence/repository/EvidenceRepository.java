package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.EvidenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EvidenceRepository
        extends JpaRepository<EvidenceEntity, UUID> {

    List<EvidenceEntity>
    findByVisit_VisitId(UUID visitId);

    List<EvidenceEntity> findByServiceRequest_ServiceRequestIdOrderByCapturedAtDesc(UUID serviceRequestId);

    @Query("select e from EvidenceEntity e where e.serviceRequest.serviceRequestId = :serviceRequestId "
            + "or e.visit.serviceRequest.serviceRequestId = :serviceRequestId order by e.capturedAt desc")
    List<EvidenceEntity> findAllForServiceRequest(@Param("serviceRequestId") UUID serviceRequestId);

    List<EvidenceEntity>
findByEvidenceType_Code(
        String code);

boolean existsByVisit_VisitIdAndEvidenceType_EvidenceTypeId(
        UUID visitId,
        UUID evidenceTypeId);

        boolean existsByVisit_VisitIdAndEvidenceType_Code(
        UUID visitId,
        String code);

boolean existsByServiceRequest_ServiceRequestIdAndEvidenceType_Code(
        UUID serviceRequestId,
        String code);

    List<EvidenceEntity> findByValidationStatusInOrderByCapturedAtAsc(List<String> statuses);
    List<EvidenceEntity> findByVisit_VisitIdOrderByCapturedAtDesc(UUID visitId);
    boolean existsByServiceRequest_ServiceRequestIdAndEvidenceType_CodeAndValidationStatus(UUID serviceRequestId, String code, String validationStatus);
    boolean existsByVisit_VisitIdAndEvidenceType_CodeAndValidationStatus(UUID visitId, String code, String validationStatus);

    long countByServiceRequest_ServiceRequestId(UUID serviceRequestId);

    @Query("select count(distinct e.evidenceId) from EvidenceEntity e where e.serviceRequest.serviceRequestId = :serviceRequestId "
            + "or e.visit.serviceRequest.serviceRequestId = :serviceRequestId")
    long countAllForServiceRequest(@Param("serviceRequestId") UUID serviceRequestId);

}
