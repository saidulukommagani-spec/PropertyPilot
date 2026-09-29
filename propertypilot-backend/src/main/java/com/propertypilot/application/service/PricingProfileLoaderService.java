package com.propertypilot.application.service;

import java.util.Map;

public interface PricingProfileLoaderService {

    Map<String, String> loadProfile(
            String profileCode);
}