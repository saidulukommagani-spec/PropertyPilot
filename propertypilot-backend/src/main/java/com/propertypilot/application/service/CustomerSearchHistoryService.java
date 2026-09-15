package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreateCustomerSearchHistoryRequest;
import com.propertypilot.application.dto.CustomerSearchHistoryResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerSearchHistoryService {

    CustomerSearchHistoryResponse createSearchHistory(
            UUID customerId,
            CreateCustomerSearchHistoryRequest request);

    List<CustomerSearchHistoryResponse> getCustomerSearchHistory(
            UUID customerId);

    CustomerSearchHistoryResponse getSearchHistory(
            UUID searchHistoryId);
}