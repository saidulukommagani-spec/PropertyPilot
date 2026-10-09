package com.propertypilot.application.service;

import com.propertypilot.application.dto.AdminCustomerResponse;
import java.util.List;
import java.util.UUID;

public interface AdminCustomerService {
    List<AdminCustomerResponse> listCustomers();
    AdminCustomerResponse getCustomer(UUID customerId);
    AdminCustomerResponse updateStatus(UUID customerId, String status);
}
