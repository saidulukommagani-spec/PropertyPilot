package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.RevenueDashboardResponse;
import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.InvoiceStatus;
import com.propertypilot.infrastructure.persistence.entity.InvoiceEntity;
import com.propertypilot.infrastructure.persistence.repository.InvoiceRepository;

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

    @InjectMocks
    private RevenueDashboardServiceImpl service;

    private InvoiceEntity invoice(
        InvoiceStatus status,
        BillingEntityType type,
        BigDecimal amount) {

    InvoiceEntity invoice =
            new InvoiceEntity();

    invoice.setStatus(status);
    invoice.setEntityType(type);
    invoice.setInvoiceAmount(amount);

    return invoice;
}

@Test
void getRevenueDashboard_emptyInvoices() {

    when(invoiceRepository.findAll())
            .thenReturn(List.of());

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
                            InvoiceStatus.ISSUED,
                            BillingEntityType.SUBSCRIPTION,
                            BigDecimal.valueOf(2000)),

                    invoice(
                            InvoiceStatus.PAID,
                            BillingEntityType.DEAL,
                            BigDecimal.valueOf(3000))
            );

    when(invoiceRepository.findAll())
            .thenReturn(invoices);

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

    when(invoiceRepository.findAll())
            .thenReturn(invoices);

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

    when(invoiceRepository.findAll())
            .thenReturn(List.of(invoice));

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
