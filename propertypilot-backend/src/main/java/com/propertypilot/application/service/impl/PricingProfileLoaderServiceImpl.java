package com.propertypilot.application.service.impl;

import com.propertypilot.application.service.PricingProfileLoaderService;
import com.propertypilot.infrastructure.persistence.entity.PricingProfileParameterEntity;
import com.propertypilot.infrastructure.persistence.repository.PricingProfileParameterRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PricingProfileLoaderServiceImpl
        implements PricingProfileLoaderService {

    private final PricingProfileParameterRepository
            pricingProfileParameterRepository;

    public PricingProfileLoaderServiceImpl(
            PricingProfileParameterRepository pricingProfileParameterRepository) {

        this.pricingProfileParameterRepository =
                pricingProfileParameterRepository;
    }

    @Override
    public Map<String, String> loadProfile(
            String profileCode) {

        List<PricingProfileParameterEntity> parameters =
                pricingProfileParameterRepository
                        .findByPricingProfileProfileCode(
                                profileCode);

        if (parameters.isEmpty()) {
            throw new IllegalArgumentException(
                    "Pricing profile not found: "
                            + profileCode);
        }

        if (parameters.get(0).getPricingProfile() == null
                || !Boolean.TRUE.equals(parameters.get(0).getPricingProfile().getActiveFlag())) {
            throw new IllegalArgumentException("Pricing profile is not active: " + profileCode);
        }

        Map<String, String> result =
                new HashMap<>();

        for (PricingProfileParameterEntity parameter
                : parameters) {

            if (!Boolean.TRUE.equals(parameter.getActiveFlag())) {
                continue;
            }

            result.put(
                    parameter.getParameterCode(),
                    parameter.getParameterValue());
        }

        return result;
    }
}
