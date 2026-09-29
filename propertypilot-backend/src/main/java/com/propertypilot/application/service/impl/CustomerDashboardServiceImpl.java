package com.propertypilot.application.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.propertypilot.application.dto.CustomerDashboardResponse;
import com.propertypilot.application.service.CustomerDashboardService;
import com.propertypilot.domain.enums.InvoiceStatus;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;
import com.propertypilot.infrastructure.persistence.repository.InvoiceRepository;
import com.propertypilot.infrastructure.persistence.repository.PaymentRepository;
import com.propertypilot.infrastructure.persistence.repository.PropertyRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestRepository;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class CustomerDashboardServiceImpl
        implements CustomerDashboardService {

        private final PropertyRepository propertyRepository;
        private final ServiceRequestRepository serviceRequestRepository;
        private final CustomerSubscriptionRepository customerSubscriptionRepository;
        private final InvoiceRepository invoiceRepository;
        private final PaymentRepository paymentRepository;

        public CustomerDashboardServiceImpl(
                        PropertyRepository propertyRepository,
                        ServiceRequestRepository serviceRequestRepository,
                        CustomerSubscriptionRepository customerSubscriptionRepository,
                        InvoiceRepository invoiceRepository,
                        PaymentRepository paymentRepository) {
                this.propertyRepository = propertyRepository;
                this.serviceRequestRepository = serviceRequestRepository;
                this.customerSubscriptionRepository = customerSubscriptionRepository;
                this.invoiceRepository = invoiceRepository;
                this.paymentRepository = paymentRepository;
        }

    @Override
    public CustomerDashboardResponse getDashboard(
            UUID customerId) {

        CustomerDashboardResponse response =
                new CustomerDashboardResponse();

        response.setTotalProperties(
                propertyRepository
                        .countByCustomer_CustomerId(
                                customerId));

        response.setTotalServiceRequests(
                serviceRequestRepository
                        .countByCustomer_CustomerId(
                                customerId));

        response.setActiveServiceRequests(
                serviceRequestRepository
                        .countByCustomer_CustomerIdAndStatus(
                                customerId,
                                "IN_PROGRESS"));

        response.setCompletedServiceRequests(
                serviceRequestRepository
                        .countByCustomer_CustomerIdAndStatus(
                                customerId,
                                "COMPLETED"));

        response.setActiveSubscriptions(
                customerSubscriptionRepository
                        .countByCustomer_CustomerIdAndStatus(
                                customerId,
                                "ACTIVE"));

        response.setPendingInvoices(
                invoiceRepository
                        .countByCustomerCustomerIdAndStatus(
                                customerId,
                                InvoiceStatus.ISSUED));

        response.setPaidInvoices(
                invoiceRepository
                        .countByCustomerCustomerIdAndStatus(
                                customerId,
                                InvoiceStatus.PAID));

        response.setTotalPayments(
                paymentRepository
                        .countByCustomer_CustomerId(
                                customerId));

        return response;
    }
}