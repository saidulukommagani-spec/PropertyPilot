package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.InvoiceResponse;
import com.propertypilot.application.service.BillingService;
import com.propertypilot.application.service.DealBillingService;
import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.DealStatus;
import com.propertypilot.domain.enums.InvoiceType;
import com.propertypilot.infrastructure.persistence.entity.DealEntity;
import com.propertypilot.infrastructure.persistence.repository.DealRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DealBillingServiceImpl
        implements DealBillingService {

    private final DealRepository dealRepository;

    private final BillingService billingService;

    @Override
    public List<UUID> generateDealInvoices(
            UUID dealId) {

        DealEntity deal =
                dealRepository
                        .findById(dealId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Deal not found"));

        validateDeal(deal);

        List<UUID> invoiceIds =
                new ArrayList<>();

        createBuyerInvoice(
                deal,
                invoiceIds);

        createSellerInvoice(
                deal,
                invoiceIds);

        return invoiceIds;
    }

    private void createBuyerInvoice(
            DealEntity deal,
            List<UUID> invoiceIds) {

        BigDecimal amount =
                deal.getBuyerCommissionAmount();

        if (amount == null
                || amount.compareTo(
                        BigDecimal.ZERO) <= 0) {
            return;
        }

        InvoiceResponse invoice =
                billingService.createInvoice(
                        BillingEntityType.DEAL,
                        deal.getDealId(),
                        deal.getSelectedBuyerId(),
                        InvoiceType.BUYER_COMMISSION,
                        amount,
                        "Buyer commission for deal "
                                + deal.getDealId());

        invoiceIds.add(
                invoice.getInvoiceId());
    }

    private void createSellerInvoice(
            DealEntity deal,
            List<UUID> invoiceIds) {

        BigDecimal amount =
                deal.getSellerCommissionAmount();

        if (amount == null
                || amount.compareTo(
                        BigDecimal.ZERO) <= 0) {
            return;
        }

        InvoiceResponse invoice =
                billingService.createInvoice(
                        BillingEntityType.DEAL,
                        deal.getDealId(),
                        deal.getSeller()
                                .getCustomerId(),
                        InvoiceType.SELLER_COMMISSION,
                        amount,
                        "Seller commission for deal "
                                + deal.getDealId());

        invoiceIds.add(
                invoice.getInvoiceId());
    }

    private void validateDeal(
            DealEntity deal) {

    if (deal.getDealStatus()
        != DealStatus.COMPLETED) {

    throw new IllegalStateException(
            "Invoices can only be generated for COMPLETED deals");
}
    }
}