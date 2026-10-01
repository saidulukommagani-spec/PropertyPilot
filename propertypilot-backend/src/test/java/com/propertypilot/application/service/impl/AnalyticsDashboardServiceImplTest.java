package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.AnalyticsDashboardResponse;
import com.propertypilot.infrastructure.persistence.repository.CustomerRepository;
import com.propertypilot.infrastructure.persistence.repository.PropertyRepository;
import com.propertypilot.infrastructure.persistence.repository.LeadRepository;
import com.propertypilot.infrastructure.persistence.repository.DealRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestRepository;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsDashboardServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private DealRepository dealRepository;

    @Mock
    private ServiceRequestRepository serviceRequestRepository;

    @Mock
    private CustomerSubscriptionRepository
            customerSubscriptionRepository;

    @InjectMocks
    private AnalyticsDashboardServiceImpl service;

    @Test
    void getAnalyticsDashboard_success() {

        when(customerRepository.count())
                .thenReturn(10L);

        when(propertyRepository.count())
                .thenReturn(20L);

        when(leadRepository.count())
                .thenReturn(30L);

        when(dealRepository.count())
                .thenReturn(40L);

        when(serviceRequestRepository.count())
                .thenReturn(50L);

        when(customerSubscriptionRepository
                .countByStatus("ACTIVE"))
                .thenReturn(5L);

        AnalyticsDashboardResponse response =
                service.getAnalyticsDashboard();

        assertThat(response).isNotNull();

        assertThat(response.getTotalCustomers())
                .isEqualTo(10L);

        assertThat(response.getTotalProperties())
                .isEqualTo(20L);

        assertThat(response.getTotalLeads())
                .isEqualTo(30L);

        assertThat(response.getTotalDeals())
                .isEqualTo(40L);

        assertThat(response.getTotalServiceRequests())
                .isEqualTo(50L);

        assertThat(response.getActiveSubscriptions())
                .isEqualTo(5L);
    }
}