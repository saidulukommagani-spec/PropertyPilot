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

class SubscriptionExpirySchedulerTest {

    @Mock
    private CustomerSubscriptionRepository repository;

    private SubscriptionExpiryScheduler scheduler;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        scheduler =
                new SubscriptionExpiryScheduler(
                        repository);
    }

    @Test
    void expireSubscriptions_ShouldReturn_WhenNoSubscriptionsFound() {

        when(repository.findByStatusAndEndDateBefore(
                eq("ACTIVE"),
                any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        scheduler.expireSubscriptions();

        verify(repository, never())
                .saveAll(anyList());
    }

    @Test
    void expireSubscriptions_ShouldExpireSubscriptions() {

        CustomerSubscriptionEntity subscription =
                mock(CustomerSubscriptionEntity.class);

        when(repository.findByStatusAndEndDateBefore(
                eq("ACTIVE"),
                any(LocalDate.class)))
                .thenReturn(List.of(subscription));

        scheduler.expireSubscriptions();

        verify(subscription)
                .setStatus("EXPIRED");

        verify(repository)
                .saveAll(anyList());
    }
}