package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateSubscriptionPlanVersionRequest;
import com.propertypilot.application.dto.SubscriptionPlanVersionResponse;
import com.propertypilot.application.dto.UpdateSubscriptionPlanVersionRequest;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanVersionEntity;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionPlanRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionPlanVersionRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import java.util.Map;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class SubscriptionPlanVersionServiceImplTest {

    @Mock
    private SubscriptionPlanVersionRepository
            subscriptionPlanVersionRepository;

    @Mock
    private SubscriptionPlanRepository
            subscriptionPlanRepository;

    @InjectMocks
    private SubscriptionPlanVersionServiceImpl service;

    private SubscriptionPlanVersionEntity buildEntity() {

    SubscriptionPlanEntity plan =
            new SubscriptionPlanEntity();

    plan.setSubscriptionPlanId(
            UUID.randomUUID());

    SubscriptionPlanVersionEntity entity =
            new SubscriptionPlanVersionEntity();

    entity.setPlanVersionId(
            UUID.randomUUID());

    entity.setSubscriptionPlan(plan);

    entity.setVersionNumber(1);

    entity.setPrice(
            BigDecimal.valueOf(999));

    entity.setCurrencyCode("INR");

    entity.setEffectiveFrom(
            OffsetDateTime.now());

    entity.setStatus("ACTIVE");

  Map<String, Object> benefits =
        new HashMap<>();

benefits.put(
        "feature",
        "Premium Support");

entity.setBenefitsJson(
        benefits);

    return entity;
}
@Test
void createPlanVersion_ShouldCreate() {

    UUID planId = UUID.randomUUID();

    SubscriptionPlanEntity plan =
            new SubscriptionPlanEntity();

    plan.setSubscriptionPlanId(planId);

    CreateSubscriptionPlanVersionRequest request =
            new CreateSubscriptionPlanVersionRequest();

    request.setSubscriptionPlanId(planId);
    request.setPrice(BigDecimal.valueOf(999));
    request.setCurrencyCode("INR");
    request.setEffectiveFrom(OffsetDateTime.now());
    request.setStatus("ACTIVE");
   Map<String, Object> benefits =
        new HashMap<>();

benefits.put(
        "feature",
        "Premium Support");

request.setBenefitsJson(
        benefits);

    SubscriptionPlanVersionEntity saved =
            buildEntity();

    when(subscriptionPlanRepository.findById(planId))
            .thenReturn(Optional.of(plan));

    when(subscriptionPlanVersionRepository
            .findTopBySubscriptionPlan_SubscriptionPlanIdOrderByVersionNumberDesc(
                    planId))
            .thenReturn(Optional.empty());

    when(subscriptionPlanVersionRepository.save(any()))
            .thenReturn(saved);

    SubscriptionPlanVersionResponse response =
            service.createPlanVersion(request);

    assertThat(response).isNotNull();

    verify(subscriptionPlanVersionRepository)
            .save(any());
}
@Test
void createPlanVersion_ShouldThrow_WhenPlanNotFound() {

    UUID planId = UUID.randomUUID();

    CreateSubscriptionPlanVersionRequest request =
            new CreateSubscriptionPlanVersionRequest();

    request.setSubscriptionPlanId(planId);

    when(subscriptionPlanRepository.findById(planId))
            .thenReturn(Optional.empty());

    assertThatThrownBy(
            () -> service.createPlanVersion(request))
            .isInstanceOf(
                    ResourceNotFoundException.class);
}

@Test
void createPlanVersion_ShouldThrow_WhenPriceInvalid() {

    UUID planId = UUID.randomUUID();

    SubscriptionPlanEntity plan =
            new SubscriptionPlanEntity();

    plan.setSubscriptionPlanId(planId);

    CreateSubscriptionPlanVersionRequest request =
            new CreateSubscriptionPlanVersionRequest();

    request.setSubscriptionPlanId(planId);
    request.setPrice(BigDecimal.ZERO);
    request.setStatus("ACTIVE");
    request.setEffectiveFrom(OffsetDateTime.now());

    when(subscriptionPlanRepository.findById(planId))
            .thenReturn(Optional.of(plan));

    assertThatThrownBy(
            () -> service.createPlanVersion(request))
            .isInstanceOf(
                    IllegalArgumentException.class);
}

@Test
void createPlanVersion_ShouldThrow_WhenDatesInvalid() {

    UUID planId = UUID.randomUUID();

    SubscriptionPlanEntity plan =
            new SubscriptionPlanEntity();

    plan.setSubscriptionPlanId(planId);

    OffsetDateTime now =
            OffsetDateTime.now();

    CreateSubscriptionPlanVersionRequest request =
            new CreateSubscriptionPlanVersionRequest();

    request.setSubscriptionPlanId(planId);
    request.setPrice(BigDecimal.valueOf(100));
    request.setStatus("ACTIVE");
    request.setEffectiveFrom(now);
    request.setEffectiveTo(now.minusDays(1));

    when(subscriptionPlanRepository.findById(planId))
            .thenReturn(Optional.of(plan));

    assertThatThrownBy(
            () -> service.createPlanVersion(request))
            .isInstanceOf(
                    IllegalArgumentException.class);
}

@Test
void createPlanVersion_ShouldThrow_WhenStatusInvalid() {

    UUID planId = UUID.randomUUID();

    SubscriptionPlanEntity plan =
            new SubscriptionPlanEntity();

    plan.setSubscriptionPlanId(planId);

    CreateSubscriptionPlanVersionRequest request =
            new CreateSubscriptionPlanVersionRequest();

    request.setSubscriptionPlanId(planId);
    request.setPrice(BigDecimal.valueOf(100));
    request.setStatus("BAD_STATUS");
    request.setEffectiveFrom(OffsetDateTime.now());

    when(subscriptionPlanRepository.findById(planId))
            .thenReturn(Optional.of(plan));

    assertThatThrownBy(
            () -> service.createPlanVersion(request))
            .isInstanceOf(
                    IllegalArgumentException.class);
}

@Test
void getPlanVersion_ShouldReturnVersion() {

    UUID id = UUID.randomUUID();

    SubscriptionPlanVersionEntity entity =
            buildEntity();

    when(subscriptionPlanVersionRepository.findById(id))
            .thenReturn(Optional.of(entity));

    SubscriptionPlanVersionResponse response =
            service.getPlanVersion(id);

    assertThat(response).isNotNull();
}
@Test
void getPlanVersion_ShouldThrow_WhenNotFound() {

    UUID id = UUID.randomUUID();

    when(subscriptionPlanVersionRepository.findById(id))
            .thenReturn(Optional.empty());

    assertThatThrownBy(
            () -> service.getPlanVersion(id))
            .isInstanceOf(
                    ResourceNotFoundException.class);
}
@Test
void getAllPlanVersions_ShouldReturnList() {

    when(subscriptionPlanVersionRepository.findAll())
            .thenReturn(
                    List.of(buildEntity()));

    List<SubscriptionPlanVersionResponse> result =
            service.getAllPlanVersions();

    assertThat(result).hasSize(1);
}
@Test
void updatePlanVersion_ShouldUpdate() {

    UUID id = UUID.randomUUID();

    SubscriptionPlanVersionEntity entity =
            buildEntity();

    UpdateSubscriptionPlanVersionRequest request =
            new UpdateSubscriptionPlanVersionRequest();

    request.setPrice(BigDecimal.valueOf(500));
    request.setCurrencyCode("USD");
    request.setStatus("ACTIVE");

    when(subscriptionPlanVersionRepository.findById(id))
            .thenReturn(Optional.of(entity));

    when(subscriptionPlanVersionRepository.save(any()))
            .thenReturn(entity);

    SubscriptionPlanVersionResponse response =
            service.updatePlanVersion(id, request);

    assertThat(response).isNotNull();

    verify(subscriptionPlanVersionRepository)
            .save(any());
}
@Test
void updatePlanVersion_ShouldThrow_WhenNotFound() {

    UUID id = UUID.randomUUID();

    UpdateSubscriptionPlanVersionRequest request =
            new UpdateSubscriptionPlanVersionRequest();

    when(subscriptionPlanVersionRepository.findById(id))
            .thenReturn(Optional.empty());

    assertThatThrownBy(
            () -> service.updatePlanVersion(id, request))
            .isInstanceOf(
                    ResourceNotFoundException.class);
}
private Map<String, Object> buildBenefits() {

    Map<String, Object> benefits =
            new HashMap<>();

    benefits.put(
            "feature",
            "Premium Support");

    benefits.put(
            "maxProperties",
            100);

    return benefits;
}

@Test
void createPlanVersion_ShouldRetireExistingActiveVersion() {

    UUID planId = UUID.randomUUID();

    SubscriptionPlanEntity plan =
            new SubscriptionPlanEntity();

    plan.setSubscriptionPlanId(planId);

    SubscriptionPlanVersionEntity activeVersion =
            buildEntity();

    activeVersion.setStatus("ACTIVE");

    CreateSubscriptionPlanVersionRequest request =
            new CreateSubscriptionPlanVersionRequest();

    request.setSubscriptionPlanId(planId);
    request.setPrice(BigDecimal.valueOf(999));
    request.setCurrencyCode("INR");
    request.setEffectiveFrom(OffsetDateTime.now());
    request.setStatus("ACTIVE");
    request.setBenefitsJson(buildBenefits());

    when(subscriptionPlanRepository.findById(planId))
            .thenReturn(Optional.of(plan));

    when(subscriptionPlanVersionRepository
            .findBySubscriptionPlan_SubscriptionPlanIdAndStatus(
                    planId,
                    "ACTIVE"))
            .thenReturn(Optional.of(activeVersion));

    when(subscriptionPlanVersionRepository
            .findTopBySubscriptionPlan_SubscriptionPlanIdOrderByVersionNumberDesc(
                    planId))
            .thenReturn(Optional.of(activeVersion));

    when(subscriptionPlanVersionRepository.save(any()))
            .thenReturn(buildEntity());

    service.createPlanVersion(request);

    assertThat(activeVersion.getStatus())
            .isEqualTo("RETIRED");

    verify(subscriptionPlanVersionRepository,
            atLeast(2))
            .save(any());
}

}