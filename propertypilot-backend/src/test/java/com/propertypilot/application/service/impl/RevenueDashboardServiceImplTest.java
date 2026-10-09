package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.RevenueDashboardResponse;
import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.InvoiceStatus;
import com.propertypilot.infrastructure.persistence.entity.InvoiceEntity;
import com.propertypilot.infrastructure.persistence.repository.InvoiceRepository;
import com.propertypilot.infrastructure.persistence.repository.PaymentRepository;
import com.propertypilot.domain.enums.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RevenueDashboardServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private RevenueDashboardServiceImpl service;

    private InvoiceEntity invoice(
        InvoiceStatus status,
        BillingEntityType type,
        BigDecimal amount) {

    InvoiceEntity invoice =
            new InvoiceEntity();

    invoice.setStatus(status);
    invoice.setInvoiceId(java.util.UUID.randomUUID());
    invoice.setEntityType(type);
    invoice.setInvoiceAmount(amount);
    invoice.setTaxAmount(BigDecimal.ZERO);

    return invoice;
}

@BeforeEach
void defaults() {
    when(invoiceRepository.findAll()).thenReturn(List.of());
    when(paymentRepository.sumSettledByInvoice(PaymentStatus.SUCCESS)).thenReturn(List.of());
    when(invoiceRepository.findByStatusIn(List.of(InvoiceStatus.SENT, InvoiceStatus.PARTIALLY_PAID, InvoiceStatus.OVERDUE)))
            .thenReturn(List.of());
}

@Test
void getRevenueDashboard_emptyInvoices() {
        when(invoiceRepository.count()).thenReturn(0L);

    RevenueDashboardResponse response =
            service.getRevenueDashboard();

    assertThat(response.getTotalInvoices())
            .isEqualTo(0);

    assertThat(response.getPaidInvoices())
            .isEqualTo(0);

    assertThat(response.getPendingInvoices())
            .isEqualTo(0);

    assertThat(response.getTotalRevenue())
            .isEqualByComparingTo(BigDecimal.ZERO);
}
@Test
void getRevenueDashboard_success() {

    List<InvoiceEntity> invoices =
            List.of(

                    invoice(
                            InvoiceStatus.PAID,
                            BillingEntityType.SERVICE_REQUEST,
                            BigDecimal.valueOf(1000)),

                    invoice(
                            InvoiceStatus.SENT,
                            BillingEntityType.SUBSCRIPTION,
                            BigDecimal.valueOf(2000)),

                    invoice(
                            InvoiceStatus.PAID,
                            BillingEntityType.DEAL,
                            BigDecimal.valueOf(3000))
            );

    when(invoiceRepository.count()).thenReturn(3L);
    when(invoiceRepository.findAll()).thenReturn(invoices);
    when(invoiceRepository.countByStatus(InvoiceStatus.PAID)).thenReturn(2L);
    when(invoiceRepository.countByStatus(InvoiceStatus.SENT)).thenReturn(1L);
    when(invoiceRepository.sumInvoiceAmounts()).thenReturn(new BigDecimal("6000"));
    when(paymentRepository.sumSettledByInvoice(PaymentStatus.SUCCESS)).thenReturn(List.<Object[]>of(
            new Object[] { invoices.get(0).getInvoiceId(), new BigDecimal("1000") },
            new Object[] { invoices.get(2).getInvoiceId(), new BigDecimal("3000") }));
    when(invoiceRepository.findByStatusIn(List.of(InvoiceStatus.SENT, InvoiceStatus.PARTIALLY_PAID, InvoiceStatus.OVERDUE)))
            .thenReturn(List.of(invoices.get(1)));
    when(invoiceRepository.sumInvoiceAmountsByEntityType(BillingEntityType.SERVICE_REQUEST)).thenReturn(new BigDecimal("1000"));
    when(invoiceRepository.sumInvoiceAmountsByEntityType(BillingEntityType.SUBSCRIPTION)).thenReturn(new BigDecimal("2000"));
    when(invoiceRepository.sumInvoiceAmountsByEntityType(BillingEntityType.DEAL)).thenReturn(new BigDecimal("3000"));

    RevenueDashboardResponse response =
            service.getRevenueDashboard();

    assertThat(response.getTotalInvoices())
            .isEqualTo(3);

    assertThat(response.getPaidInvoices())
            .isEqualTo(2);

    assertThat(response.getPendingInvoices())
            .isEqualTo(1);

    assertThat(response.getTotalRevenue())
            .isEqualByComparingTo("6000");

    assertThat(response.getCollectedRevenue())
            .isEqualByComparingTo("4000");

    assertThat(response.getOutstandingRevenue())
            .isEqualByComparingTo("2000");

    assertThat(response.getServiceRevenue())
            .isEqualByComparingTo("1000");

    assertThat(response.getSubscriptionRevenue())
            .isEqualByComparingTo("2000");

    assertThat(response.getDealRevenue())
            .isEqualByComparingTo("3000");
    assertThat(response.getRevenueByCurrency()).singleElement().satisfies(row -> {
        assertThat(row.getCurrencyCode()).isEqualTo("INR");
        assertThat(row.getTotalInvoiceValue()).isEqualByComparingTo("6000");
        assertThat(row.getCollectedAmount()).isEqualByComparingTo("4000");
        assertThat(row.getOutstandingAmount()).isEqualByComparingTo("2000");
        assertThat(row.getDealRevenue()).isEqualByComparingTo("3000");
    });
}

@Test
void getRevenueDashboardSeparatesTotalsByInvoiceCurrency() {
    InvoiceEntity inr = invoice(InvoiceStatus.PARTIALLY_PAID, BillingEntityType.SERVICE_REQUEST, new BigDecimal("100"));
    inr.setCurrencyCode("INR");
    inr.setTaxAmount(new BigDecimal("18"));
    InvoiceEntity usd = invoice(InvoiceStatus.SENT, BillingEntityType.SUBSCRIPTION, new BigDecimal("50"));
    usd.setCurrencyCode("USD");
    usd.setTaxAmount(new BigDecimal("5"));
    InvoiceEntity usdDeal = invoice(InvoiceStatus.PAID, BillingEntityType.DEAL, new BigDecimal("20"));
    usdDeal.setCurrencyCode("USD");
    when(invoiceRepository.findAll()).thenReturn(List.of(inr, usd, usdDeal));
    when(paymentRepository.sumSettledByInvoice(PaymentStatus.SUCCESS)).thenReturn(List.<Object[]>of(
            new Object[] { inr.getInvoiceId(), new BigDecimal("40") }));
    when(invoiceRepository.findByStatusIn(List.of(InvoiceStatus.SENT, InvoiceStatus.PARTIALLY_PAID, InvoiceStatus.OVERDUE)))
            .thenReturn(List.of(inr, usd));

    RevenueDashboardResponse response = service.getRevenueDashboard();

    assertThat(response.getTotalRevenue()).isNull();
    assertThat(response.getCollectedRevenue()).isNull();
    assertThat(response.getOutstandingRevenue()).isNull();
    assertThat(response.getServiceRevenue()).isNull();
    assertThat(response.getSubscriptionRevenue()).isNull();
    assertThat(response.getDealRevenue()).isNull();
    assertThat(response.getRevenueByCurrency()).hasSize(2);
    assertThat(response.getRevenueByCurrency()).extracting("currencyCode").containsExactly("INR", "USD");
    assertThat(response.getRevenueByCurrency().get(0).getTotalInvoiceValue()).isEqualByComparingTo("118");
    assertThat(response.getRevenueByCurrency().get(0).getCollectedAmount()).isEqualByComparingTo("40");
    assertThat(response.getRevenueByCurrency().get(0).getOutstandingAmount()).isEqualByComparingTo("78");
    assertThat(response.getRevenueByCurrency().get(1).getTotalInvoiceValue()).isEqualByComparingTo("75");
    assertThat(response.getRevenueByCurrency().get(1).getOutstandingAmount()).isEqualByComparingTo("55");
    assertThat(response.getRevenueByCurrency().get(1).getDealRevenue()).isEqualByComparingTo("20");
}

@Test
void getRevenueDashboardCountsPartialAndOverdueBalancesAndSettledAmounts() {
    InvoiceEntity partiallyPaid = invoice(InvoiceStatus.PARTIALLY_PAID, BillingEntityType.SERVICE_REQUEST, new BigDecimal("100"));
    partiallyPaid.setTaxAmount(new BigDecimal("10"));
    InvoiceEntity overdue = invoice(InvoiceStatus.OVERDUE, BillingEntityType.SERVICE_REQUEST, new BigDecimal("200"));
    when(invoiceRepository.count()).thenReturn(2L);
    when(paymentRepository.sumSettledByInvoice(PaymentStatus.SUCCESS)).thenReturn(List.<Object[]>of(
            new Object[] { partiallyPaid.getInvoiceId(), new BigDecimal("40") }));
    when(invoiceRepository.findByStatusIn(List.of(InvoiceStatus.SENT, InvoiceStatus.PARTIALLY_PAID, InvoiceStatus.OVERDUE)))
            .thenReturn(List.of(partiallyPaid, overdue));

    RevenueDashboardResponse response = service.getRevenueDashboard();

    assertThat(response.getCollectedRevenue()).isEqualByComparingTo("40");
    assertThat(response.getOutstandingRevenue()).isEqualByComparingTo("270");
}

@Test
void getRevenueDashboard_nullAmount() {

    List<InvoiceEntity> invoices =
            List.of(

                    invoice(
                            InvoiceStatus.PAID,
                            BillingEntityType.SERVICE_REQUEST,
                            null)
            );

    when(invoiceRepository.sumInvoiceAmounts()).thenReturn(null);

    RevenueDashboardResponse response =
            service.getRevenueDashboard();

    assertThat(response.getTotalRevenue())
            .isEqualByComparingTo(BigDecimal.ZERO);

    assertThat(response.getCollectedRevenue())
            .isEqualByComparingTo(BigDecimal.ZERO);
}

@Test
void getRevenueDashboard_otherStatus() {

    InvoiceEntity invoice =
            new InvoiceEntity();

    invoice.setStatus(
            InvoiceStatus.CANCELLED);

    invoice.setEntityType(
            BillingEntityType.SERVICE_REQUEST);

    invoice.setInvoiceAmount(
            BigDecimal.valueOf(500));

    when(invoiceRepository.count()).thenReturn(1L);
    when(invoiceRepository.sumInvoiceAmounts()).thenReturn(new BigDecimal("500"));

    RevenueDashboardResponse response =
            service.getRevenueDashboard();

    assertThat(response.getPaidInvoices())
            .isEqualTo(0);

    assertThat(response.getPendingInvoices())
            .isEqualTo(0);

    assertThat(response.getCollectedRevenue())
            .isEqualByComparingTo("0");

    assertThat(response.getOutstandingRevenue())
            .isEqualByComparingTo("0");
}

    
}
