package com.propertypilot.application.service;

import com.propertypilot.application.dto.LeadServiceRequestResponse;
import java.util.List;
import java.util.UUID;

public interface LeadServiceRequestService {
    List<LeadServiceRequestResponse> getLeadServiceRequests(UUID leadId);
}
