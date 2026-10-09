package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.LeadAssignRequest;
import com.propertypilot.application.dto.LeadAssignmentResponse;
import com.propertypilot.application.dto.LeadCreateRequest;
import com.propertypilot.application.dto.LeadResponse;
import com.propertypilot.domain.enums.LeadStatus;
import com.propertypilot.infrastructure.persistence.entity.AgentEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.LeadActivityEntity;
import com.propertypilot.infrastructure.persistence.entity.LeadEntity;
import com.propertypilot.infrastructure.persistence.entity.UserEntity;
import com.propertypilot.infrastructure.persistence.repository.AgentRepository;
import com.propertypilot.infrastructure.persistence.repository.LeadActivityRepository;
import com.propertypilot.infrastructure.persistence.repository.LeadRepository;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeadServiceImplTest {

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private SecurityService securityService;

    @Mock
    private AgentRepository agentRepository;

    @Mock
    private LeadActivityRepository leadActivityRepository;

    @InjectMocks
    private LeadServiceImpl leadService;

    @Test
    void searchOperationsLeadsRequiresOperationsAndUsesDatabasePagination() {
        var pageable = PageRequest.of(2, 10);
        when(leadRepository.searchForOperations("owner", LeadStatus.NEW, pageable))
                .thenReturn(Page.empty(pageable));

        Page<?> result = leadService.searchOperationsLeads("  owner  ", LeadStatus.NEW, pageable);

        assertThat(result.getContent()).isEmpty();
        verify(securityService).validateOperationsAccess();
        verify(leadRepository).searchForOperations("owner", LeadStatus.NEW, pageable);
    }

    @Test
    void createLead_ShouldCreateLead() {

        CustomerEntity customer =
                new CustomerEntity();

        UserEntity user =
                new UserEntity();

        LeadCreateRequest request =
                new LeadCreateRequest(
                        "Website",
                        "PLOT",
                        "Hyderabad",
                        BigDecimal.valueOf(100000),
                        BigDecimal.valueOf(200000),
                        "Interested"
                );

        LeadEntity savedLead =
                new LeadEntity();

        savedLead.setSource("Website");
        savedLead.setStatus(LeadStatus.NEW);

        when(securityService.getCurrentCustomer())
                .thenReturn(customer);

        when(securityService.getCurrentUser())
                .thenReturn(user);

        when(leadRepository.save(any(LeadEntity.class)))
                .thenReturn(savedLead);

        LeadResponse response =
                leadService.createLead(request);

        assertThat(response).isNotNull();
        assertThat(response.source())
                .isEqualTo("Website");

        verify(leadRepository)
                .save(any(LeadEntity.class));

        verify(leadActivityRepository)
                .save(any(LeadActivityEntity.class));
    }

    @Test
    void getLead_ShouldReturnLead() {

        UUID leadId =
                UUID.randomUUID();

        LeadEntity lead =
                new LeadEntity();

        lead.setSource("Website");
        lead.setStatus(LeadStatus.NEW);

        when(leadRepository.findByLeadId(leadId))
                .thenReturn(Optional.of(lead));

        LeadResponse response =
                leadService.getLead(leadId);

        assertThat(response).isNotNull();

        verify(securityService)
                .validateLeadOwnership(lead);
    }

    @Test
    void getLead_ShouldThrowException_WhenNotFound() {

        UUID leadId =
                UUID.randomUUID();

        when(leadRepository.findByLeadId(leadId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> leadService.getLead(leadId))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }

    @Test
    void updateLeadStatus_ShouldUpdateStatus() {

        UUID leadId =
                UUID.randomUUID();

        UserEntity user =
                new UserEntity();

        LeadEntity lead =
                new LeadEntity();

        lead.setStatus(LeadStatus.NEW);

        when(securityService.getCurrentUser())
                .thenReturn(user);

        when(leadRepository.findByLeadId(leadId))
                .thenReturn(Optional.of(lead));

        when(leadRepository.save(any()))
                .thenReturn(lead);

        LeadResponse response =
                leadService.updateLeadStatus(
                        leadId,
                        LeadStatus.CONTACTED);

        assertThat(response).isNotNull();

        verify(securityService)
                .validateAdminAccess();

        verify(leadRepository)
                .save(any());
    }

    @Test
    void updateLeadStatus_ShouldRejectSkippingContactAndQualificationStages() {
        UUID leadId = UUID.randomUUID();
        LeadEntity lead = new LeadEntity();
        lead.setStatus(LeadStatus.NEW);
        when(leadRepository.findByLeadId(leadId)).thenReturn(Optional.of(lead));

        assertThatThrownBy(() -> leadService.updateLeadStatus(leadId, LeadStatus.READY_FOR_DEAL))
                .isInstanceOf(com.propertypilot.web.exception.BusinessException.class)
                .hasMessageContaining("Invalid lead status transition");

        verify(leadRepository, never()).save(any());
        verify(leadActivityRepository, never()).save(any());
    }

    @Test
    void updateLeadStatus_ShouldNotReopenTerminalLostLead() {
        UUID leadId = UUID.randomUUID();
        LeadEntity lead = new LeadEntity();
        lead.setStatus(LeadStatus.LOST);
        when(leadRepository.findByLeadId(leadId)).thenReturn(Optional.of(lead));

        assertThatThrownBy(() -> leadService.updateLeadStatus(leadId, LeadStatus.CONTACTED))
                .isInstanceOf(com.propertypilot.web.exception.BusinessException.class)
                .hasMessageContaining("Invalid lead status transition");

        verify(leadRepository, never()).save(any());
    }

    @Test
    void getMyLeads_ShouldReturnCustomerLeads() {

        UUID customerId =
                UUID.randomUUID();

        LeadEntity lead =
                new LeadEntity();

        lead.setStatus(LeadStatus.NEW);

        when(securityService.getCurrentCustomerId())
                .thenReturn(customerId);

        when(
                leadRepository
                        .findByCustomer_CustomerIdOrderByCreatedAtDesc(
                                customerId))
                .thenReturn(List.of(lead));

        List<LeadResponse> result =
                leadService.getMyLeads();

        assertThat(result)
                .hasSize(1);
    }

    @Test
    void getMyLeads_ShouldReturnOnlyLeadsAssignedToCurrentAgent() {
        UUID userId = UUID.randomUUID();
        UUID agentId = UUID.randomUUID();
        AgentEntity agent = new AgentEntity();
        agent.setAgentId(agentId);
        agent.setAgentCode("AG-TEST");

        LeadEntity assignedLead = new LeadEntity();
        assignedLead.setLeadId(UUID.randomUUID());
        assignedLead.setAssignedAgent(agent);
        assignedLead.setSource("Existing customer referral");
        assignedLead.setStatus(LeadStatus.NEW);

        when(securityService.hasRole("AGENT")).thenReturn(true);
        when(securityService.getCurrentUserId()).thenReturn(userId);
        when(agentRepository.findByUser_UserIdAndStatus(userId, "ACTIVE"))
                .thenReturn(Optional.of(agent));
        when(leadRepository.findByAssignedAgent_AgentIdOrderByCreatedAtDesc(agentId))
                .thenReturn(List.of(assignedLead));
        when(securityService.getCurrentCustomerId())
                .thenThrow(new ResourceNotFoundException("Customer profile not found"));

        List<LeadResponse> result = leadService.getMyLeads();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).leadId()).isEqualTo(assignedLead.getLeadId());
        verify(leadRepository).findByAssignedAgent_AgentIdOrderByCreatedAtDesc(agentId);
        verify(leadRepository, never()).findByCustomer_CustomerIdOrderByCreatedAtDesc(any());
    }

    @Test
    void getMyLeads_ShouldRejectAgentWithoutActiveAgentProfile() {
        UUID userId = UUID.randomUUID();
        when(securityService.hasRole("AGENT")).thenReturn(true);
        when(securityService.getCurrentUserId()).thenReturn(userId);
        when(agentRepository.findByUser_UserIdAndStatus(userId, "ACTIVE"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(leadService::getMyLeads)
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("active agent profile");
    }

    @Test
    void assignLead_ShouldAssignAgent() {

        UUID leadId =
                UUID.randomUUID();

        UUID agentId =
                UUID.randomUUID();

        UserEntity user =
                new UserEntity();

        LeadEntity lead =
                new LeadEntity();

        lead.setStatus(LeadStatus.NEW);

        AgentEntity agent =
                new AgentEntity();

        agent.setAgentId(agentId);
        agent.setAgentCode("AG001");

        when(securityService.getCurrentUser())
                .thenReturn(user);

        when(leadRepository.findByLeadId(leadId))
                .thenReturn(Optional.of(lead));

        when(agentRepository.findById(agentId))
                .thenReturn(Optional.of(agent));

        when(leadRepository.save(any()))
                .thenReturn(lead);

        LeadAssignmentResponse response =
                leadService.assignLead(
                        leadId,
                        new LeadAssignRequest(agentId));

        assertThat(response.agentId())
                .isEqualTo(agentId);

        verify(leadRepository)
                .save(any());

        verify(leadActivityRepository)
                .save(any(LeadActivityEntity.class));
    }
}
