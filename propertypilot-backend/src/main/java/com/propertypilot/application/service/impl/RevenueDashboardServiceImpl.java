package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.RevenueDashboardResponse;
import com.propertypilot.application.service.RevenueDashboardService;
import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.InvoiceStatus;
import com.propertypilot.infrastructure.persistence.entity.InvoiceEntity;
import com.propertypilot.infrastructure.persistence.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RevenueDashboardServiceImpl
        implements RevenueDashboardService {

    private final InvoiceRepository
            invoiceRepository;

    public RevenueDashboardServiceImpl(
            InvoiceRepository invoiceRepository) {

        this.invoiceRepository =
                invoiceRepository;
    }

    @Override
    public RevenueDashboardResponse
    getRevenueDashboard() {

        List<InvoiceEntity> invoices =
                invoiceRepository.findAll();

        RevenueDashboardResponse response =
                new RevenueDashboardResponse();

        response.setTotalInvoices(
                invoices.size());

        response.setPaidInvoices(
                invoices.stream()
                        .filter(i ->
                                i.getStatus()
                                        == InvoiceStatus.PAID)
                        .count());

        response.setPendingInvoices(
                invoices.stream()
                        .filter(i ->
                                i.getStatus()
                                        == InvoiceStatus.ISSUED)
                        .count());

        BigDecimal totalRevenue =
                BigDecimal.ZERO;

        BigDecimal collectedRevenue =
                BigDecimal.ZERO;

        BigDecimal outstandingRevenue =
                BigDecimal.ZERO;

        BigDecimal serviceRevenue =
                BigDecimal.ZERO;

        BigDecimal subscriptionRevenue =
                BigDecimal.ZERO;

        BigDecimal dealRevenue =
                BigDecimal.ZERO;

        for (InvoiceEntity invoice : invoices) {

            BigDecimal amount =
                    invoice.getInvoiceAmount();

            if (amount == null) {
                amount = BigDecimal.ZERO;
            }

            totalRevenue =
                    totalRevenue.add(amount);

            if (invoice.getStatus()
                    == InvoiceStatus.PAID) {

                collectedRevenue =
                        collectedRevenue.add(amount);
            }

            if (invoice.getStatus()
                    == InvoiceStatus.ISSUED) {

                outstandingRevenue =
                        outstandingRevenue.add(amount);
            }

            if (invoice.getEntityType()
                    == BillingEntityType.SERVICE_REQUEST) {

                serviceRevenue =
                        serviceRevenue.add(amount);
            }

            if (invoice.getEntityType()
                    == BillingEntityType.SUBSCRIPTION) {

                subscriptionRevenue =
                        subscriptionRevenue.add(amount);
            }

            if (invoice.getEntityType()
                    == BillingEntityType.DEAL) {

                dealRevenue =
                        dealRevenue.add(amount);
            }
        }

        response.setTotalRevenue(
                totalRevenue);

        response.setCollectedRevenue(
                collectedRevenue);

        response.setOutstandingRevenue(
                outstandingRevenue);

        response.setServiceRevenue(
                serviceRevenue);

        response.setSubscriptionRevenue(
                subscriptionRevenue);

        response.setDealRevenue(
                dealRevenue);

        return response;
    }
}