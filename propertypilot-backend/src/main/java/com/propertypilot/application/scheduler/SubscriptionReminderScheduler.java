package com.propertypilot.application.scheduler;

import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class SubscriptionReminderScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(
                    SubscriptionReminderScheduler.class);

    private final CustomerSubscriptionRepository
            customerSubscriptionRepository;

    public SubscriptionReminderScheduler(
            CustomerSubscriptionRepository customerSubscriptionRepository) {

        this.customerSubscriptionRepository =
                customerSubscriptionRepository;
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void sendRenewalReminders() {

        List<CustomerSubscriptionEntity> subscriptions =
                customerSubscriptionRepository
                        .findByStatusAndEndDateBetween(
                                "ACTIVE",
                                LocalDate.now(),
                                LocalDate.now().plusDays(30));

        if (subscriptions.isEmpty()) {

            log.info(
                    "No subscriptions expiring in next 30 days");
            return;
        }

        for (CustomerSubscriptionEntity subscription
                : subscriptions) {

            log.info(
                    "Renewal reminder for subscription: {}",
                    subscription.getCustomerSubscriptionId());
        }
    }
}