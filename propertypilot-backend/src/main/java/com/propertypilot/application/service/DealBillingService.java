package com.propertypilot.application.service;

import java.util.List;
import java.util.UUID;

public interface DealBillingService {

    List<UUID> generateDealInvoices(
            UUID dealId);
}