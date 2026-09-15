package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreateCustomerFavoriteRequest;
import com.propertypilot.application.dto.CustomerFavoriteResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerFavoriteService {

    CustomerFavoriteResponse createFavorite(
            UUID customerId,
            CreateCustomerFavoriteRequest request);

    List<CustomerFavoriteResponse>
    getCustomerFavorites(
            UUID customerId);

    void deleteFavorite(
            UUID favoriteId);
}