package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.LeadActivityCreateRequest;
import com.propertypilot.infrastructure.persistence.entity.LeadActivityEntity;
import com.propertypilot.infrastructure.persistence.entity.LeadEntity;
import com.propertypilot.infrastructure.persistence.repository.LeadActivityRepository;
import com.propertypilot.infrastructure.persistence.repository.LeadRepository;
import com.propertypilot.security.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LeadActivityServiceImplTest {
    private LeadActivityRepository activityRepository;
    private LeadRepository leadRepository;
    private SecurityService securityService;
    private LeadActivityServiceImpl service;
    private LeadEntity lead;
    private UUID leadId;

    @BeforeEach
    void setUp() {
        activityRepository = mock(LeadActivityRepository.class);
        leadRepository = mock(LeadRepository.class);
        securityService = mock(SecurityService.class);
        service = new LeadActivityServiceImpl(activityRepository, leadRepository, securityService);
        leadId = UUID.randomUUID();
        lead = new LeadEntity();
        lead.setLeadId(leadId);
        when(leadRepository.findByLeadId(leadId)).thenReturn(Optional.of(lead));
    }

    @Test
    void customerGetsOnlyExplicitlyCustomerVisibleActivities() {
        when(securityService.hasRole("CUSTOMER")).thenReturn(true);
        when(securityService.isOperationsUser()).thenReturn(false);
        LeadActivityEntity visible = activity("CUSTOMER_VISIBLE");
        LeadActivityEntity internal = activity("INTERNAL");
        LeadActivityEntity legacy = activity(null);
        when(activityRepository.findByLead_LeadIdAndVisibilityOrderByOccurredAtDesc(leadId, "CUSTOMER_VISIBLE"))
                .thenReturn(List.of(visible));

        var result = service.getActivities(leadId);

        assertEquals(1, result.size());
        assertEquals("CUSTOMER_VISIBLE", result.getFirst().visibility());
        verify(securityService).validateLeadOwnership(lead);
        verify(activityRepository, never()).findByLead_LeadIdOrderByOccurredAtDesc(leadId);
    }

    @Test
    void staffGetsHistoryAndLegacyRowsAreReportedInternal() {
        when(securityService.hasRole("CUSTOMER")).thenReturn(false);
        when(securityService.isOperationsUser()).thenReturn(true);
        LeadActivityEntity legacy = activity(null);
        when(activityRepository.findByLead_LeadIdOrderByOccurredAtDesc(leadId)).thenReturn(List.of(legacy));

        var result = service.getActivities(leadId);

        assertEquals("INTERNAL", result.getFirst().visibility());
    }

    @Test
    void customerSubmittedActivityGetsVisibilityFromAuthenticatedRole() {
        when(securityService.hasRole("CUSTOMER")).thenReturn(true);
        when(securityService.getCurrentUser()).thenReturn(null);
        when(activityRepository.save(any(LeadActivityEntity.class))).thenAnswer(invocation -> {
            LeadActivityEntity saved = invocation.getArgument(0);
            saved.setLeadActivityId(UUID.randomUUID());
            return saved;
        });

        var response = service.createActivity(leadId,
                new LeadActivityCreateRequest("NOTE", "Customer update", "Asked for contact", null, null));

        assertEquals("CUSTOMER_VISIBLE", response.visibility());
    }

    @Test
    void rejectsActivityReadWhenLeadOwnershipCheckFails() {
        doThrow(new AccessDeniedException("denied")).when(securityService).validateLeadOwnership(lead);
        assertThrows(AccessDeniedException.class, () -> service.getActivities(leadId));
        verifyNoInteractions(activityRepository);
    }

    private LeadActivityEntity activity(String visibility) {
        LeadActivityEntity activity = new LeadActivityEntity();
        activity.setLeadActivityId(UUID.randomUUID());
        activity.setLead(lead);
        activity.setActivityType("NOTE");
        activity.setSubject("Activity");
        activity.setOccurredAt(OffsetDateTime.now());
        activity.setVisibility(visibility);
        return activity;
    }
}
