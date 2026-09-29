package com.propertypilot.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EligibilityRuleDefinition {

    @JsonProperty("field")
    private String field;

    @JsonProperty("operator")
    private String operator;

    @JsonProperty("value")
    private Object value;

    public String getField() {
        return field;
    }

    public void setField(
            String field) {
        this.field = field;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(
            String operator) {
        this.operator = operator;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(
            Object value) {
        this.value = value;
    }
}