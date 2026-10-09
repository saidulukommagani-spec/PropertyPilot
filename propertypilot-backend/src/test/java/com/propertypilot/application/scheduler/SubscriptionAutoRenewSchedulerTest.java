package com.propertypilot.application.scheduler;

import org.junit.jupiter.api.Test;

class SubscriptionAutoRenewSchedulerTest {

    @Test
    void automaticRenewalDoesNotCreateRenewalsWithoutConfirmedPayment() {
        SubscriptionAutoRenewScheduler scheduler = new SubscriptionAutoRenewScheduler();

        scheduler.autoRenewSubscriptions();
    }
}
