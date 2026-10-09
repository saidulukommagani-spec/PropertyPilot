package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.RevenueDashboardResponse;
import com.propertypilot.application.dto.InvoiceRevenueByCurrencyResponse;
import com.propertypilot.application.service.RevenueDashboardService;
import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.InvoiceStatus;
import com.propertypilot.infrastructure.persistence.entity.InvoiceEntity;
import com.propertypilot.infrastructure.persistence.repository.InvoiceRepository;
import com.propertypilot.infrastructure.persistence.repository.PaymentRepository;
import com.propertypilot.domain.enums.PaymentStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RevenueDashboardServiceImpl
        implements RevenueDashboardService {

    private final InvoiceRepository
            invoiceRepository;

    private final PaymentRepository paymentRepository;

    public RevenueDashboardServiceImpl(
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository) {

        this.invoiceRepository =
                invoiceRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public RevenueDashboardResponse
    getRevenueDashboard() {

        RevenueDashboardResponse response =
                new RevenueDashboardResponse();

        List<InvoiceEntity> invoices = invoiceRepository.findAll();
        long currencyCount = invoices.stream()
                .map(invoice -> invoice.getCurrencyCode() == null || invoice.getCurrencyCode().isBlank()
                        ? "INR" : invoice.getCurrencyCode().trim().toUpperCase(java.util.Locale.ROOT))
                .distinct()
                .count();
        boolean scalarAmountsAreComparable = currencyCount <= 1;

        response.setTotalInvoices(invoiceRepository.count());

        response.setPaidInvoices(invoiceRepository.countByStatus(InvoiceStatus.PAID));

        response.setPendingInvoices(invoiceRepository.countByStatus(InvoiceStatus.SENT));
        Map<UUID, BigDecimal> settledByInvoice = paymentRepository.sumSettledByInvoice(PaymentStatus.SUCCESS)
                .stream().collect(Collectors.toMap(
                        row -> (UUID) row[0],
                        row -> zeroIfNull((BigDecimal) row[1]),
                        BigDecimal::add));
        BigDecimal collected = settledByInvoice.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal outstanding = invoiceRepository.findByStatusIn(List.of(
                        InvoiceStatus.SENT, InvoiceStatus.PARTIALLY_PAID, InvoiceStatus.OVERDUE))
                .stream()
                .map(invoice -> zeroIfNull(invoice.getInvoiceAmount())
                        .add(zeroIfNull(invoice.getTaxAmount()))
                        .subtract(settledByInvoice.getOrDefault(invoice.getInvoiceId(), BigDecimal.ZERO))
                        .max(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (scalarAmountsAreComparable) {
            response.setTotalRevenue(zeroIfNull(invoiceRepository.sumInvoiceAmounts()));
            response.setCollectedRevenue(collected);
            response.setOutstandingRevenue(outstanding);
            response.setServiceRevenue(zeroIfNull(invoiceRepository.sumInvoiceAmountsByEntityType(BillingEntityType.SERVICE_REQUEST)));
            response.setSubscriptionRevenue(zeroIfNull(invoiceRepository.sumInvoiceAmountsByEntityType(BillingEntityType.SUBSCRIPTION)));
            response.setDealRevenue(zeroIfNull(invoiceRepository.sumInvoiceAmountsByEntityType(BillingEntityType.DEAL)));
        } else {
            // A monetary aggregate has no valid scalar representation when currencies differ.
            response.setTotalRevenue(null);
            response.setCollectedRevenue(null);
            response.setOutstandingRevenue(null);
            response.setServiceRevenue(null);
            response.setSubscriptionRevenue(null);
            response.setDealRevenue(null);
        }

        Map<String, InvoiceRevenueByCurrencyResponse> currencyTotals = new TreeMap<>();
        for (var invoice : invoices) {
            String currency = invoice.getCurrencyCode() == null || invoice.getCurrencyCode().isBlank()
                    ? "INR" : invoice.getCurrencyCode().trim().toUpperCase();
            var totals = currencyTotals.computeIfAbsent(currency, InvoiceRevenueByCurrencyResponse::new);
            BigDecimal gross = zeroIfNull(invoice.getInvoiceAmount()).add(zeroIfNull(invoice.getTaxAmount()));
            totals.setTotalInvoiceValue(totals.getTotalInvoiceValue().add(gross));
            if (invoice.getEntityType() == BillingEntityType.DEAL) {
                totals.setDealRevenue(totals.getDealRevenue().add(zeroIfNull(invoice.getInvoiceAmount())));
            }
            BigDecimal settled = settledByInvoice.getOrDefault(invoice.getInvoiceId(), BigDecimal.ZERO);
            totals.setCollectedAmount(totals.getCollectedAmount().add(settled));
            if (List.of(InvoiceStatus.SENT, InvoiceStatus.PARTIALLY_PAID, InvoiceStatus.OVERDUE).contains(invoice.getStatus())) {
                totals.setOutstandingAmount(totals.getOutstandingAmount().add(gross.subtract(settled).max(BigDecimal.ZERO)));
            }
        }
        response.setRevenueByCurrency(List.copyOf(currencyTotals.values()));

        return response;
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
