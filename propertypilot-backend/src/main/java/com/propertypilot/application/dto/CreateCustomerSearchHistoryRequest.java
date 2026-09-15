package com.propertypilot.application.dto;

public class CreateCustomerSearchHistoryRequest {

    private String searchType;

    private String searchCriteria;

    private Integer resultCount;

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
}