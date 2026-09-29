package com.propertypilot.application.service;

import com.propertypilot.application.dto.LeadCreateRequest;
import com.propertypilot.application.dto.LeadResponse;
import com.propertypilot.domain.enums.LeadStatus;
import com.propertypilot.application.dto.LeadAssignRequest;
import com.propertypilot.application.dto.LeadAssignmentResponse;
import java.util.List;
import java.util.UUID;

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
}