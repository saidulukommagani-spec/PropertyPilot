package com.propertypilot.application.service;

import com.propertypilot.application.dto.QuoteResponse;
import java.util.List;
import java.util.UUID;

public interface QuoteService {
    List<QuoteResponse> getMyQuotes();
    List<QuoteResponse> getAllQuotesForOperations();
    QuoteResponse getQuote(UUID quoteId);
    QuoteResponse acceptQuote(UUID quoteId);
    QuoteResponse rejectQuote(UUID quoteId, String reason);
    int expireQuotes();
}
