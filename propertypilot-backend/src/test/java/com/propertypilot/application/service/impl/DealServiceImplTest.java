package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.*;
import com.propertypilot.application.service.BillingService;
import com.propertypilot.application.service.PricingConfigurationService;
import com.propertypilot.domain.constants.PricingParameterCodes;
import com.propertypilot.domain.enums.*;
import com.propertypilot.infrastructure.persistence.entity.*;
import com.propertypilot.infrastructure.persistence.repository.*;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.BusinessException;
import com.propertypilot.web.exception.ResourceNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class DealServiceImplTest {

    @Mock
    private DealRepository dealRepository;

    @Mock
    private DealParticipantRepository participantRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private SecurityService securityService;

    @Mock
    private BillingService billingService;

    @Mock
    private PricingConfigurationService pricingConfigurationService;

    @InjectMocks
    private DealServiceImpl dealService;

    @Test
void getDeal_ShouldReturnDeal() {

    UUID dealId = UUID.randomUUID();

    Property property = new Property();
    property.setPropertyId(UUID.randomUUID());

    CustomerEntity seller = new CustomerEntity();
    seller.setCustomerId(UUID.randomUUID());

    DealEntity deal = new DealEntity();
    deal.setProperty(property);
    deal.setSeller(seller);
    deal.setDealType(DealType.PROPERTY_SALE);
    deal.setDealStatus(DealStatus.NEW);
    deal.setDealSource(DealSource.ADMIN_CREATED);

    when(dealRepository.findById(dealId))
            .thenReturn(Optional.of(deal));

    DealResponse response =
            dealService.getDeal(dealId);

    assertThat(response).isNotNull();
    assertThat(response.getDealStatus())
            .isEqualTo("NEW");
}

@Test
void getDeal_ShouldThrowException_WhenNotFound() {

    UUID dealId = UUID.randomUUID();

    when(dealRepository.findById(dealId))
            .thenReturn(Optional.empty());

    assertThatThrownBy(
            () -> dealService.getDeal(dealId))
            .isInstanceOf(
                    ResourceNotFoundException.class);
}
@Test
void getAllDeals_ShouldReturnDeals() {

    Property property = new Property();
    property.setPropertyId(UUID.randomUUID());

    CustomerEntity seller = new CustomerEntity();
    seller.setCustomerId(UUID.randomUUID());

    DealEntity deal = new DealEntity();
    deal.setProperty(property);
    deal.setSeller(seller);
    deal.setDealType(DealType.PROPERTY_SALE);
    deal.setDealStatus(DealStatus.NEW);
    deal.setDealSource(DealSource.ADMIN_CREATED);

    when(dealRepository.findAll())
            .thenReturn(List.of(deal));

    List<DealResponse> result =
            dealService.getAllDeals();

    assertThat(result).hasSize(1);

    verify(securityService)
            .validateAdminAccess();
}
@Test
void createDeal_PropertyNotFound() {

    UUID propertyId = UUID.randomUUID();

    CreateDealRequest request =
            new CreateDealRequest();

    request.setPropertyId(propertyId);

    when(propertyRepository.findById(propertyId))
            .thenReturn(Optional.empty());

    assertThatThrownBy(
            () -> dealService.createDeal(request))
            .isInstanceOf(
                    ResourceNotFoundException.class);
}
@Test
void cancelDeal_ShouldCancelDeal() {

    UUID dealId = UUID.randomUUID();

    Property property = new Property();
    property.setPropertyId(UUID.randomUUID());

    CustomerEntity seller = new CustomerEntity();
    seller.setCustomerId(UUID.randomUUID());

    DealEntity deal = new DealEntity();
    deal.setProperty(property);
    deal.setSeller(seller);
    deal.setDealType(DealType.PROPERTY_SALE);
    deal.setDealStatus(DealStatus.NEW);
    deal.setDealSource(DealSource.ADMIN_CREATED);

    when(dealRepository.findById(dealId))
            .thenReturn(Optional.of(deal));

    when(dealRepository.save(any()))
            .thenReturn(deal);

    DealResponse response =
            dealService.cancelDeal(
                    dealId,
                    "Customer cancelled");

    assertThat(response).isNotNull();

    verify(dealRepository)
            .save(any());
            assertThat(
        response.getDealStatus())
        .isEqualTo("CANCELLED");
}
@Test
void updateDealStatus_ShouldUpdateStatus() {

    UUID dealId = UUID.randomUUID();

    Property property = new Property();
    property.setPropertyId(UUID.randomUUID());

    CustomerEntity seller = new CustomerEntity();
    seller.setCustomerId(UUID.randomUUID());

    DealEntity deal = new DealEntity();
    deal.setProperty(property);
    deal.setSeller(seller);
    deal.setDealType(DealType.PROPERTY_SALE);
    deal.setDealStatus(DealStatus.NEW);
    deal.setDealSource(DealSource.ADMIN_CREATED);

    UpdateDealStatusRequest request =
            new UpdateDealStatusRequest();

    request.setDealStatus("COMPLETED");

    when(dealRepository.findById(dealId))
            .thenReturn(Optional.of(deal));

    when(dealRepository.save(any()))
            .thenReturn(deal);

    DealResponse response =
            dealService.updateDealStatus(
                    dealId,
                    request);

    assertThat(response).isNotNull();

    verify(dealRepository)
            .save(any());
            assertThat(
        response.getDealStatus())
        .isEqualTo("COMPLETED");
}
@Test
void createDeal_ShouldCreateDeal() {

    UUID propertyId = UUID.randomUUID();
    UUID sellerId = UUID.randomUUID();

    Property property = new Property();
    property.setPropertyId(propertyId);

    CustomerEntity seller =
            new CustomerEntity();

    seller.setCustomerId(sellerId);

    CreateDealRequest request =
            new CreateDealRequest();

    request.setPropertyId(propertyId);
    request.setSellerCustomerId(sellerId);
    request.setDealType("PROPERTY_SALE");
    request.setExpectedAmount(
            BigDecimal.valueOf(1000000));

    DealEntity saved =
            new DealEntity();

    saved.setProperty(property);
    saved.setSeller(seller);
    saved.setDealType(
            DealType.PROPERTY_SALE);
    saved.setDealStatus(
            DealStatus.NEW);

    when(propertyRepository.findById(propertyId))
            .thenReturn(Optional.of(property));

    when(customerRepository.findById(sellerId))
            .thenReturn(Optional.of(seller));

    when(dealRepository.save(any()))
            .thenReturn(saved);

    DealResponse response =
            dealService.createDeal(request);

    assertThat(response)
            .isNotNull();

    verify(dealRepository)
            .save(any());
}
@Test
void finalizeDeal_ShouldCalculateCommissions() {

    UUID dealId =
            UUID.randomUUID();

    Property property =
            new Property();

    property.setPropertyId(
            UUID.randomUUID());

    CustomerEntity seller =
            new CustomerEntity();

    seller.setCustomerId(
            UUID.randomUUID());

    DealEntity deal =
            new DealEntity();
deal.setDealType(
        DealType.PROPERTY_SALE);

deal.setDealStatus(
        DealStatus.BUYER_SELECTED);

deal.setDealSource(
        DealSource.ADMIN_CREATED);
    deal.setProperty(property);

    deal.setSeller(seller);

    deal.setBuyerCommissionPercent(
            BigDecimal.valueOf(2));

    deal.setSellerCommissionPercent(
            BigDecimal.valueOf(1));

    when(dealRepository.findById(dealId))
            .thenReturn(Optional.of(deal));

    when(dealRepository.save(any()))
            .thenReturn(deal);

    FinalizeDealRequest request =
            new FinalizeDealRequest();

    request.setFinalDealAmount(
            BigDecimal.valueOf(1000000));

    DealResponse response =
            dealService.finalizeDeal(
                    dealId,
                    request);

    assertThat(response)
            .isNotNull();

    verify(dealRepository)
            .save(any());
}
@Test
void completeDeal_ShouldThrow_WhenBuyerNotSelected() {

    UUID dealId =
            UUID.randomUUID();

    DealEntity deal =
            new DealEntity();

    deal.setSelectedBuyerId(
            null);

    when(dealRepository.findById(dealId))
            .thenReturn(Optional.of(deal));

    assertThatThrownBy(
            () -> dealService.completeDeal(
                    dealId,
                    BigDecimal.valueOf(1000000)))
            .isInstanceOf(
                    BusinessException.class);
}

@Test
void getParticipants_ShouldReturnList() {

    UUID dealId =
            UUID.randomUUID();

    CustomerEntity customer =
            new CustomerEntity();

    customer.setCustomerId(
            UUID.randomUUID());

    UserEntity user =
            new UserEntity();

    user.setFullName(
            "Buyer One");

    customer.setUser(user);

    DealEntity deal =
            new DealEntity();

    deal.setDealId(dealId);

    DealParticipantEntity participant =
            new DealParticipantEntity();

    participant.setDeal(deal);   // IMPORTANT

    participant.setCustomer(customer);

    participant.setParticipantRole(
            ParticipantRole.BUYER);

    participant.setParticipantStatus(
            ParticipantStatus.ACTIVE);

    participant.setContactVisibility(
            ContactVisibility.HIDDEN);

    when(participantRepository
            .findByDeal_DealId(dealId))
            .thenReturn(
                    List.of(participant));

    List<DealParticipantResponse> result =
            dealService.getParticipants(
                    dealId);

    assertThat(result)
            .hasSize(1);
}

@Test
void selectBuyer_ShouldSelectBuyer() {

    UUID dealId = UUID.randomUUID();
    UUID participantId = UUID.randomUUID();
    UUID buyerId = UUID.randomUUID();

    DealEntity deal = new DealEntity();

    deal.setDealId(dealId);
    deal.setDealType(DealType.PROPERTY_SALE);
    deal.setDealStatus(DealStatus.NEW);
    deal.setDealSource(DealSource.ADMIN_CREATED);

    UserEntity user = new UserEntity();
    user.setFullName("Buyer One");

    CustomerEntity customer = new CustomerEntity();
    customer.setCustomerId(buyerId);
    customer.setUser(user);

    DealParticipantEntity participant =
            new DealParticipantEntity();

    participant.setDealParticipantId(participantId);
    participant.setDeal(deal);
    participant.setCustomer(customer);
    participant.setParticipantRole(
            ParticipantRole.BUYER);
    participant.setParticipantStatus(
            ParticipantStatus.ACTIVE);
    participant.setContactVisibility(
            ContactVisibility.HIDDEN);

    when(dealRepository.findById(dealId))
            .thenReturn(Optional.of(deal));

    when(participantRepository.findByDeal_DealId(dealId))
            .thenReturn(List.of(participant));

    when(participantRepository.findById(participantId))
            .thenReturn(Optional.of(participant));

    when(participantRepository.save(any()))
            .thenReturn(participant);

    when(dealRepository.save(any()))
            .thenReturn(deal);

    DealParticipantResponse response =
            dealService.selectBuyer(
                    dealId,
                    participantId);

    assertThat(response).isNotNull();

    verify(dealRepository).save(any());
}

@Test
void completeDeal_ShouldCompleteDeal() {

    UUID dealId =
            UUID.randomUUID();

    Property property =
            new Property();

    property.setPropertyId(
            UUID.randomUUID());

    UserEntity sellerUser =
            new UserEntity();

    sellerUser.setFullName(
            "Seller One");

    CustomerEntity seller =
            new CustomerEntity();

    seller.setCustomerId(
            UUID.randomUUID());

    seller.setUser(
            sellerUser);

    DealEntity deal =
            new DealEntity();

    deal.setDealId(
            dealId);

    deal.setProperty(
            property);

    deal.setSeller(
            seller);

    deal.setSelectedBuyerId(
            UUID.randomUUID());

    deal.setDealType(
            DealType.PROPERTY_SALE);

    deal.setDealStatus(
            DealStatus.BUYER_SELECTED);

    deal.setDealSource(
            DealSource.ADMIN_CREATED);

    when(dealRepository.findById(dealId))
            .thenReturn(Optional.of(deal));

    when(pricingConfigurationService
            .getDecimalParameter(
                    "DEFAULT",
                    PricingParameterCodes
                            .BUYER_COMMISSION_PERCENT))
            .thenReturn(
                    BigDecimal.valueOf(2));

    when(pricingConfigurationService
            .getDecimalParameter(
                    "DEFAULT",
                    PricingParameterCodes
                            .SELLER_COMMISSION_PERCENT))
            .thenReturn(
                    BigDecimal.valueOf(1));

    when(dealRepository.save(any()))
            .thenReturn(deal);

    DealResponse response =
            dealService.completeDeal(
                    dealId,
                    BigDecimal.valueOf(1000000));

    assertThat(response)
            .isNotNull();

    verify(dealRepository)
            .save(any());
}

@Test
void addParticipant_ShouldCreateParticipant() {

    UUID dealId =
            UUID.randomUUID();

    UUID customerId =
            UUID.randomUUID();

    DealEntity deal =
            new DealEntity();

    deal.setDealType(
            DealType.PROPERTY_SALE);

    deal.setDealStatus(
            DealStatus.NEW);

    deal.setDealSource(
            DealSource.ADMIN_CREATED);

    CustomerEntity customer =
            new CustomerEntity();

    customer.setCustomerId(
            customerId);

    UserEntity user =
            new UserEntity();

    user.setFullName(
            "Buyer One");

    customer.setUser(user);

    AddDealParticipantRequest request =
            new AddDealParticipantRequest();

    request.setCustomerId(
            customerId);

    request.setParticipantRole(
            "BUYER");

    request.setContactVisibility(
            "HIDDEN");

    request.setOfferedAmount(
            BigDecimal.valueOf(1000000));

    when(dealRepository.findById(dealId))
            .thenReturn(Optional.of(deal));

    when(customerRepository.findById(customerId))
            .thenReturn(Optional.of(customer));

    when(participantRepository.save(any()))
            .thenAnswer(
                    invocation ->
                            invocation.getArgument(0));

    DealParticipantResponse response =
            dealService.addParticipant(
                    dealId,
                    request);

    assertThat(response)
            .isNotNull();

    verify(participantRepository)
            .save(any(
                    DealParticipantEntity.class));
}

}
