package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreatePricingEstimateRequest;
import com.propertypilot.application.dto.PricingBreakdownResponse;
import com.propertypilot.application.service.PricingProfileLoaderService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PricingRuleEngineServiceImplTest {
    private final PricingProfileLoaderService loader = mock(PricingProfileLoaderService.class);
    private final PricingRuleEngineServiceImpl engine = new PricingRuleEngineServiceImpl(loader);

    @Test
    void calculatesConfigurableRoundTripServicePrice() {
        when(loader.loadProfile("STANDARD")).thenReturn(Map.of(
                "TRAVEL_RATE", "2.00", "AGENT_DA", "100", "OVERHEAD_PERCENT", "10",
                "PROPERTYPILOT_MARGIN_PERCENT", "20", "GST_PERCENT", "18",
                "TRAVEL_DISTANCE_MODE", "ROUND_TRIP"));
        CreatePricingEstimateRequest request = new CreatePricingEstimateRequest();
        request.setOneWayDistanceKm(10.0);
        request.setVendorCost(new BigDecimal("1000.00"));

        PricingBreakdownResponse result = engine.calculateBreakdown(request, "STANDARD");

        assertThat(result.getTravelCost()).isEqualByComparingTo("40.00");
        assertThat(result.getAgentDa()).isEqualByComparingTo("100.00");
        assertThat(result.getOverheadAmount()).isEqualByComparingTo("114.00");
        assertThat(result.getMarginAmount()).isEqualByComparingTo("250.80");
        assertThat(result.getSubtotal()).isEqualByComparingTo("1504.80");
        assertThat(result.getGstAmount()).isEqualByComparingTo("270.86");
        assertThat(result.getTotalAmount()).isEqualByComparingTo("1775.66");
    }

    @Test
    void calculatesOneWayTravelUsingConfiguredRateAndCurrencyRounding() {
        when(loader.loadProfile("CUSTOMER")).thenReturn(Map.of(
                "TRAVEL_RATE", "2.00", "AGENT_DA", "100", "OVERHEAD_PERCENT", "10",
                "PROPERTYPILOT_MARGIN_PERCENT", "20", "GST_PERCENT", "18",
                "TRAVEL_DISTANCE_MODE", "ONE_WAY"));
        CreatePricingEstimateRequest request = new CreatePricingEstimateRequest();
        request.setOneWayDistanceKm(10.0);
        request.setVendorCost(new BigDecimal("1000.00"));

        PricingBreakdownResponse result = engine.calculateBreakdown(request, "CUSTOMER");

        assertThat(result.getTravelCost()).isEqualByComparingTo("20.00");
        assertThat(result.getSubtotal()).isEqualByComparingTo("1478.40");
        assertThat(result.getGstAmount()).isEqualByComparingTo("266.11");
        assertThat(result.getTotalAmount()).isEqualByComparingTo("1744.51");
    }

    @Test
    void nonVisitEstimateHasZeroTravelCost() {
        when(loader.loadProfile("CUSTOMER")).thenReturn(Map.of(
                "TRAVEL_RATE", "4.00", "AGENT_DA", "0", "OVERHEAD_PERCENT", "0",
                "PROPERTYPILOT_MARGIN_PERCENT", "0", "GST_PERCENT", "0",
                "TRAVEL_DISTANCE_MODE", "ROUND_TRIP"));
        CreatePricingEstimateRequest request = new CreatePricingEstimateRequest();
        request.setOneWayDistanceKm(0.0);
        request.setVendorCost(new BigDecimal("500.00"));

        PricingBreakdownResponse result = engine.calculateBreakdown(request, "CUSTOMER");

        assertThat(result.getTravelCost()).isEqualByComparingTo("0.00");
        assertThat(result.getTotalAmount()).isEqualByComparingTo("500.00");
    }
}
