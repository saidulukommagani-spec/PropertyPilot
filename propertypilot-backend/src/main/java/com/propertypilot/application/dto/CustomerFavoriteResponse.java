package com.propertypilot.application.dto;

import java.util.UUID;

public class CustomerFavoriteResponse {

    private UUID customerFavoriteId;

    private UUID customerId;

    private String favoriteType;

    private UUID propertyId;

    private UUID marketplaceListingId;

    public UUID getCustomerFavoriteId() {
        return customerFavoriteId;
    }

    public void setCustomerFavoriteId(
            UUID customerFavoriteId) {

        this.customerFavoriteId =
                customerFavoriteId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(
            UUID customerId) {

        this.customerId = customerId;
    }

    public String getFavoriteType() {
        return favoriteType;
    }

    public void setFavoriteType(
            String favoriteType) {

        this.favoriteType = favoriteType;
    }

    public UUID getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(
            UUID propertyId) {

        this.propertyId = propertyId;
    }

    public UUID getMarketplaceListingId() {
        return marketplaceListingId;
    }

    public void setMarketplaceListingId(
            UUID marketplaceListingId) {

        this.marketplaceListingId =
                marketplaceListingId;
    }
}