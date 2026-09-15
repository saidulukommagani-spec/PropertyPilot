package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateCustomerSearchHistoryRequest;
import com.propertypilot.application.dto.CustomerSearchHistoryResponse;
import com.propertypilot.application.service.CustomerSearchHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerSearchHistoryController {

    private final CustomerSearchHistoryService
            customerSearchHistoryService;

    public CustomerSearchHistoryController(
            CustomerSearchHistoryService customerSearchHistoryService) {

        this.customerSearchHistoryService =
                customerSearchHistoryService;
    }

    @PostMapping("/{customerId}/search-history")
    public ResponseEntity<CustomerSearchHistoryResponse>
    createSearchHistory(
            @PathVariable UUID customerId,
            @RequestBody
            CreateCustomerSearchHistoryRequest request) {

        return ResponseEntity.ok(
                customerSearchHistoryService
                        .createSearchHistory(
                                customerId,
                                request));
    }

    @GetMapping("/{customerId}/search-history")
    public ResponseEntity<List<CustomerSearchHistoryResponse>>
    getCustomerSearchHistory(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                customerSearchHistoryService
                        .getCustomerSearchHistory(
                                customerId));
    }

    @GetMapping("/search-history/{searchHistoryId}")
    public ResponseEntity<CustomerSearchHistoryResponse>
    getSearchHistory(
            @PathVariable UUID searchHistoryId) {

        return ResponseEntity.ok(
                customerSearchHistoryService
                        .getSearchHistory(
                                searchHistoryId));
    }
}