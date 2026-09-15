package com.propertypilot.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CustomerSearchHistoryResponse {

    private UUID customerSearchHistoryId;

    private UUID customerId;

    private String searchType;

    private String searchCriteria;

    private Integer resultCount;

    private OffsetDateTime searchedAt;

    public UUID getCustomerSearchHistoryId() {
        return customerSearchHistoryId;
    }

    public void setCustomerSearchHistoryId(
            UUID customerSearchHistoryId) {

        this.customerSearchHistoryId =
                customerSearchHistoryId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(
            UUID customerId) {

        this.customerId = customerId;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(
            String searchType) {

        this.searchType = searchType;
    }

    public String getSearchCriteria() {
        return searchCriteria;
    }

    public void setSearchCriteria(
            String searchCriteria) {

        this.searchCriteria = searchCriteria;
    }

    public Integer getResultCount() {
        return resultCount;
    }

    public void setResultCount(
            Integer resultCount) {

        this.resultCount = resultCount;
    }

    public OffsetDateTime getSearchedAt() {
        return searchedAt;
    }

    public void setSearchedAt(
            OffsetDateTime searchedAt) {

        this.searchedAt = searchedAt;
    }
}