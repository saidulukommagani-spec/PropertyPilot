package com.propertypilot.application.service;

import com.propertypilot.application.dto.LeadCreateRequest;
import com.propertypilot.application.dto.LeadResponse;
import com.propertypilot.domain.enums.LeadStatus;
import com.propertypilot.application.dto.LeadAssignRequest;
import com.propertypilot.application.dto.LeadAssignmentResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import com.propertypilot.application.dto.OperationsLeadResponse;
import org.springframework.data.domain.Pageable;

public interface LeadService {

    LeadResponse createLead(
            LeadCreateRequest request);

    LeadResponse getLead(
            UUID leadId);

    LeadResponse updateLeadStatus(
            UUID leadId,
            LeadStatus status);

    List<LeadResponse> getMyLeads();

   LeadAssignmentResponse assignLead(
    UUID leadId,
    LeadAssignRequest request);

    Page<OperationsLeadResponse> searchOperationsLeads(String search, LeadStatus status, Pageable pageable);
}
