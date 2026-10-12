package com.propertypilot.application.service;

import com.propertypilot.application.dto.QuoteResponse;
import java.util.List;
import java.util.UUID;
import com.propertypilot.application.dto.CreateServiceRequestRequest;
import com.propertypilot.application.dto.ServiceRequestResponse;

public interface QuoteService {
    List<QuoteResponse> getMyQuotes();
    List<QuoteResponse> getAllQuotesForOperations();
    QuoteResponse getQuote(UUID quoteId);
    QuoteResponse acceptQuote(UUID quoteId);
    ServiceRequestResponse acceptAndCreateRequest(UUID quoteId, CreateServiceRequestRequest request);
    QuoteResponse rejectQuote(UUID quoteId, String reason);
    int expireQuotes();
}
