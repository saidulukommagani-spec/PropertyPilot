package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.SubscriptionNotificationResponse;
import com.propertypilot.application.service.SubscriptionNotificationService;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionNotificationEntity;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionNotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.ResourceNotFoundException;

@Service
public class SubscriptionNotificationServiceImpl
        implements SubscriptionNotificationService {

    private final SubscriptionNotificationRepository
            notificationRepository;
    private final CustomerSubscriptionRepository customerSubscriptionRepository;
    private final SecurityService securityService;

    public SubscriptionNotificationServiceImpl(
            SubscriptionNotificationRepository notificationRepository,
            CustomerSubscriptionRepository customerSubscriptionRepository,
            SecurityService securityService) {

        this.notificationRepository =
                notificationRepository;
        this.customerSubscriptionRepository = customerSubscriptionRepository;
        this.securityService = securityService;
    }

    @Override
    public List<SubscriptionNotificationResponse>
    getNotifications(
            UUID customerSubscriptionId) {

        CustomerSubscriptionEntity subscription = customerSubscriptionRepository.findById(customerSubscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer subscription not found"));
        securityService.validateCustomerAccess(subscription.getCustomer());

        return notificationRepository
                .findByCustomerSubscription_CustomerSubscriptionId(
                        customerSubscriptionId)
                .stream()
                .map(this::map)
                .toList();
    }

    private SubscriptionNotificationResponse map(
            SubscriptionNotificationEntity entity) {

        SubscriptionNotificationResponse response =
                new SubscriptionNotificationResponse();

        response.setNotificationId(
                entity.getNotificationId());

        response.setCustomerSubscriptionId(
                entity.getCustomerSubscription()
                        .getCustomerSubscriptionId());

        response.setNotificationType(
                entity.getNotificationType());

        response.setMessage(
                entity.getMessage());

        response.setStatus(
                entity.getStatus());

        response.setSentDate(
                entity.getSentDate());

        return response;
    }
}
