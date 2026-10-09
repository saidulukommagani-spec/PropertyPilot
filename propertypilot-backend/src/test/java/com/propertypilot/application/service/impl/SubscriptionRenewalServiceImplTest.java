package com.propertypilot.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.OffsetDateTime;

import com.propertypilot.application.dto.CreateSubscriptionRenewalRequest;
import com.propertypilot.application.dto.SubscriptionRenewalResponse;
import com.propertypilot.application.service.BillingService;
import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanVersionEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionRenewalEntity;
import com.propertypilot.infrastructure.persistence.entity.PaymentEntity;
import com.propertypilot.infrastructure.persistence.entity.InvoiceEntity;
import com.propertypilot.infrastructure.persistence.entity.DealEntity;
import com.propertypilot.domain.enums.PaymentStatus;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionPlanVersionRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionRenewalRepository;
import com.propertypilot.infrastructure.persistence.repository.PaymentRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import com.propertypilot.web.exception.BusinessException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import com.propertypilot.security.SecurityService;
@ExtendWith(MockitoExtension.class)
class SubscriptionRenewalServiceImplTest {
    @Mock private SecurityService securityService;

    @Mock
    private SubscriptionRenewalRepository renewalRepository;

    @Mock
    private CustomerSubscriptionRepository
            customerSubscriptionRepository;

    @Mock
    private SubscriptionPlanVersionRepository
            planVersionRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BillingService billingService;

    @InjectMocks
    private SubscriptionRenewalServiceImpl service;

private CustomerSubscriptionEntity
buildSubscription(UUID subscriptionId) {

    SubscriptionPlanEntity plan =
            new SubscriptionPlanEntity();

    plan.setSubscriptionPlanId(
            UUID.randomUUID());

    SubscriptionPlanVersionEntity version =
            new SubscriptionPlanVersionEntity();

    version.setSubscriptionPlan(plan);

    CustomerSubscriptionEntity subscription =
            new CustomerSubscriptionEntity();

    subscription.setCustomerSubscriptionId(
            subscriptionId);

    subscription.setPlanVersion(
            version);

    subscription.setAutoRenew(true);

    com.propertypilot.infrastructure.persistence.entity.CustomerEntity customer =
            new com.propertypilot.infrastructure.persistence.entity.CustomerEntity();
    customer.setCustomerId(UUID.randomUUID());
    subscription.setCustomer(customer);

    subscription.setStatus("ACTIVE");

    return subscription;
}

@Test
void renewSubscription_success() {

    UUID subscriptionId =
            UUID.randomUUID();

    UUID paymentId =
            UUID.randomUUID();

    CreateSubscriptionRenewalRequest request =
            new CreateSubscriptionRenewalRequest();

    request.setCustomerSubscriptionId(
            subscriptionId);

    request.setPaymentId(paymentId);

    request.setPeriodStart(
            LocalDate.now());

    request.setPeriodEnd(
            LocalDate.now().plusMonths(1));

    request.setRenewalType(
            "MANUAL");

    request.setRenewalAmount(
            BigDecimal.valueOf(999));

    request.setRemarks(
            "Renewed");

    CustomerSubscriptionEntity existing =
            buildSubscription(
                    subscriptionId);

    SubscriptionPlanVersionEntity latestVersion =
            new SubscriptionPlanVersionEntity();

    SubscriptionPlanEntity plan =
            new SubscriptionPlanEntity();

    plan.setSubscriptionPlanId(
            existing.getPlanVersion()
                    .getSubscriptionPlan()
                    .getSubscriptionPlanId());

    latestVersion.setSubscriptionPlan(
            plan);
    latestVersion.setPrice(BigDecimal.valueOf(999));

    PaymentEntity payment = new PaymentEntity();
    payment.setPaymentStatus(PaymentStatus.SUCCESS);
    payment.setSettledAt(OffsetDateTime.now());
    payment.setAmount(BigDecimal.valueOf(999));
    InvoiceEntity invoice = new InvoiceEntity();
    invoice.setCustomer(existing.getCustomer());
    invoice.setEntityType(com.propertypilot.domain.enums.BillingEntityType.SUBSCRIPTION);
    invoice.setEntityId(subscriptionId);
    invoice.setInvoiceType(com.propertypilot.domain.enums.InvoiceType.SUBSCRIPTION_RENEWAL);
    invoice.setStatus(com.propertypilot.domain.enums.InvoiceStatus.PAID);
    invoice.setInvoiceAmount(new BigDecimal("999.00"));
    invoice.setTaxAmount(BigDecimal.ZERO);
    invoice.setCurrencyCode("INR");
    invoice.setRenewalPeriodStart(request.getPeriodStart());
    invoice.setRenewalPeriodEnd(request.getPeriodEnd());
    payment.setInvoice(invoice);
    payment.setCurrencyCode("INR");

    when(customerSubscriptionRepository.findById(
            subscriptionId))
            .thenReturn(Optional.of(existing));

    when(planVersionRepository
            .findTopBySubscriptionPlan_SubscriptionPlanIdOrderByVersionNumberDesc(
                    any()))
            .thenReturn(Optional.of(
                    latestVersion));

    when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

    when(customerSubscriptionRepository.save(
            any(CustomerSubscriptionEntity.class)))
            .thenAnswer(inv -> inv.getArgument(0));

    when(renewalRepository.saveAndFlush(
            any(SubscriptionRenewalEntity.class)))
            .thenAnswer(invocation -> {

                SubscriptionRenewalEntity entity =
                        invocation.getArgument(0);

                entity.setSubscriptionRenewalId(
                        UUID.randomUUID());

                return entity;
            });

    SubscriptionRenewalResponse response =
            service.renewSubscription(
                    request);

    assertThat(response)
            .isNotNull();

    assertThat(response.getStatus())
            .isEqualTo("SUCCESS");

    assertThat(existing.getStatus())
            .isEqualTo("EXPIRED");
}

@Test
void renewSubscription_rejectsPaymentAlreadyUsedByAnotherRenewal() {
    UUID subscriptionId = UUID.randomUUID();
    UUID paymentId = UUID.randomUUID();
    CustomerSubscriptionEntity existing = buildSubscription(subscriptionId);
    CreateSubscriptionRenewalRequest request = new CreateSubscriptionRenewalRequest();
    request.setCustomerSubscriptionId(subscriptionId);
    request.setPaymentId(paymentId);
    request.setPeriodStart(LocalDate.now());
    request.setPeriodEnd(LocalDate.now().plusMonths(1));
    request.setRenewalAmount(new BigDecimal("999.00"));

    SubscriptionPlanVersionEntity latestVersion = new SubscriptionPlanVersionEntity();
    latestVersion.setPrice(new BigDecimal("999.00"));
    PaymentEntity payment = new PaymentEntity();
    payment.setPaymentStatus(PaymentStatus.SUCCESS);
    payment.setSettledAt(OffsetDateTime.now());
    payment.setAmount(new BigDecimal("999.00"));
    InvoiceEntity invoice = new InvoiceEntity();
    invoice.setCustomer(existing.getCustomer());
    invoice.setEntityType(com.propertypilot.domain.enums.BillingEntityType.SUBSCRIPTION);
    invoice.setEntityId(subscriptionId);
    invoice.setInvoiceType(com.propertypilot.domain.enums.InvoiceType.SUBSCRIPTION_RENEWAL);
    invoice.setStatus(com.propertypilot.domain.enums.InvoiceStatus.PAID);
    invoice.setInvoiceAmount(new BigDecimal("999.00"));
    invoice.setTaxAmount(BigDecimal.ZERO);
    invoice.setCurrencyCode("INR");
    invoice.setRenewalPeriodStart(request.getPeriodStart());
    invoice.setRenewalPeriodEnd(request.getPeriodEnd());
    payment.setInvoice(invoice);
    payment.setCurrencyCode("INR");

    when(customerSubscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(existing));
    when(planVersionRepository.findTopBySubscriptionPlan_SubscriptionPlanIdOrderByVersionNumberDesc(any()))
            .thenReturn(Optional.of(latestVersion));
    when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
    when(renewalRepository.existsByPaymentId(paymentId)).thenReturn(true);

    assertThatThrownBy(() -> service.renewSubscription(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage("Payment was already applied to a renewal");
    verify(customerSubscriptionRepository, never()).save(any(CustomerSubscriptionEntity.class));
    verify(renewalRepository, never()).saveAndFlush(any(SubscriptionRenewalEntity.class));
}

@Test
void renewSubscription_translatesConcurrentPaymentReuseConstraintViolation() {
    UUID subscriptionId = UUID.randomUUID();
    UUID paymentId = UUID.randomUUID();
    CustomerSubscriptionEntity existing = buildSubscription(subscriptionId);
    CreateSubscriptionRenewalRequest request = new CreateSubscriptionRenewalRequest();
    request.setCustomerSubscriptionId(subscriptionId);
    request.setPaymentId(paymentId);
    request.setPeriodStart(LocalDate.now());
    request.setPeriodEnd(LocalDate.now().plusMonths(1));
    request.setRenewalAmount(new BigDecimal("999.00"));

    SubscriptionPlanVersionEntity latestVersion = new SubscriptionPlanVersionEntity();
    latestVersion.setPrice(new BigDecimal("999.00"));
    PaymentEntity payment = new PaymentEntity();
    payment.setPaymentStatus(PaymentStatus.SUCCESS);
    payment.setSettledAt(OffsetDateTime.now());
    payment.setAmount(new BigDecimal("999.00"));
    InvoiceEntity invoice = new InvoiceEntity();
    invoice.setCustomer(existing.getCustomer());
    invoice.setEntityType(com.propertypilot.domain.enums.BillingEntityType.SUBSCRIPTION);
    invoice.setEntityId(subscriptionId);
    invoice.setInvoiceType(com.propertypilot.domain.enums.InvoiceType.SUBSCRIPTION_RENEWAL);
    invoice.setStatus(com.propertypilot.domain.enums.InvoiceStatus.PAID);
    invoice.setInvoiceAmount(new BigDecimal("999.00"));
    invoice.setTaxAmount(BigDecimal.ZERO);
    invoice.setCurrencyCode("INR");
    invoice.setRenewalPeriodStart(request.getPeriodStart());
    invoice.setRenewalPeriodEnd(request.getPeriodEnd());
    payment.setInvoice(invoice);
    payment.setCurrencyCode("INR");

    when(customerSubscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(existing));
    when(planVersionRepository.findTopBySubscriptionPlan_SubscriptionPlanIdOrderByVersionNumberDesc(any()))
            .thenReturn(Optional.of(latestVersion));
    when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
    when(customerSubscriptionRepository.save(any(CustomerSubscriptionEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
    var databaseConstraint = new org.hibernate.exception.ConstraintViolationException(
            "duplicate renewal payment", null, "uq_subscription_renewals_payment_id");
    when(renewalRepository.saveAndFlush(any(SubscriptionRenewalEntity.class)))
            .thenThrow(new DataIntegrityViolationException("unique payment", databaseConstraint));

    assertThatThrownBy(() -> service.renewSubscription(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage("Payment was already applied to a renewal");
}

@Test
void renewSubscription_customerSubscriptionNotFound() {

    UUID id =
            UUID.randomUUID();

    CreateSubscriptionRenewalRequest request =
            new CreateSubscriptionRenewalRequest();

    request.setCustomerSubscriptionId(id);

    when(customerSubscriptionRepository.findById(id))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.renewSubscription(request))
            .isInstanceOf(
                    ResourceNotFoundException.class)
            .hasMessage(
                    "Customer Subscription not found");
}

@Test
void renewSubscription_latestPlanVersionNotFound() {

    UUID subscriptionId =
            UUID.randomUUID();

    CreateSubscriptionRenewalRequest request =
            new CreateSubscriptionRenewalRequest();

    request.setCustomerSubscriptionId(
            subscriptionId);
    request.setPaymentId(UUID.randomUUID());
    request.setPeriodStart(LocalDate.now());
    request.setPeriodEnd(LocalDate.now().plusMonths(1));

    CustomerSubscriptionEntity existing =
            buildSubscription(
                    subscriptionId);

    when(customerSubscriptionRepository.findById(
            subscriptionId))
            .thenReturn(Optional.of(existing));

    when(planVersionRepository
            .findTopBySubscriptionPlan_SubscriptionPlanIdOrderByVersionNumberDesc(
                    any()))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.renewSubscription(request))
            .isInstanceOf(
                    ResourceNotFoundException.class)
            .hasMessage(
                    "Latest Plan Version not found");
}

@Test
void renewSubscription_rejectsUnsettledPaymentBeforeCreatingRenewal() {
    UUID subscriptionId = UUID.randomUUID();
    UUID paymentId = UUID.randomUUID();
    CustomerSubscriptionEntity existing = buildSubscription(subscriptionId);
    CreateSubscriptionRenewalRequest request = new CreateSubscriptionRenewalRequest();
    request.setCustomerSubscriptionId(subscriptionId);
    request.setPaymentId(paymentId);
    request.setPeriodStart(LocalDate.now());
    request.setPeriodEnd(LocalDate.now().plusMonths(1));
    request.setRenewalAmount(BigDecimal.valueOf(999));

    PaymentEntity unsettledPayment = new PaymentEntity();
    unsettledPayment.setPaymentStatus(PaymentStatus.PENDING);
    DealEntity deal = new DealEntity();
    CustomerSubscriptionEntity linkedSubscription = new CustomerSubscriptionEntity();
    linkedSubscription.setCustomerSubscriptionId(subscriptionId);
    deal.setCustomerSubscription(linkedSubscription);
    InvoiceEntity invoice = new InvoiceEntity();
    invoice.setDeal(deal);
    unsettledPayment.setInvoice(invoice);

    SubscriptionPlanVersionEntity latestVersion = new SubscriptionPlanVersionEntity();
    latestVersion.setPrice(BigDecimal.valueOf(999));
    when(customerSubscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(existing));
    when(planVersionRepository.findTopBySubscriptionPlan_SubscriptionPlanIdOrderByVersionNumberDesc(any()))
            .thenReturn(Optional.of(latestVersion));
    when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(unsettledPayment));

    assertThatThrownBy(() -> service.renewSubscription(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage("Renewal requires verified settlement of the invoice issued for this renewal period");
    verify(customerSubscriptionRepository, never()).save(any(CustomerSubscriptionEntity.class));
    verify(renewalRepository, never()).save(any(SubscriptionRenewalEntity.class));
}

@Test
void renewSubscription_rejectsInitialSubscriptionPurchasePayment() {
    UUID subscriptionId = UUID.randomUUID();
    UUID paymentId = UUID.randomUUID();
    CustomerSubscriptionEntity existing = buildSubscription(subscriptionId);
    CreateSubscriptionRenewalRequest request = new CreateSubscriptionRenewalRequest();
    request.setCustomerSubscriptionId(subscriptionId);
    request.setPaymentId(paymentId);
    request.setPeriodStart(LocalDate.now());
    request.setPeriodEnd(LocalDate.now().plusYears(1));
    request.setRenewalAmount(new BigDecimal("999.00"));

    SubscriptionPlanVersionEntity latestVersion = new SubscriptionPlanVersionEntity();
    latestVersion.setPrice(new BigDecimal("999.00"));
    InvoiceEntity originalPurchaseInvoice = new InvoiceEntity();
    originalPurchaseInvoice.setCustomer(existing.getCustomer());
    originalPurchaseInvoice.setEntityType(com.propertypilot.domain.enums.BillingEntityType.SUBSCRIPTION);
    originalPurchaseInvoice.setEntityId(subscriptionId);
    originalPurchaseInvoice.setInvoiceType(com.propertypilot.domain.enums.InvoiceType.SUBSCRIPTION_FEE);
    originalPurchaseInvoice.setStatus(com.propertypilot.domain.enums.InvoiceStatus.PAID);
    PaymentEntity verifiedPurchasePayment = new PaymentEntity();
    verifiedPurchasePayment.setPaymentStatus(PaymentStatus.SUCCESS);
    verifiedPurchasePayment.setSettledAt(OffsetDateTime.now());
    verifiedPurchasePayment.setAmount(new BigDecimal("999.00"));
    verifiedPurchasePayment.setCurrencyCode("INR");
    verifiedPurchasePayment.setInvoice(originalPurchaseInvoice);

    when(customerSubscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(existing));
    when(planVersionRepository.findTopBySubscriptionPlan_SubscriptionPlanIdOrderByVersionNumberDesc(any()))
            .thenReturn(Optional.of(latestVersion));
    when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(verifiedPurchasePayment));

    assertThatThrownBy(() -> service.renewSubscription(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage("Renewal requires verified settlement of the invoice issued for this renewal period");
    verify(customerSubscriptionRepository, never()).save(any(CustomerSubscriptionEntity.class));
}

@Test
void getRenewal_success() {

    UUID renewalId =
            UUID.randomUUID();

    SubscriptionRenewalEntity entity =
            new SubscriptionRenewalEntity();

    entity.setSubscriptionRenewalId(
            renewalId);

    CustomerSubscriptionEntity subscription =
            new CustomerSubscriptionEntity();

    subscription.setCustomerSubscriptionId(
            UUID.randomUUID());

    entity.setCustomerSubscription(
            subscription);

    entity.setStatus("SUCCESS");

    when(renewalRepository.findById(
            renewalId))
            .thenReturn(Optional.of(entity));

    SubscriptionRenewalResponse response =
            service.getRenewal(
                    renewalId);

    assertThat(response)
            .isNotNull();

    assertThat(response.getStatus())
            .isEqualTo("SUCCESS");
}

@Test
void getRenewal_notFound() {

    UUID renewalId =
            UUID.randomUUID();

    when(renewalRepository.findById(
            renewalId))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.getRenewal(
                    renewalId))
            .isInstanceOf(
                    ResourceNotFoundException.class)
            .hasMessage(
                    "Subscription Renewal not found");
}

@Test
void getRenewalsBySubscription_success() {

    UUID subscriptionId =
            UUID.randomUUID();
    when(customerSubscriptionRepository.findById(subscriptionId))
            .thenReturn(Optional.of(buildSubscription(subscriptionId)));

    SubscriptionRenewalEntity entity =
            new SubscriptionRenewalEntity();

    CustomerSubscriptionEntity subscription =
            new CustomerSubscriptionEntity();

    subscription.setCustomerSubscriptionId(
            subscriptionId);

    entity.setCustomerSubscription(
            subscription);

    entity.setStatus(
            "SUCCESS");

    when(renewalRepository
            .findByCustomerSubscription_CustomerSubscriptionId(
                    subscriptionId))
            .thenReturn(List.of(entity));

    List<SubscriptionRenewalResponse> result =
            service.getRenewalsBySubscription(
                    subscriptionId);

    assertThat(result)
            .hasSize(1);
}

@Test
void getRenewal_withNewSubscription() {

    UUID renewalId =
            UUID.randomUUID();

    SubscriptionRenewalEntity entity =
            new SubscriptionRenewalEntity();

    entity.setSubscriptionRenewalId(
            renewalId);

    CustomerSubscriptionEntity oldSub =
            new CustomerSubscriptionEntity();

    oldSub.setCustomerSubscriptionId(
            UUID.randomUUID());

    CustomerSubscriptionEntity newSub =
            new CustomerSubscriptionEntity();

    newSub.setCustomerSubscriptionId(
            UUID.randomUUID());

    entity.setCustomerSubscription(
            oldSub);

    entity.setNewCustomerSubscription(
            newSub);

    when(renewalRepository.findById(
            renewalId))
            .thenReturn(Optional.of(entity));

    SubscriptionRenewalResponse response =
            service.getRenewal(
                    renewalId);

    assertThat(
            response.getNewCustomerSubscriptionId())
            .isEqualTo(
                    newSub.getCustomerSubscriptionId());
}

@Test
void getRenewalsBySubscription_emptyList() {

    UUID subscriptionId =
            UUID.randomUUID();
    when(customerSubscriptionRepository.findById(subscriptionId))
            .thenReturn(Optional.of(buildSubscription(subscriptionId)));

    when(renewalRepository
            .findByCustomerSubscription_CustomerSubscriptionId(
                    subscriptionId))
            .thenReturn(List.of());

    List<SubscriptionRenewalResponse> result =
            service.getRenewalsBySubscription(
                    subscriptionId);

    assertThat(result)
            .isEmpty();
}





}

