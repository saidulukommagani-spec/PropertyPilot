package com.propertypilot.application.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class SubscriptionAutoRenewScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(
                    SubscriptionAutoRenewScheduler.class);

    @Scheduled(cron = "0 0 2 * * *")
    public void autoRenewSubscriptions() {
        // Intentionally disabled: renewal requires a verified successful payment.
        log.info("Automatic subscription activation is disabled; renewals require confirmed payment");
    }
}
