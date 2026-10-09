package com.propertypilot.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.propertypilot.domain.enums.CustomerStatus;
import com.propertypilot.domain.enums.CustomerType;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.UserEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerRepository;
import com.propertypilot.security.SecurityService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AdminCustomerServiceImplTest {
    private final CustomerRepository customerRepository = mock(CustomerRepository.class);
    private final SecurityService securityService = mock(SecurityService.class);
    private final AdminCustomerServiceImpl service = new AdminCustomerServiceImpl(customerRepository, securityService);

    @Test
    void customerDetailsIncludeLinkedUserIdForAdminContactEdit() {
        UUID customerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setUserId(userId);
        user.setFullName("Alex Customer");
        user.setEmail("alex@example.com");
        user.setMobileNumber("5551234567");
        CustomerEntity customer = new CustomerEntity();
        customer.setCustomerId(customerId);
        customer.setUser(user);
        customer.setStatus(CustomerStatus.ACTIVE);
        customer.setCustomerType(CustomerType.INDIVIDUAL);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        var response = service.getCustomer(customerId);

        assertThat(response.customerId()).isEqualTo(customerId);
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.fullName()).isEqualTo("Alex Customer");
        verify(securityService).validateAdminAccess();
    }
}
