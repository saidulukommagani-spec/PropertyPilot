package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateSubscriptionPlanEntitlementRequest;
import com.propertypilot.application.dto.SubscriptionPlanEntitlementResponse;
import com.propertypilot.application.dto.UpdateSubscriptionPlanEntitlementRequest;
import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanEntitlementEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanVersionEntity;
import com.propertypilot.infrastructure.persistence.repository.ServiceRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionPlanEntitlementRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionPlanVersionRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionPlanEntitlementServiceImplTest {

    @Mock
    private SubscriptionPlanEntitlementRepository
            entitlementRepository;

    @Mock
    private SubscriptionPlanVersionRepository
            planVersionRepository;

    @Mock
    private ServiceRepository
            serviceRepository;

    @InjectMocks
    private SubscriptionPlanEntitlementServiceImpl
            service;

    @Test
    void createEntitlement() {

        UUID planVersionId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        UUID entitlementId = UUID.randomUUID();

        CreateSubscriptionPlanEntitlementRequest request =
                new CreateSubscriptionPlanEntitlementRequest();

        request.setPlanVersionId(planVersionId);
        request.setServiceId(serviceId);
        request.setQuantity(5);
        request.setPeriodType("MONTH");
        request.setCarryForwardAllowed(true);
        request.setStatus("ACTIVE");

        SubscriptionPlanVersionEntity planVersion =
                new SubscriptionPlanVersionEntity();

        planVersion.setPlanVersionId(planVersionId);

        ServiceEntity serviceEntity =
                new ServiceEntity();

        serviceEntity.setServiceId(serviceId);

        SubscriptionPlanEntitlementEntity saved =
                new SubscriptionPlanEntitlementEntity();

        saved.setEntitlementId(entitlementId);
        saved.setPlanVersion(planVersion);
        saved.setService(serviceEntity);
        saved.setQuantity(5);
        saved.setPeriodType("MONTH");
        saved.setCarryForwardAllowed(true);
        saved.setStatus("ACTIVE");

        when(planVersionRepository.findById(planVersionId))
                .thenReturn(Optional.of(planVersion));

        when(serviceRepository.findById(serviceId))
                .thenReturn(Optional.of(serviceEntity));

        when(entitlementRepository.save(any()))
                .thenReturn(saved);

        SubscriptionPlanEntitlementResponse response =
                service.createEntitlement(request);

        assertThat(response).isNotNull();
        assertThat(response.getEntitlementId())
                .isEqualTo(entitlementId);

        verify(entitlementRepository).save(any());
    }

    @Test
    void createEntitlement_planVersionNotFound() {

        UUID planVersionId = UUID.randomUUID();

        CreateSubscriptionPlanEntitlementRequest request =
                new CreateSubscriptionPlanEntitlementRequest();

        request.setPlanVersionId(planVersionId);

        when(planVersionRepository.findById(planVersionId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.createEntitlement(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Plan version not found");
    }

    @Test
    void createEntitlement_serviceNotFound() {

        UUID planVersionId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();

        CreateSubscriptionPlanEntitlementRequest request =
                new CreateSubscriptionPlanEntitlementRequest();

        request.setPlanVersionId(planVersionId);
        request.setServiceId(serviceId);

        SubscriptionPlanVersionEntity planVersion =
                new SubscriptionPlanVersionEntity();

        when(planVersionRepository.findById(planVersionId))
                .thenReturn(Optional.of(planVersion));

        when(serviceRepository.findById(serviceId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.createEntitlement(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Service not found");
    }

    @Test
    void getEntitlement() {

        UUID entitlementId = UUID.randomUUID();
        UUID planVersionId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();

        SubscriptionPlanVersionEntity planVersion =
                new SubscriptionPlanVersionEntity();

        planVersion.setPlanVersionId(planVersionId);

        ServiceEntity serviceEntity =
                new ServiceEntity();

        serviceEntity.setServiceId(serviceId);

        SubscriptionPlanEntitlementEntity entity =
                new SubscriptionPlanEntitlementEntity();

        entity.setEntitlementId(entitlementId);
        entity.setPlanVersion(planVersion);
        entity.setService(serviceEntity);
        entity.setQuantity(10);
        entity.setPeriodType("YEAR");
        entity.setCarryForwardAllowed(false);
        entity.setStatus("ACTIVE");

        when(entitlementRepository.findById(entitlementId))
                .thenReturn(Optional.of(entity));

        SubscriptionPlanEntitlementResponse response =
                service.getEntitlement(entitlementId);

        assertThat(response).isNotNull();
        assertThat(response.getEntitlementId())
                .isEqualTo(entitlementId);
    }

    @Test
    void getEntitlement_notFound() {

        UUID entitlementId = UUID.randomUUID();

        when(entitlementRepository.findById(entitlementId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.getEntitlement(entitlementId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Entitlement not found");
    }

    @Test
    void getAllEntitlements() {

        UUID entitlementId = UUID.randomUUID();
        UUID planVersionId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();

        SubscriptionPlanVersionEntity planVersion =
                new SubscriptionPlanVersionEntity();

        planVersion.setPlanVersionId(planVersionId);

        ServiceEntity serviceEntity =
                new ServiceEntity();

        serviceEntity.setServiceId(serviceId);

        SubscriptionPlanEntitlementEntity entity =
                new SubscriptionPlanEntitlementEntity();

        entity.setEntitlementId(entitlementId);
        entity.setPlanVersion(planVersion);
        entity.setService(serviceEntity);
        entity.setQuantity(5);
        entity.setPeriodType("MONTH");
        entity.setCarryForwardAllowed(true);
        entity.setStatus("ACTIVE");

        when(entitlementRepository.findAll())
                .thenReturn(List.of(entity));

        List<SubscriptionPlanEntitlementResponse> responses =
                service.getAllEntitlements();

        assertThat(responses)
                .hasSize(1);

        assertThat(responses.get(0)
                .getEntitlementId())
                .isEqualTo(entitlementId);
    }

    @Test
    void updateEntitlement() {

        UUID entitlementId = UUID.randomUUID();
        UUID planVersionId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();

        SubscriptionPlanVersionEntity planVersion =
                new SubscriptionPlanVersionEntity();

        planVersion.setPlanVersionId(planVersionId);

        ServiceEntity serviceEntity =
                new ServiceEntity();

        serviceEntity.setServiceId(serviceId);

        SubscriptionPlanEntitlementEntity entity =
                new SubscriptionPlanEntitlementEntity();

        entity.setEntitlementId(entitlementId);
        entity.setPlanVersion(planVersion);
        entity.setService(serviceEntity);

        UpdateSubscriptionPlanEntitlementRequest request =
                new UpdateSubscriptionPlanEntitlementRequest();

        request.setQuantity(20);
        request.setPeriodType("YEAR");
        request.setCarryForwardAllowed(false);
        request.setStatus("INACTIVE");

        when(entitlementRepository.findById(entitlementId))
                .thenReturn(Optional.of(entity));

        when(entitlementRepository.save(any()))
                .thenReturn(entity);

        SubscriptionPlanEntitlementResponse response =
                service.updateEntitlement(
                        entitlementId,
                        request);

        assertThat(response).isNotNull();

        verify(entitlementRepository).save(entity);

        assertThat(entity.getQuantity())
                .isEqualTo(20);

        assertThat(entity.getStatus())
                .isEqualTo("INACTIVE");
    }

    @Test
    void updateEntitlement_notFound() {

        UUID entitlementId = UUID.randomUUID();

        when(entitlementRepository.findById(entitlementId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.updateEntitlement(
                        entitlementId,
                        new UpdateSubscriptionPlanEntitlementRequest()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Entitlement not found");
    }
}