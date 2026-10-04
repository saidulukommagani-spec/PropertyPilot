package com.propertypilot.application.service;

import com.propertypilot.application.dto.ConsumptionResponse;
import com.propertypilot.application.dto.CreateConsumptionRequest;
import com.propertypilot.application.dto.SubscriptionUsageResponse;
import com.propertypilot.infrastructure.persistence.entity.*;
import com.propertypilot.infrastructure.persistence.repository.*;
import com.propertypilot.web.exception.BusinessException;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionEntitlementConsumptionServiceImplTest {

    @Mock
    private SubscriptionEntitlementConsumptionRepository
            consumptionRepository;

    @Mock
    private CustomerSubscriptionRepository
            customerSubscriptionRepository;

    @Mock
    private SubscriptionPlanEntitlementRepository
            entitlementRepository;

    @Mock
    private ServiceRepository
            serviceRepository;

    @Mock
    private ServiceRequestRepository
            serviceRequestRepository;

    @InjectMocks
    private SubscriptionEntitlementConsumptionServiceImpl service;

    private UUID subscriptionId;
    private UUID serviceId;
    private UUID planVersionId;

    private CustomerSubscriptionEntity subscription;
    private ServiceEntity serviceEntity;
    private SubscriptionPlanEntitlementEntity entitlement;

    @BeforeEach
    void setUp() {

        subscriptionId = UUID.randomUUID();
        serviceId = UUID.randomUUID();
        planVersionId = UUID.randomUUID();

        SubscriptionPlanVersionEntity version =
                new SubscriptionPlanVersionEntity();

        version.setPlanVersionId(planVersionId);

        subscription =
                new CustomerSubscriptionEntity();

        subscription.setCustomerSubscriptionId(
                subscriptionId);

        subscription.setStatus(
                "ACTIVE");

        subscription.setPlanVersion(
                version);

        serviceEntity =
                new ServiceEntity();

        serviceEntity.setServiceId(
                serviceId);

        entitlement =
                new SubscriptionPlanEntitlementEntity();

        entitlement.setQuantity(
                10);
    }

@Test
void consume_ShouldThrow_WhenSubscriptionNotFound() {

    CreateConsumptionRequest request =
            new CreateConsumptionRequest();

    request.setCustomerSubscriptionId(
            subscriptionId);

    when(customerSubscriptionRepository.findById(
            subscriptionId))
            .thenReturn(Optional.empty());

    assertThrows(
            ResourceNotFoundException.class,
            () -> service.consume(request));

    verify(customerSubscriptionRepository)
            .findById(subscriptionId);
}

@Test
void consume_ShouldThrow_WhenSubscriptionInactive() {

    subscription.setStatus("EXPIRED");

    CreateConsumptionRequest request =
            new CreateConsumptionRequest();

    request.setCustomerSubscriptionId(
            subscriptionId);

    when(customerSubscriptionRepository.findById(
            subscriptionId))
            .thenReturn(Optional.of(subscription));

    assertThrows(
            BusinessException.class,
            () -> service.consume(request));
}

@Test
void consume_ShouldThrow_WhenServiceNotFound() {

    CreateConsumptionRequest request =
            new CreateConsumptionRequest();

    request.setCustomerSubscriptionId(
            subscriptionId);

    request.setServiceId(
            serviceId);

    when(customerSubscriptionRepository.findById(
            subscriptionId))
            .thenReturn(Optional.of(subscription));

    when(serviceRepository.findById(
            serviceId))
            .thenReturn(Optional.empty());

    assertThrows(
            ResourceNotFoundException.class,
            () -> service.consume(request));
}

@Test
void consume_ShouldThrow_WhenEntitlementMissing() {

    CreateConsumptionRequest request =
            new CreateConsumptionRequest();

    request.setCustomerSubscriptionId(
            subscriptionId);

    request.setServiceId(
            serviceId);

    when(customerSubscriptionRepository.findById(
            subscriptionId))
            .thenReturn(Optional.of(subscription));

    when(serviceRepository.findById(
            serviceId))
            .thenReturn(Optional.of(serviceEntity));

    when(entitlementRepository
            .findByPlanVersion_PlanVersionIdAndService_ServiceId(
                    planVersionId,
                    serviceId))
            .thenReturn(Optional.empty());

    assertThrows(
            BusinessException.class,
            () -> service.consume(request));
}

@Test
void consume_ShouldThrow_WhenQuantityLessThanOrEqualZero() {

    CreateConsumptionRequest request =
            new CreateConsumptionRequest();

    request.setCustomerSubscriptionId(
            subscriptionId);

    request.setServiceId(
            serviceId);

    request.setQuantity(
            0);

    when(customerSubscriptionRepository.findById(
            subscriptionId))
            .thenReturn(Optional.of(subscription));

    when(serviceRepository.findById(
            serviceId))
            .thenReturn(Optional.of(serviceEntity));

    when(entitlementRepository
            .findByPlanVersion_PlanVersionIdAndService_ServiceId(
                    planVersionId,
                    serviceId))
            .thenReturn(Optional.of(entitlement));

    assertThrows(
            BusinessException.class,
            () -> service.consume(request));
}

@Test
void consume_ShouldCreateNewConsumption_WhenFirstUsage() {

    CreateConsumptionRequest request =
            new CreateConsumptionRequest();

    request.setCustomerSubscriptionId(
            subscriptionId);

    request.setServiceId(
            serviceId);

    request.setQuantity(
            2);

            
    when(customerSubscriptionRepository.findById(
            subscriptionId))
            .thenReturn(Optional.of(subscription));

    when(serviceRepository.findById(
            serviceId))
            .thenReturn(Optional.of(serviceEntity));

    when(entitlementRepository
            .findByPlanVersion_PlanVersionIdAndService_ServiceId(
                    planVersionId,
                    serviceId))
            .thenReturn(Optional.of(entitlement));

 when(consumptionRepository
        .findByCustomerSubscription_CustomerSubscriptionIdAndService_ServiceIdAndPeriodEndIsNull(
                subscriptionId,
                serviceId))
        .thenReturn(Optional.empty());

    when(consumptionRepository.save(any()))
            .thenAnswer(invocation ->
                    invocation.getArgument(0));

    ConsumptionResponse response =
            service.consume(request);

    assertEquals(
            2,
            response.getConsumedQuantity());

    assertEquals(
            8,
            response.getRemainingQuantity());

    verify(consumptionRepository)
            .save(any(
                    SubscriptionEntitlementConsumptionEntity.class));
}




}