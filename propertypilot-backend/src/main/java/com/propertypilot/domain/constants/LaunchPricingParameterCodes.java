package com.propertypilot.domain.constants;

import java.util.List;

public final class LaunchPricingParameterCodes {
    public static final String TRAVEL_RATE = "TRAVEL_RATE";
    public static final String AGENT_DA = "AGENT_DA";
    public static final String OVERHEAD_PERCENT = "OVERHEAD_PERCENT";
    public static final String MARGIN_PERCENT = "PROPERTYPILOT_MARGIN_PERCENT";
    public static final String GST_PERCENT = "GST_PERCENT";
    public static final String TRAVEL_DISTANCE_MODE = "TRAVEL_DISTANCE_MODE";
    public static final String ETA_DISCOUNT_PERCENT = "ETA_DISCOUNT_PERCENT";
    public static final String ETA_DISCOUNT_MIN_DAYS = "ETA_DISCOUNT_MIN_DAYS";
    public static final List<String> REQUIRED = List.of(TRAVEL_RATE, AGENT_DA, OVERHEAD_PERCENT,
            MARGIN_PERCENT, GST_PERCENT, TRAVEL_DISTANCE_MODE, ETA_DISCOUNT_PERCENT, ETA_DISCOUNT_MIN_DAYS);

    private LaunchPricingParameterCodes() { }
}
