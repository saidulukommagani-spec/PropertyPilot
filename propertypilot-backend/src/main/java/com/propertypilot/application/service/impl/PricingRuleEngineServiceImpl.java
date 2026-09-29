package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreatePricingEstimateRequest;
import com.propertypilot.application.dto.PricingBreakdownResponse;
import com.propertypilot.application.service.PricingProfileLoaderService;
import com.propertypilot.application.service.PricingRuleEngineService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class PricingRuleEngineServiceImpl
        implements PricingRuleEngineService {

    private final PricingProfileLoaderService
            pricingProfileLoaderService;

    public PricingRuleEngineServiceImpl(
            PricingProfileLoaderService pricingProfileLoaderService) {

        this.pricingProfileLoaderService =
                pricingProfileLoaderService;
    }

    @Override
    public PricingBreakdownResponse calculateBreakdown(
            CreatePricingEstimateRequest request,
            String profileCode) {

        Map<String, String> profile =
                pricingProfileLoaderService
                        .loadProfile(profileCode);

        BigDecimal travelRate =
                new BigDecimal(
                        profile.get("TRAVEL_RATE"));

        BigDecimal agentDa =
                new BigDecimal(
                        profile.get("AGENT_DA"));

        BigDecimal overheadPercent =
                new BigDecimal(
                        profile.get("OVERHEAD_PERCENT"));

        BigDecimal marginPercent =
                new BigDecimal(
                        profile.get(
                                "PROPERTYPILOT_MARGIN_PERCENT"));

        BigDecimal gstPercent =
                new BigDecimal(
                        profile.get("GST_PERCENT"));

        String distanceMode =
                profile.get(
                        "TRAVEL_DISTANCE_MODE");

        double distanceKm =
                request.getOneWayDistanceKm();

        if ("ROUND_TRIP".equalsIgnoreCase(
                distanceMode)) {

            distanceKm =
                    distanceKm * 2;
        }

        BigDecimal travelCost =
                BigDecimal.valueOf(distanceKm)
                        .multiply(travelRate);

        BigDecimal vendorCost =
                request.getVendorCost();

        BigDecimal baseCost =
                vendorCost
                        .add(travelCost)
                        .add(agentDa);

        BigDecimal overhead =
                baseCost
                        .multiply(overheadPercent)
                        .divide(
                                BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP);

        BigDecimal margin =
                baseCost
                        .add(overhead)
                        .multiply(marginPercent)
                        .divide(
                                BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP);

        BigDecimal subtotal =
                baseCost
                        .add(overhead)
                        .add(margin);

        BigDecimal gst =
                subtotal
                        .multiply(gstPercent)
                        .divide(
                                BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP);

        BigDecimal total =
                subtotal.add(gst);

        PricingBreakdownResponse response =
                new PricingBreakdownResponse();

        response.setVendorCost(
                vendorCost);

        response.setTravelCost(
                travelCost);

        response.setAgentDa(
                agentDa);

        response.setOverheadAmount(
                overhead);

        response.setMarginAmount(
                margin);

        response.setClusterDiscount(
                BigDecimal.ZERO);

        response.setEtaDiscount(
                BigDecimal.ZERO);

        response.setSubtotal(
                subtotal);

        response.setGstAmount(
                gst);

        response.setTotalAmount(
                total);

        return response;
    }
}