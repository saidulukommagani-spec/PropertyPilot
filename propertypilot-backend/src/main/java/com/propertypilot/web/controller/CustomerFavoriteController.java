package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateCustomerFavoriteRequest;
import com.propertypilot.application.dto.CustomerFavoriteResponse;
import com.propertypilot.application.service.CustomerFavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerFavoriteController {

    private final CustomerFavoriteService
            customerFavoriteService;

    public CustomerFavoriteController(
            CustomerFavoriteService customerFavoriteService) {

        this.customerFavoriteService =
                customerFavoriteService;
    }

    @PostMapping("/{customerId}/favorites")
    public ResponseEntity<CustomerFavoriteResponse>
    createFavorite(
            @PathVariable UUID customerId,
            @RequestBody
            CreateCustomerFavoriteRequest request) {

        return ResponseEntity.ok(
                customerFavoriteService
                        .createFavorite(
                                customerId,
                                request));
    }

    @GetMapping("/{customerId}/favorites")
    public ResponseEntity<
            List<CustomerFavoriteResponse>>
    getCustomerFavorites(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                customerFavoriteService
                        .getCustomerFavorites(
                                customerId));
    }

    @DeleteMapping("/favorites/{favoriteId}")
    public ResponseEntity<Void>
    deleteFavorite(
            @PathVariable UUID favoriteId) {

        customerFavoriteService
                .deleteFavorite(favoriteId);

        return ResponseEntity.noContent()
                .build();
    }
}