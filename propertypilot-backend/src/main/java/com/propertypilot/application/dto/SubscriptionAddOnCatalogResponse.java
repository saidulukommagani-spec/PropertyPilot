package com.propertypilot.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record SubscriptionAddOnCatalogResponse(UUID subscriptionAddOnId, UUID serviceId,
                                                String serviceName, String name,
                                                BigDecimal price, String status) { }
