package com.propertypilot.application.service.impl;

import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.PricingEstimateEntity;
import com.propertypilot.infrastructure.persistence.entity.Property;
import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.infrastructure.persistence.entity.UserEntity;
import com.propertypilot.application.service.ServiceRequestService;
import com.propertypilot.application.dto.CreateServiceRequestRequest;
import com.propertypilot.application.dto.ServiceRequestResponse;
import com.propertypilot.infrastructure.persistence.repository.CouponRedemptionRepository;
import com.propertypilot.infrastructure.persistence.repository.CouponRepository;
import com.propertypilot.infrastructure.persistence.repository.PricingDiscountApplicationRepository;
import com.propertypilot.infrastructure.persistence.repository.PricingEstimateRepository;
import com.propertypilot.infrastructure.persistence.repository.PricingQuoteLineItemRepository;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class QuoteServiceImplTest {
    private final PricingEstimateRepository estimates = mock(PricingEstimateRepository.class);
    private final PricingQuoteLineItemRepository lineItems = mock(PricingQuoteLineItemRepository.class);
    private final PricingDiscountApplicationRepository discounts = mock(PricingDiscountApplicationRepository.class);
    private final CouponRepository coupons = mock(CouponRepository.class);
    private final CouponRedemptionRepository redemptions = mock(CouponRedemptionRepository.class);
    private final SecurityService security = mock(SecurityService.class);
    private final ServiceRequestService requests = mock(ServiceRequestService.class);
    private final QuoteServiceImpl service = new QuoteServiceImpl(
            estimates, lineItems, discounts, coupons, redemptions, security, requests);

    private PricingEstimateEntity estimate;
    private UUID quoteId;
    private UUID actorId;

    @BeforeEach
    void setUp() {
        quoteId = UUID.randomUUID();
        actorId = UUID.randomUUID();
        CustomerEntity customer = new CustomerEntity();
        customer.setCustomerId(UUID.randomUUID());
        UserEntity customerUser = new UserEntity();
        customerUser.setFullName("Asha Customer");
        customerUser.setEmail("asha@example.test");
        customer.setUser(customerUser);
        Property property = new Property();
        property.setPropertyId(UUID.randomUUID());
        property.setTitle("Lakeview Residence");
        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setServiceId(UUID.randomUUID());
        serviceEntity.setServiceName("Property inspection");
        estimate = new PricingEstimateEntity();
        estimate.setPricingEstimateId(quoteId);
        estimate.setCustomer(customer);
        estimate.setProperty(property);
        estimate.setService(serviceEntity);
        estimate.setSubtotal(new BigDecimal("1000.00"));
        estimate.setDiscountAmount(new BigDecimal("100.00"));
        estimate.setTaxAmount(new BigDecimal("162.00"));
        estimate.setTotalAmount(new BigDecimal("1062.00"));
        estimate.setCurrencyCode("INR");
        estimate.setStatus("ACTIVE");
        estimate.setExpiresAt(OffsetDateTime.now().plusMinutes(15));
        estimate.setCreatedAt(OffsetDateTime.now().minusDays(1));
        estimate.setCalculationSnapshot(Map.of("standardBreakdown", Map.of("total", new BigDecimal("1200.00"))));
        when(estimates.findById(quoteId)).thenReturn(Optional.of(estimate));
        when(estimates.findByIdForUpdate(quoteId)).thenReturn(Optional.of(estimate));
        when(estimates.save(any(PricingEstimateEntity.class))).thenAnswer(call -> call.getArgument(0));
        when(security.getCurrentUserId()).thenReturn(actorId);
    }

    @Test
    void acceptsQuoteWithoutChangingFrozenAmounts() {
        var result = service.acceptQuote(quoteId);

        assertThat(result.status()).isEqualTo("ACCEPTED");
        assertThat(result.totalAmount()).isEqualByComparingTo("1062.00");
        assertThat(result.acceptedAt()).isNotNull();
        assertThat(estimate.getAcceptedByUserId()).isEqualTo(actorId);
        assertThat(result.standardPrice()).isEqualByComparingTo("1200.00");
        assertThat(result.savings()).isEqualByComparingTo("138.00");
        assertThat(result.customerName()).isEqualTo("Asha Customer");
        assertThat(result.customerEmail()).isEqualTo("asha@example.test");
        assertThat(result.propertyName()).isEqualTo("Lakeview Residence");
        assertThat(result.createdAt()).isNotNull();
        assertThat(result.lineItems()).extracting("lineType").containsExactly("SERVICE", "DISCOUNT", "TAX");
        verify(estimates).findByIdForUpdate(quoteId);
        verify(estimates).save(estimate);
        verifyNoInteractions(coupons, redemptions);
    }

    @Test
    void rejectsQuoteAndRecordsReason() {
        var result = service.rejectQuote(quoteId, "Please revise the schedule");

        assertThat(result.status()).isEqualTo("REJECTED");
        assertThat(result.rejectedAt()).isNotNull();
        assertThat(result.rejectionReason()).isEqualTo("Please revise the schedule");
        assertThat(estimate.getRejectedByUserId()).isEqualTo(actorId);
        assertThat(result.totalAmount()).isEqualByComparingTo("1062.00");
        verify(estimates).findByIdForUpdate(quoteId);
    }

    @Test
    void expiresQuoteInsteadOfAcceptingAfterDeadline() {
        estimate.setExpiresAt(OffsetDateTime.now().minusSeconds(1));

        var result = service.acceptQuote(quoteId);

        assertThat(result.status()).isEqualTo("EXPIRED");
        assertThat(result.acceptedAt()).isNull();
        verify(estimates).save(estimate);
    }

    @Test
    void preventsDecisionOnPreviouslyDecidedQuote() {
        estimate.setStatus("REJECTED");

        assertThatThrownBy(() -> service.acceptQuote(quoteId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("not open");
        verify(estimates, never()).save(any(PricingEstimateEntity.class));
    }

    @Test
    void acceptsAndCreatesRequestWithinTheQuoteTransaction() {
        CreateServiceRequestRequest request = new CreateServiceRequestRequest();
        ServiceRequestResponse created = new ServiceRequestResponse();
        when(requests.createRequest(request)).thenReturn(created);

        assertThat(service.acceptAndCreateRequest(quoteId, request)).isSameAs(created);
        assertThat(estimate.getStatus()).isEqualTo("ACCEPTED");
        assertThat(request.getQuoteId()).isEqualTo(quoteId);
        assertThat(request.getPropertyId()).isEqualTo(estimate.getProperty().getPropertyId());
        assertThat(request.getServiceId()).isEqualTo(estimate.getService().getServiceId());
        verify(estimates).save(estimate);
        verify(requests).createRequest(request);
    }

    @Test
    void repeatedAcceptAndBookCallUsesTheSameQuoteToReturnExistingRequest() {
        estimate.setStatus("ACCEPTED");
        estimate.setAcceptedAt(OffsetDateTime.now().minusMinutes(1));
        CreateServiceRequestRequest request = new CreateServiceRequestRequest();
        ServiceRequestResponse existing = new ServiceRequestResponse();
        when(requests.createRequest(request)).thenReturn(existing);

        assertThat(service.acceptAndCreateRequest(quoteId, request)).isSameAs(existing);
        verify(requests).createRequest(request);
        verify(estimates, never()).save(any(PricingEstimateEntity.class));
        verifyNoInteractions(coupons, redemptions);
    }
}
