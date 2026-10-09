package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreateSubscriptionRenewalRequest;
import com.propertypilot.application.dto.SubscriptionRenewalResponse;
import com.propertypilot.application.dto.InvoiceResponse;
import com.propertypilot.application.dto.SubscriptionRenewalInvoiceRequest;

import java.util.List;
import java.util.UUID;

public interface SubscriptionRenewalService {

    InvoiceResponse createRenewalInvoice(SubscriptionRenewalInvoiceRequest request);

    SubscriptionRenewalResponse renewSubscription(
            CreateSubscriptionRenewalRequest request);

    SubscriptionRenewalResponse getRenewal(
            UUID subscriptionRenewalId);

    List<SubscriptionRenewalResponse>
    getRenewalsBySubscription(
            UUID customerSubscriptionId);
}
