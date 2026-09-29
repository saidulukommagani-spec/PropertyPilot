package com.propertypilot.application.dto;

import java.util.Map;

public class PlanBenefits {

    private Map<String, Object> benefits;

    public Map<String, Object> getBenefits() {
        return benefits;
    }

    public void setBenefits(
            Map<String, Object> benefits) {
        this.benefits = benefits;
    }
}