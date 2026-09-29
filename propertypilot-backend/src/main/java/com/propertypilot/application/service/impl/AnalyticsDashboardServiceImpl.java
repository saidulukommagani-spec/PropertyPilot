package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.AnalyticsDashboardResponse;
import com.propertypilot.application.service.AnalyticsDashboardService;
import com.propertypilot.infrastructure.persistence.repository.CustomerRepository;
import com.propertypilot.infrastructure.persistence.repository.PropertyRepository;
import com.propertypilot.infrastructure.persistence.repository.LeadRepository;
import com.propertypilot.infrastructure.persistence.repository.DealRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestRepository;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsDashboardServiceImpl
        implements AnalyticsDashboardService {

    private final CustomerRepository
            customerRepository;

    private final PropertyRepository
            propertyRepository;

    private final LeadRepository
            leadRepository;

    private final DealRepository
            dealRepository;

    private final ServiceRequestRepository
            serviceRequestRepository;

    private final CustomerSubscriptionRepository
            customerSubscriptionRepository;

    public AnalyticsDashboardServiceImpl(
            CustomerRepository customerRepository,
            PropertyRepository propertyRepository,
            LeadRepository leadRepository,
            DealRepository dealRepository,
            ServiceRequestRepository serviceRequestRepository,
            CustomerSubscriptionRepository customerSubscriptionRepository) {

        this.customerRepository =
                customerRepository;

        this.propertyRepository =
                propertyRepository;

        this.leadRepository =
                leadRepository;

        this.dealRepository =
                dealRepository;

        this.serviceRequestRepository =
                serviceRequestRepository;

        this.customerSubscriptionRepository =
                customerSubscriptionRepository;
    }

    @Override
    public AnalyticsDashboardResponse
    getAnalyticsDashboard() {

        AnalyticsDashboardResponse response =
                new AnalyticsDashboardResponse();

        response.setTotalCustomers(
                customerRepository.count());

        response.setTotalProperties(
                propertyRepository.count());

        response.setTotalLeads(
                leadRepository.count());

        response.setTotalDeals(
                dealRepository.count());

        response.setTotalServiceRequests(
                serviceRequestRepository.count());

        response.setActiveSubscriptions(
                customerSubscriptionRepository
                        .countByStatus("ACTIVE"));

        return response;
    }
}