package com.propertypilot.application.scheduler;

import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class SubscriptionExpiryScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(
                    SubscriptionExpiryScheduler.class);

    private final CustomerSubscriptionRepository
            customerSubscriptionRepository;

    public SubscriptionExpiryScheduler(
            CustomerSubscriptionRepository customerSubscriptionRepository) {

        this.customerSubscriptionRepository =
                customerSubscriptionRepository;
    }

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void expireSubscriptions() {

        List<CustomerSubscriptionEntity> subscriptions =
                customerSubscriptionRepository
                        .findByStatusAndEndDateBefore(
                                "ACTIVE",
                                LocalDate.now());

        if (subscriptions.isEmpty()) {

            log.info(
                    "No subscriptions found for expiry");
            return;
        }

        for (CustomerSubscriptionEntity subscription
                : subscriptions) {

            subscription.setStatus("EXPIRED");

            log.info(
                    "Subscription expired: {}",
                    subscription
                            .getCustomerSubscriptionId());
        }

        customerSubscriptionRepository
                .saveAll(subscriptions);
    }
}