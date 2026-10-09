package com.propertypilot.web.controller;

import com.propertypilot.application.dto.QuoteResponse;
import com.propertypilot.application.dto.RejectQuoteRequest;
import com.propertypilot.application.service.QuoteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/quotes")
public class QuoteController {
    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<QuoteResponse>> getMyQuotes() {
        return ResponseEntity.ok(quoteService.getMyQuotes());
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATIONS')")
    public ResponseEntity<List<QuoteResponse>> getQuotesForOperations() {
        return ResponseEntity.ok(quoteService.getAllQuotesForOperations());
    }

    @GetMapping("/{quoteId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN','OPERATIONS')")
    public ResponseEntity<QuoteResponse> getQuote(@PathVariable UUID quoteId) {
        return ResponseEntity.ok(quoteService.getQuote(quoteId));
    }

    @PostMapping("/{quoteId}/accept")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<QuoteResponse> acceptQuote(@PathVariable UUID quoteId) {
        return ResponseEntity.ok(quoteService.acceptQuote(quoteId));
    }

    @PostMapping("/{quoteId}/reject")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<QuoteResponse> rejectQuote(@PathVariable UUID quoteId,
            @Valid @RequestBody(required = false) RejectQuoteRequest request) {
        return ResponseEntity.ok(quoteService.rejectQuote(quoteId, request == null ? null : request.reason()));
    }
}
