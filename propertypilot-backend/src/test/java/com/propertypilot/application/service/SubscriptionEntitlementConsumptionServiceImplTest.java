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
import com.propertypilot.security.SecurityService;


import java.util.Optional;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;
import java.time.Clock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;

@ExtendWith(MockitoExtension.class)
class SubscriptionEntitlementConsumptionServiceImplTest {
    @Mock private SecurityService securityService;
    @Mock private Clock businessClock;

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

    @Mock private SubscriptionPaymentVerificationService paymentVerificationService;

    @InjectMocks
    private SubscriptionEntitlementConsumptionServiceImpl service;

    private UUID subscriptionId;
    private UUID serviceId;
    private UUID planVersionId;

    private CustomerSubscriptionEntity subscription;
    private ServiceEntity serviceEntity;
    private SubscriptionPlanEntitlementEntity entitlement;
    private CustomerEntity customer;

    @BeforeEach
    void setUp() {

        Clock systemClock = Clock.systemDefaultZone();
        lenient().when(businessClock.instant()).thenAnswer(invocation -> systemClock.instant());
        lenient().when(businessClock.getZone()).thenReturn(systemClock.getZone());

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

        customer = new CustomerEntity();
        customer.setCustomerId(UUID.randomUUID());
        subscription.setCustomer(customer);
        subscription.setStartDate(LocalDate.now().minusDays(1));
        subscription.setEndDate(LocalDate.now().plusDays(30));

        subscription.setPlanVersion(
                version);
        org.mockito.Mockito.lenient()
                .when(paymentVerificationService.isPaymentVerified(subscription))
                .thenReturn(true);

        serviceEntity =
                new ServiceEntity();

        serviceEntity.setServiceId(
                serviceId);
        serviceEntity.setServiceName("Window cleaning");

        entitlement =
                new SubscriptionPlanEntitlementEntity();

        entitlement.setQuantity(
                10);
        entitlement.setService(serviceEntity);
        entitlement.setStatus("ACTIVE");
    }

@Test
void consume_ShouldThrow_WhenSubscriptionNotFound() {

    CreateConsumptionRequest request =
            new CreateConsumptionRequest();

    request.setCustomerSubscriptionId(
            subscriptionId);

    when(customerSubscriptionRepository.findByIdForUpdate(
            subscriptionId))
            .thenReturn(Optional.empty());

    assertThrows(
            ResourceNotFoundException.class,
            () -> service.consume(request));

    verify(customerSubscriptionRepository).findByIdForUpdate(subscriptionId);
}

@Test
void getUsage_ReturnsServiceNameForCustomerDisplay() {
    when(customerSubscriptionRepository.findById(subscriptionId))
            .thenReturn(Optional.of(subscription));
    when(entitlementRepository.findByPlanVersion_PlanVersionId(planVersionId))
            .thenReturn(List.of(entitlement));
    when(consumptionRepository
            .findByCustomerSubscription_CustomerSubscriptionIdAndService_ServiceIdAndPeriodEndIsNull(
                    subscriptionId, serviceId))
            .thenReturn(List.of());

    List<SubscriptionUsageResponse> result = service.getUsage(subscriptionId);

    assertEquals(1, result.size());
    assertEquals(serviceId, result.get(0).getServiceId());
    assertEquals("Window cleaning", result.get(0).getServiceName());
}

@Test
void consume_ShouldThrow_WhenSubscriptionInactive() {

    subscription.setStatus("EXPIRED");

    CreateConsumptionRequest request =
            new CreateConsumptionRequest();

    request.setCustomerSubscriptionId(
            subscriptionId);

    when(customerSubscriptionRepository.findByIdForUpdate(
            subscriptionId))
            .thenReturn(Optional.of(subscription));

    assertThrows(
            BusinessException.class,
            () -> service.consume(request));
}

@Test
void consume_ShouldRejectPaidSubscriptionWithoutVerifiedPurchasePayment() {
    CreateConsumptionRequest request = new CreateConsumptionRequest();
    request.setCustomerSubscriptionId(subscriptionId);
    request.setServiceId(serviceId);

    when(customerSubscriptionRepository.findByIdForUpdate(subscriptionId))
            .thenReturn(Optional.of(subscription));
    when(paymentVerificationService.isPaymentVerified(subscription)).thenReturn(false);

    BusinessException error = assertThrows(BusinessException.class, () -> service.consume(request));

    assertTrue(error.getMessage().contains("require verified payment"));
    verify(paymentVerificationService).isPaymentVerified(subscription);
    verify(serviceRepository, never()).findById(any());
    verify(consumptionRepository, never()).save(any());
}

@Test
void consume_AllowsPaidSubscriptionWithFullyVerifiedPurchasePayment() {
    CreateConsumptionRequest request = new CreateConsumptionRequest();
    request.setCustomerSubscriptionId(subscriptionId);
    request.setServiceId(serviceId);
    request.setQuantity(1);

    when(customerSubscriptionRepository.findByIdForUpdate(subscriptionId))
            .thenReturn(Optional.of(subscription));
    when(paymentVerificationService.isPaymentVerified(subscription)).thenReturn(true);
    when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(serviceEntity));
    when(entitlementRepository.findByPlanVersion_PlanVersionIdAndService_ServiceId(planVersionId, serviceId))
            .thenReturn(Optional.of(entitlement));
    when(consumptionRepository
            .findByCustomerSubscription_CustomerSubscriptionIdAndService_ServiceIdAndPeriodEndIsNull(
                    subscriptionId, serviceId))
            .thenReturn(List.of());
    when(consumptionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    ConsumptionResponse response = service.consume(request);

    assertEquals(1, response.getConsumedQuantity());
    assertEquals(9, response.getRemainingQuantity());
    verify(paymentVerificationService).isPaymentVerified(subscription);
}

@Test
void consume_ShouldThrow_WhenSubscriptionDatesAreOutsideCurrentTerm() {
    subscription.setStartDate(LocalDate.now().plusDays(1));
    CreateConsumptionRequest request = new CreateConsumptionRequest();
    request.setCustomerSubscriptionId(subscriptionId);

    when(customerSubscriptionRepository.findByIdForUpdate(subscriptionId))
            .thenReturn(Optional.of(subscription));

    assertThrows(BusinessException.class, () -> service.consume(request));
    verify(serviceRepository, never()).findById(any());
}

@Test
void consume_ShouldThrow_WhenServiceNotFound() {

    CreateConsumptionRequest request =
            new CreateConsumptionRequest();

    request.setCustomerSubscriptionId(
            subscriptionId);

    request.setServiceId(
            serviceId);

    when(customerSubscriptionRepository.findByIdForUpdate(
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

    when(customerSubscriptionRepository.findByIdForUpdate(
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
void consume_ShouldThrow_WhenEntitlementInactive() {
    entitlement.setStatus("INACTIVE");
    CreateConsumptionRequest request = new CreateConsumptionRequest();
    request.setCustomerSubscriptionId(subscriptionId);
    request.setServiceId(serviceId);

    when(customerSubscriptionRepository.findByIdForUpdate(subscriptionId))
            .thenReturn(Optional.of(subscription));
    when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(serviceEntity));
    when(entitlementRepository.findByPlanVersion_PlanVersionIdAndService_ServiceId(planVersionId, serviceId))
            .thenReturn(Optional.of(entitlement));

    assertThrows(BusinessException.class, () -> service.consume(request));
    verify(consumptionRepository, never()).save(any());
}

@Test
void consume_ShouldNotApplyEntitlementToClosedServiceRequest() {
    UUID requestId = UUID.randomUUID();
    ServiceRequestEntity requestEntity = new ServiceRequestEntity();
    requestEntity.setServiceRequestId(requestId);
    requestEntity.setCustomer(customer);
    requestEntity.setService(serviceEntity);
    requestEntity.setStatus("COMPLETED");

    CreateConsumptionRequest request = new CreateConsumptionRequest();
    request.setCustomerSubscriptionId(subscriptionId);
    request.setServiceId(serviceId);
    request.setServiceRequestId(requestId);
    request.setQuantity(1);

    when(customerSubscriptionRepository.findByIdForUpdate(subscriptionId))
            .thenReturn(Optional.of(subscription));
    when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(serviceEntity));
    when(entitlementRepository.findByPlanVersion_PlanVersionIdAndService_ServiceId(planVersionId, serviceId))
            .thenReturn(Optional.of(entitlement));
    when(serviceRequestRepository.findByIdForUpdate(requestId)).thenReturn(Optional.of(requestEntity));

    assertThrows(BusinessException.class, () -> service.consume(request));
    verify(consumptionRepository, never()).save(any());
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

    when(customerSubscriptionRepository.findByIdForUpdate(
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

            
    when(customerSubscriptionRepository.findByIdForUpdate(
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
        .thenReturn(List.of());

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

    ArgumentCaptor<SubscriptionEntitlementConsumptionEntity> savedConsumption =
            ArgumentCaptor.forClass(SubscriptionEntitlementConsumptionEntity.class);
    verify(consumptionRepository).save(savedConsumption.capture());
    assertNull(savedConsumption.getValue().getPeriodEnd(),
            "New active entitlement balances use a null end date");
}

@Test
void consume_ShouldKeepEachRequestConsumptionAndReturnAggregateUsage() {
    UUID requestId = UUID.randomUUID();
    ServiceRequestEntity requestEntity = new ServiceRequestEntity();
    requestEntity.setServiceRequestId(requestId);
    requestEntity.setCustomer(customer);
    requestEntity.setService(serviceEntity);

    SubscriptionEntitlementConsumptionEntity previous =
            new SubscriptionEntitlementConsumptionEntity();
    previous.setCustomerSubscription(subscription);
    previous.setService(serviceEntity);
    previous.setConsumedQuantity(2);

    CreateConsumptionRequest request = new CreateConsumptionRequest();
    request.setCustomerSubscriptionId(subscriptionId);
    request.setServiceId(serviceId);
    request.setServiceRequestId(requestId);
    request.setQuantity(1);

    when(customerSubscriptionRepository.findByIdForUpdate(subscriptionId))
            .thenReturn(Optional.of(subscription));
    when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(serviceEntity));
    when(entitlementRepository.findByPlanVersion_PlanVersionIdAndService_ServiceId(planVersionId, serviceId))
            .thenReturn(Optional.of(entitlement));
    when(serviceRequestRepository.findByIdForUpdate(requestId)).thenReturn(Optional.of(requestEntity));
    when(consumptionRepository.findByServiceRequest_ServiceRequestIdAndPeriodEndIsNull(requestId))
            .thenReturn(Optional.empty());
    when(consumptionRepository
            .findByCustomerSubscription_CustomerSubscriptionIdAndService_ServiceIdAndPeriodEndIsNull(
                    subscriptionId, serviceId))
            .thenReturn(List.of(previous));
    when(consumptionRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

    ConsumptionResponse response = service.consume(request);

    assertEquals(3, response.getConsumedQuantity());
    assertEquals(7, response.getRemainingQuantity());
    ArgumentCaptor<SubscriptionEntitlementConsumptionEntity> saved =
            ArgumentCaptor.forClass(SubscriptionEntitlementConsumptionEntity.class);
    verify(consumptionRepository).save(saved.capture());
    assertSame(requestEntity, saved.getValue().getServiceRequest());
    assertEquals(1, saved.getValue().getConsumedQuantity());
}

@Test
void consume_ShouldBeIdempotentForAnAlreadyConsumedServiceRequest() {
    UUID requestId = UUID.randomUUID();
    ServiceRequestEntity requestEntity = new ServiceRequestEntity();
    requestEntity.setServiceRequestId(requestId);
    requestEntity.setCustomer(customer);
    requestEntity.setService(serviceEntity);

    SubscriptionEntitlementConsumptionEntity previous =
            new SubscriptionEntitlementConsumptionEntity();
    previous.setCustomerSubscription(subscription);
    previous.setService(serviceEntity);
    previous.setServiceRequest(requestEntity);
    previous.setConsumedQuantity(1);

    CreateConsumptionRequest request = new CreateConsumptionRequest();
    request.setCustomerSubscriptionId(subscriptionId);
    request.setServiceId(serviceId);
    request.setServiceRequestId(requestId);
    request.setQuantity(1);

    when(customerSubscriptionRepository.findByIdForUpdate(subscriptionId))
            .thenReturn(Optional.of(subscription));
    when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(serviceEntity));
    when(entitlementRepository.findByPlanVersion_PlanVersionIdAndService_ServiceId(planVersionId, serviceId))
            .thenReturn(Optional.of(entitlement));
    when(serviceRequestRepository.findByIdForUpdate(requestId)).thenReturn(Optional.of(requestEntity));
    when(consumptionRepository.findByServiceRequest_ServiceRequestIdAndPeriodEndIsNull(requestId))
            .thenReturn(Optional.of(previous));
    when(consumptionRepository
            .findByCustomerSubscription_CustomerSubscriptionIdAndService_ServiceIdAndPeriodEndIsNull(
                    subscriptionId, serviceId))
            .thenReturn(List.of(previous));

    ConsumptionResponse response = service.consume(request);

    assertEquals(1, response.getConsumedQuantity());
    assertEquals(9, response.getRemainingQuantity());
    verify(consumptionRepository, never()).save(any());
    verify(customerSubscriptionRepository).findByIdForUpdate(subscriptionId);
}




}
