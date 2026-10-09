package com.propertypilot.application.dto;

import java.util.List;

public record SlaReportResponse(
        long evaluatedRequests,
        long atRiskRequests,
        long breachedRequests,
        List<SlaReportRow> requests) {
}
