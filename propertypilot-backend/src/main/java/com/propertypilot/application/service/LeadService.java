package com.propertypilot.application.service;

import com.propertypilot.application.dto.LeadCreateRequest;
import com.propertypilot.application.dto.LeadResponse;

public interface LeadService {

    LeadResponse createLead(LeadCreateRequest request);
}