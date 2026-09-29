package com.propertypilot.application.service;

import com.propertypilot.application.dto.LeadActivityCreateRequest;
import com.propertypilot.application.dto.LeadActivityResponse;

import java.util.List;
import java.util.UUID;

public interface LeadActivityService {

    LeadActivityResponse createActivity(
            UUID leadId,
            LeadActivityCreateRequest request);

    List<LeadActivityResponse> getActivities(
            UUID leadId);
}