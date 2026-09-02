package com.propertypilot.application.service;

import com.propertypilot.application.dto.CustomerCreateRequest;
import com.propertypilot.application.dto.CustomerResponse;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerCreateRequest request);
}