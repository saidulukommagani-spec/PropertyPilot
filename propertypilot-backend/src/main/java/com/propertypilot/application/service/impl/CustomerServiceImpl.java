package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CustomerCreateRequest;
import com.propertypilot.application.dto.CustomerResponse;
import com.propertypilot.application.service.CustomerService;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Override
    public CustomerResponse createCustomer(CustomerCreateRequest request) {

        throw new UnsupportedOperationException("Not implemented yet");
    }
}