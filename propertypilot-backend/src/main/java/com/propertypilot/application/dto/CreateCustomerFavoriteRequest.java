package com.propertypilot.application.dto;

import java.util.UUID;

public class CreateCustomerFavoriteRequest {

    private String favoriteType;

    private UUID propertyId;

    private UUID marketplaceListingId;

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