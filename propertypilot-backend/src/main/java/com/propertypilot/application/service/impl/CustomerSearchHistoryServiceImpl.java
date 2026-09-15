package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateCustomerSearchHistoryRequest;
import com.propertypilot.application.dto.CustomerSearchHistoryResponse;
import com.propertypilot.application.service.CustomerSearchHistoryService;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerSearchHistoryEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerJpaRepository;
import com.propertypilot.infrastructure.persistence.repository.CustomerSearchHistoryRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CustomerSearchHistoryServiceImpl
        implements CustomerSearchHistoryService {

    private final CustomerJpaRepository customerRepository;

    private final CustomerSearchHistoryRepository
            customerSearchHistoryRepository;

    public CustomerSearchHistoryServiceImpl(
            CustomerJpaRepository customerRepository,
            CustomerSearchHistoryRepository customerSearchHistoryRepository) {

        this.customerRepository =
                customerRepository;

        this.customerSearchHistoryRepository =
                customerSearchHistoryRepository;
    }

    @Override
    public CustomerSearchHistoryResponse createSearchHistory(
            UUID customerId,
            CreateCustomerSearchHistoryRequest request) {

        CustomerEntity customer =
                customerRepository.findById(customerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found"));

        CustomerSearchHistoryEntity entity =
                new CustomerSearchHistoryEntity();

        entity.setCustomer(customer);

        entity.setSearchType(
                request.getSearchType());

        entity.setSearchCriteria(
                request.getSearchCriteria());

        entity.setResultCount(
                request.getResultCount());

        entity.setSearchedAt(
                OffsetDateTime.now());

        entity =
                customerSearchHistoryRepository.save(entity);

        return map(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerSearchHistoryResponse>
    getCustomerSearchHistory(
            UUID customerId) {

        return customerSearchHistoryRepository
                .findByCustomerCustomerId(customerId)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerSearchHistoryResponse getSearchHistory(
            UUID searchHistoryId) {

        CustomerSearchHistoryEntity entity =
                customerSearchHistoryRepository
                        .findById(searchHistoryId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Search history not found"));

        return map(entity);
    }

    private CustomerSearchHistoryResponse map(
            CustomerSearchHistoryEntity entity) {

        CustomerSearchHistoryResponse response =
                new CustomerSearchHistoryResponse();

        response.setCustomerSearchHistoryId(
                entity.getCustomerSearchHistoryId());

        response.setCustomerId(
                entity.getCustomer().getCustomerId());

        response.setSearchType(
                entity.getSearchType());

        response.setSearchCriteria(
                entity.getSearchCriteria());

        response.setResultCount(
                entity.getResultCount());

        response.setSearchedAt(
                entity.getSearchedAt());

        return response;
    }
}