package com.propertypilot.application.scheduler;

import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class SubscriptionReminderSchedulerTest {

    @Mock
    private CustomerSubscriptionRepository repository;

    private SubscriptionReminderScheduler scheduler;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        scheduler =
                new SubscriptionReminderScheduler(
                        repository);
    }

    @Test
    void sendRenewalReminders_ShouldReturn_WhenNoSubscriptionsFound() {

        when(repository.findByStatusAndEndDateBetween(
                eq("ACTIVE"),
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        scheduler.sendRenewalReminders();

        verify(repository)
                .findByStatusAndEndDateBetween(
                        eq("ACTIVE"),
                        any(LocalDate.class),
                        any(LocalDate.class));
    }

    @Test
    void sendRenewalReminders_ShouldProcessSubscriptions() {

        CustomerSubscriptionEntity subscription =
                mock(CustomerSubscriptionEntity.class);

        when(repository.findByStatusAndEndDateBetween(
                eq("ACTIVE"),
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(List.of(subscription));

        scheduler.sendRenewalReminders();

        verify(subscription, atLeastOnce())
                .getCustomerSubscriptionId();
    }
}