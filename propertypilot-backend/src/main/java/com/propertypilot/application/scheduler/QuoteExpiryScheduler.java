package com.propertypilot.application.scheduler;

import com.propertypilot.application.service.QuoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class QuoteExpiryScheduler {
    private static final Logger log = LoggerFactory.getLogger(QuoteExpiryScheduler.class);
    private final QuoteService quoteService;

    public QuoteExpiryScheduler(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @Scheduled(fixedDelayString = "${propertypilot.quotes.expiry-scan-ms:60000}")
    public void expireQuotes() {
        int expired = quoteService.expireQuotes();
        if (expired > 0) log.info("Expired {} quotes", expired);
    }
}
