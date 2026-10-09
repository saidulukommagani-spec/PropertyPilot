package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.AdminCustomerResponse;
import com.propertypilot.application.service.AdminCustomerService;
import com.propertypilot.domain.enums.CustomerStatus;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerRepository;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AdminCustomerServiceImpl implements AdminCustomerService {
    private final CustomerRepository customerRepository;
    private final SecurityService securityService;

    public AdminCustomerServiceImpl(CustomerRepository customerRepository, SecurityService securityService) {
        this.customerRepository = customerRepository;
        this.securityService = securityService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminCustomerResponse> listCustomers() {
        securityService.validateAdminAccess();
        return customerRepository.findAll().stream()
                .sorted((left, right) -> left.getUser().getFullName().compareToIgnoreCase(right.getUser().getFullName()))
                .map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminCustomerResponse getCustomer(UUID customerId) {
        securityService.validateAdminAccess();
        return toResponse(findCustomer(customerId));
    }

    @Override
    public AdminCustomerResponse updateStatus(UUID customerId, String status) {
        securityService.validateAdminAccess();
        CustomerStatus next;
        try { next = CustomerStatus.valueOf(status.toUpperCase()); }
        catch (RuntimeException exception) { throw new IllegalArgumentException("Unsupported customer status"); }
        if (next != CustomerStatus.ACTIVE && next != CustomerStatus.INACTIVE) {
            throw new IllegalArgumentException("Customer status can only be set to ACTIVE or INACTIVE");
        }
        CustomerEntity customer = findCustomer(customerId);
        customer.setStatus(next);
        customer.setUpdatedBy(securityService.getCurrentUserId());
        return toResponse(customerRepository.save(customer));
    }

    private CustomerEntity findCustomer(UUID customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    private AdminCustomerResponse toResponse(CustomerEntity customer) {
        return new AdminCustomerResponse(customer.getCustomerId(), customer.getUser().getUserId(), customer.getUser().getFullName(),
                customer.getUser().getEmail(), customer.getUser().getMobileNumber(), customer.getStatus().name(),
                customer.getCustomerType().name(), customer.getPreferredLocation(), customer.getCreatedAt());
    }
}
