package com.propertypilot.application.scheduler;

import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanVersionEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionRenewalEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionPlanVersionRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionRenewalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class SubscriptionAutoRenewScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(
                    SubscriptionAutoRenewScheduler.class);

    private final CustomerSubscriptionRepository
            customerSubscriptionRepository;

    private final SubscriptionPlanVersionRepository
            planVersionRepository;

    private final SubscriptionRenewalRepository
            renewalRepository;

    public SubscriptionAutoRenewScheduler(
            CustomerSubscriptionRepository customerSubscriptionRepository,
            SubscriptionPlanVersionRepository planVersionRepository,
            SubscriptionRenewalRepository renewalRepository) {

        this.customerSubscriptionRepository =
                customerSubscriptionRepository;

        this.planVersionRepository =
                planVersionRepository;

        this.renewalRepository =
                renewalRepository;
    }

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void autoRenewSubscriptions() {

        List<CustomerSubscriptionEntity> subscriptions =
                customerSubscriptionRepository
                        .findByAutoRenewTrueAndStatus(
                                "ACTIVE");

        if (subscriptions.isEmpty()) {

            log.info(
                    "No auto-renew subscriptions found");
            return;
        }

        LocalDate today =
                LocalDate.now();

        for (CustomerSubscriptionEntity subscription
                : subscriptions) {

            try {

                if (subscription.getEndDate() == null
                        || !subscription.getEndDate()
                        .equals(today)) {

                    continue;
                }

                processRenewal(subscription);

            } catch (Exception ex) {

                log.error(
                        "Auto renewal failed for subscription {}",
                        subscription.getCustomerSubscriptionId(),
                        ex);
            }
        }
    }

    private void processRenewal(
            CustomerSubscriptionEntity subscription) {

        SubscriptionPlanVersionEntity currentVersion =
                subscription.getPlanVersion();

        SubscriptionPlanVersionEntity latestVersion =
                planVersionRepository
                        .findTopBySubscriptionPlan_SubscriptionPlanIdOrderByVersionNumberDesc(
                                currentVersion
                                        .getSubscriptionPlan()
                                        .getSubscriptionPlanId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Latest plan version not found"));

        CustomerSubscriptionEntity newSubscription =
                new CustomerSubscriptionEntity();

        newSubscription.setCustomer(
                subscription.getCustomer());

        newSubscription.setPlanVersion(
                latestVersion);

        newSubscription.setStatus(
                "ACTIVE");

        newSubscription.setAutoRenew(
                true);

        LocalDate startDate =
                subscription.getEndDate()
                        .plusDays(1);

        LocalDate endDate =
                startDate.plusYears(1)
                        .minusDays(1);

        newSubscription.setStartDate(
                startDate);

        newSubscription.setEndDate(
                endDate);

        customerSubscriptionRepository
                .save(newSubscription);

        SubscriptionRenewalEntity renewal =
                new SubscriptionRenewalEntity();

        renewal.setCustomerSubscription(
                subscription);

        renewal.setNewCustomerSubscription(
                newSubscription);

        renewal.setRenewalDate(
                LocalDate.now());

        renewal.setPeriodStart(
                startDate);

        renewal.setPeriodEnd(
                endDate);

        renewal.setRenewalType(
                "AUTO");

        renewal.setRenewalAmount(
                latestVersion.getPrice());

        renewal.setStatus(
                "SUCCESS");

        renewal.setRemarks(
                "Auto renewed by scheduler");

        renewalRepository.save(
                renewal);

        subscription.setStatus(
                "RENEWED");

        customerSubscriptionRepository
                .save(subscription);

        log.info(
                "Subscription auto renewed. Old={} New={}",
                subscription.getCustomerSubscriptionId(),
                newSubscription.getCustomerSubscriptionId());
    }
}