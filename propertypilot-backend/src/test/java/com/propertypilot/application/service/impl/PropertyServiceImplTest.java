package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.PropertyCreateRequest;
import com.propertypilot.application.dto.PropertyResponse;
import com.propertypilot.application.dto.PropertyUpdateRequest;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.Property;
import com.propertypilot.infrastructure.persistence.repository.CustomerRepository;
import com.propertypilot.infrastructure.persistence.repository.PropertyRepository;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceImplTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private PropertyServiceImpl service;

    @Test
    void createProperty_ShouldCreatePropertyForLoggedInCustomer() {

        CustomerEntity customer = customer();

        PropertyCreateRequest request =
                new PropertyCreateRequest();

        request.setTitle("My Plot");
        request.setPropertyType("PLOT");
        request.setListingStatus("AVAILABLE");
        request.setPrice(BigDecimal.valueOf(100000));
        request.setStatus("ACTIVE");

        when(securityService.getCurrentCustomer())
                .thenReturn(customer);

        when(propertyRepository.save(any(Property.class)))
                .thenAnswer(invocation -> {
                    Property property =
                            invocation.getArgument(0);

                    property.setPropertyId(
                            UUID.randomUUID());

                    return property;
                });

        PropertyResponse response =
                service.createProperty(request);

        assertThat(response).isNotNull();
        assertThat(response.getTitle())
                .isEqualTo("My Plot");

        verify(propertyRepository)
                .save(any(Property.class));
    }

    @Test
    void getPropertyById_ShouldReturnPropertyForOwner() {

        Property property = property();

        when(propertyRepository.findById(
                property.getPropertyId()))
                .thenReturn(Optional.of(property));

                when(securityService.isAdmin())
        .thenReturn(false);

when(securityService.getCurrentCustomer())
        .thenReturn(property.getCustomer());
        PropertyResponse response =
                service.getPropertyById(
                        property.getPropertyId());

        assertThat(response).isNotNull();
        assertThat(response.getPropertyId())
                .isEqualTo(property.getPropertyId());
    }

    @Test
    void getPropertyById_ShouldThrowWhenPropertyNotFound() {

        UUID propertyId = UUID.randomUUID();

        when(propertyRepository.findById(propertyId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.getPropertyById(propertyId))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }

   @Test
void getPropertyById_ShouldHideArchivedPropertyFromCustomer() {

    Property property = property();

    property.setStatus("ARCHIVED");

    when(securityService.isAdmin())
            .thenReturn(false);

    when(securityService.getCurrentCustomer())
            .thenReturn(property.getCustomer());

    when(propertyRepository.findById(
            property.getPropertyId()))
            .thenReturn(Optional.of(property));

    assertThatThrownBy(() ->
            service.getPropertyById(
                    property.getPropertyId()))
            .isInstanceOf(
                    ResourceNotFoundException.class);
}
    @Test
    void getPropertyById_ShouldAllowAdminToViewArchivedProperty() {

        Property property = property();

        property.setStatus("ARCHIVED");

        when(securityService.isAdmin())
                .thenReturn(true);

        when(propertyRepository.findById(
                property.getPropertyId()))
                .thenReturn(Optional.of(property));

        PropertyResponse response =
                service.getPropertyById(
                        property.getPropertyId());

        assertThat(response).isNotNull();
    }

    @Test
    void getAllProperties_ShouldReturnAllForAdmin() {

        when(securityService.isAdmin())
                .thenReturn(true);

        when(propertyRepository.findAll())
                .thenReturn(List.of(
                        property(),
                        property()));

        List<PropertyResponse> result =
                service.getAllProperties();

        assertThat(result)
                .hasSize(2);
    }

@Test
void getAllProperties_ShouldReturnOnlyCustomerProperties() {

    CustomerEntity customer =
            new CustomerEntity();

    UUID customerId =
            UUID.randomUUID();

    customer.setCustomerId(
            customerId);

    CustomerEntity anotherCustomer =
            new CustomerEntity();

    anotherCustomer.setCustomerId(
            UUID.randomUUID());

    Property property1 =
            property(customer);

    Property property2 =
            property(anotherCustomer);

    when(securityService.isAdmin())
            .thenReturn(false);

    when(securityService.getCurrentCustomer())
            .thenReturn(customer);

    when(propertyRepository.findAll())
            .thenReturn(
                    List.of(
                            property1,
                            property2));

                            System.out.println("Logged In Customer = "
        + customer.getCustomerId());

System.out.println("Property1 Customer = "
        + property1.getCustomer().getCustomerId());

System.out.println("Property2 Customer = "
        + property2.getCustomer().getCustomerId());
System.out.println("Logged In Customer = "
        + customer.getCustomerId());

System.out.println("Property1 Customer = "
        + property1.getCustomer().getCustomerId());

System.out.println("Property2 Customer = "
        + property2.getCustomer().getCustomerId());
    List<PropertyResponse> result =
            service.getAllProperties();

    assertThat(result)
            .hasSize(1);

    assertThat(result.get(0)
            .getPropertyId())
            .isEqualTo(
                    property1.getPropertyId());
}
    @Test
    void archiveProperty_ShouldArchiveProperty() {

        

        Property property = property();

        when(securityService.isAdmin())
        .thenReturn(false);

when(securityService.getCurrentCustomer())
        .thenReturn(property.getCustomer());

        when(propertyRepository.findById(
                property.getPropertyId()))
                .thenReturn(Optional.of(property));

        when(propertyRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        PropertyResponse response =
                service.archiveProperty(
                        property.getPropertyId());

        assertThat(response).isNotNull();

        assertThat(property.getStatus())
                .isEqualTo("ARCHIVED");

        assertThat(property.getArchivedAt())
                .isNotNull();
    }

    @Test
    void archiveProperty_ShouldThrowWhenAlreadyArchived() {

        Property property = property();
when(securityService.isAdmin())
        .thenReturn(false);

when(securityService.getCurrentCustomer())
        .thenReturn(property.getCustomer());
        property.setStatus("ARCHIVED");

        when(propertyRepository.findById(
                property.getPropertyId()))
                .thenReturn(Optional.of(property));

        assertThatThrownBy(() ->
                service.archiveProperty(
                        property.getPropertyId()))
                .isInstanceOf(
                        IllegalStateException.class);
    }

    @Test
    void restoreProperty_ShouldRestoreArchivedProperty() {

        Property property = property();

        property.setStatus("ARCHIVED");
        property.setArchivedAt(
                Instant.now());

        when(securityService.isAdmin())
                .thenReturn(true);

        when(propertyRepository.findById(
                property.getPropertyId()))
                .thenReturn(Optional.of(property));

        when(propertyRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        PropertyResponse response =
                service.restoreProperty(
                        property.getPropertyId());

        assertThat(response).isNotNull();

        assertThat(property.getStatus())
                .isEqualTo("AVAILABLE");

        assertThat(property.getArchivedAt())
                .isNull();

        assertThat(property.getArchivedBy())
                .isNull();
    }

    @Test
    void restoreProperty_ShouldRejectCustomer() {

        when(securityService.isAdmin())
                .thenReturn(false);

        assertThatThrownBy(() ->
                service.restoreProperty(
                        UUID.randomUUID()))
                .isInstanceOf(
                        AccessDeniedException.class);
    }

    @Test
    void restoreProperty_ShouldThrowWhenNotArchived() {

        Property property = property();

        property.setStatus("AVAILABLE");

        when(securityService.isAdmin())
                .thenReturn(true);

        when(propertyRepository.findById(
                property.getPropertyId()))
                .thenReturn(Optional.of(property));

        assertThatThrownBy(() ->
                service.restoreProperty(
                        property.getPropertyId()))
                .isInstanceOf(
                        IllegalStateException.class);
    }

    @Test
    void updateProperty_ShouldUpdateProperty() {

        Property property = property();
when(securityService.isAdmin())
        .thenReturn(false);

when(securityService.getCurrentCustomer())
        .thenReturn(property.getCustomer());
        PropertyUpdateRequest request =
                new PropertyUpdateRequest();

        request.setTitle("Updated");
        request.setPropertyType("HOUSE");
        request.setListingStatus("SOLD");
        request.setPrice(BigDecimal.valueOf(500000));
        request.setStatus("ACTIVE");

        when(propertyRepository.findById(
                property.getPropertyId()))
                .thenReturn(Optional.of(property));

        when(propertyRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        PropertyResponse response =
                service.updateProperty(
                        property.getPropertyId(),
                        request);

        assertThat(response.getTitle())
                .isEqualTo("Updated");
    }

    @Test
    void updateProperty_ShouldRejectArchivedProperty() {

        Property property = property();
when(securityService.isAdmin())
        .thenReturn(false);

when(securityService.getCurrentCustomer())
        .thenReturn(property.getCustomer());
        property.setStatus("ARCHIVED");

        when(propertyRepository.findById(
                property.getPropertyId()))
                .thenReturn(Optional.of(property));

        PropertyUpdateRequest request =
                new PropertyUpdateRequest();

        assertThatThrownBy(() ->
                service.updateProperty(
                        property.getPropertyId(),
                        request))
                .isInstanceOf(
                        IllegalStateException.class);
    }

    private CustomerEntity customer() {

        CustomerEntity customer =
                new CustomerEntity();

        customer.setCustomerId(
                UUID.randomUUID());

        return customer;
    }

    private CustomerEntity anotherCustomer() {

        CustomerEntity customer =
                new CustomerEntity();

        customer.setCustomerId(
                UUID.randomUUID());

        return customer;
    }

    private Property property() {

        return property(customer());
    }

    private Property property(
        CustomerEntity customer) {

    Property property =
            new Property();

    property.setPropertyId(
            UUID.randomUUID());

    property.setCustomer(customer);

    property.setTitle(
            "Test Property");

    property.setPropertyType(
            "PLOT");

    property.setStatus(
            "ACTIVE");

    return property;
}
}