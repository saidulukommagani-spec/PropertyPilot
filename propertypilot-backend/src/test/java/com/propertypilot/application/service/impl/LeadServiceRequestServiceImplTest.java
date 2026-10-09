package com.propertypilot.application.service.impl;

import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.PaymentStatus;
import com.propertypilot.infrastructure.persistence.entity.InvoiceEntity;
import com.propertypilot.infrastructure.persistence.entity.LeadEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.repository.AgentAssignmentRepository;
import com.propertypilot.infrastructure.persistence.repository.EvidenceRepository;
import com.propertypilot.infrastructure.persistence.repository.InvoiceRepository;
import com.propertypilot.infrastructure.persistence.repository.LeadRepository;
import com.propertypilot.infrastructure.persistence.repository.PaymentRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestStatusHistoryRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceSlaPolicyRepository;
import com.propertypilot.infrastructure.persistence.repository.VisitRepository;
import com.propertypilot.security.SecurityService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeadServiceRequestServiceImplTest {
    @Mock private LeadRepository leadRepository;
    @Mock private ServiceRequestRepository serviceRequestRepository;
    @Mock private ServiceRequestStatusHistoryRepository statusHistoryRepository;
    @Mock private ServiceSlaPolicyRepository slaPolicyRepository;
    @Mock private AgentAssignmentRepository assignmentRepository;
    @Mock private VisitRepository visitRepository;
    @Mock private EvidenceRepository evidenceRepository;
    @Mock private InvoiceRepository invoiceRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private SecurityService securityService;
    @InjectMocks private LeadServiceRequestServiceImpl service;

    @Test
    void linkedLeadRequestUsesTheCapturedSlaInsteadOfTheCurrentPolicy() {
        UUID leadId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        LeadEntity lead = new LeadEntity();
        lead.setLeadId(leadId);
        ServiceRequestEntity request = new ServiceRequestEntity();
        request.setServiceRequestId(requestId);
        request.setLead(lead);
        request.setStatus("IN_PROGRESS");
        request.setRequestedAt(Instant.now());
        request.setSlaSnapshotCaptured(true);
        request.setSlaDueAt(Instant.now().plusSeconds(3600));

        when(leadRepository.findByLeadId(leadId)).thenReturn(Optional.of(lead));
        when(serviceRequestRepository.findByLead_LeadIdOrderByRequestedAtDesc(leadId)).thenReturn(List.of(request));
        when(securityService.hasRole("AGENT")).thenReturn(false);
        when(invoiceRepository.findByEntityTypeAndEntityId(BillingEntityType.SERVICE_REQUEST, requestId)).thenReturn(List.of());
        when(evidenceRepository.countAllForServiceRequest(requestId)).thenReturn(3L);
        when(assignmentRepository.findByServiceRequest_ServiceRequestIdOrderByAssignedAtAsc(requestId)).thenReturn(List.of());
        when(statusHistoryRepository.findByServiceRequest_ServiceRequestIdOrderByChangedAtDesc(requestId)).thenReturn(List.of());

        var result = service.getLeadServiceRequests(leadId).getFirst();

        assertThat(result.slaDueAt()).isEqualTo(request.getSlaDueAt());
        assertThat(result.slaStatus()).isEqualTo("ON_TRACK");
        assertThat(result.evidenceCount()).isEqualTo(3);
        verify(evidenceRepository).countAllForServiceRequest(requestId);
        verifyNoInteractions(slaPolicyRepository);
    }

    @Test
    void leadRevenueSeparatesNetInvoiceValueFromVerifiedTaxInclusiveCollections() {
        UUID leadId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        UUID invoiceId = UUID.randomUUID();
        LeadEntity lead = new LeadEntity();
        lead.setLeadId(leadId);
        ServiceRequestEntity request = new ServiceRequestEntity();
        request.setServiceRequestId(requestId);
        request.setLead(lead);
        request.setStatus("IN_PROGRESS");
        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setInvoiceId(invoiceId);
        invoice.setEntityType(BillingEntityType.SERVICE_REQUEST);
        invoice.setEntityId(requestId);
        invoice.setInvoiceAmount(new BigDecimal("100.00"));
        invoice.setTaxAmount(new BigDecimal("18.00"));
        invoice.setCurrencyCode("INR");

        when(leadRepository.findByLeadId(leadId)).thenReturn(Optional.of(lead));
        when(serviceRequestRepository.findByLead_LeadIdOrderByRequestedAtDesc(leadId)).thenReturn(List.of(request));
        when(securityService.hasRole("AGENT")).thenReturn(false);
        when(invoiceRepository.findByEntityTypeAndEntityId(BillingEntityType.SERVICE_REQUEST, requestId)).thenReturn(List.of(invoice));
        when(paymentRepository.sumVerifiedSettledAmountByInvoiceAndCurrency(invoiceId, PaymentStatus.SUCCESS, "INR"))
                .thenReturn(new BigDecimal("118.00"));
        when(assignmentRepository.findByServiceRequest_ServiceRequestIdOrderByAssignedAtAsc(requestId)).thenReturn(List.of());
        when(statusHistoryRepository.findByServiceRequest_ServiceRequestIdOrderByChangedAtDesc(requestId)).thenReturn(List.of());

        var result = service.getLeadServiceRequests(leadId).getFirst();

        assertThat(result.revenueByCurrency()).singleElement().satisfies(summary -> {
            assertThat(summary.invoicedRevenue()).isEqualTo(new BigDecimal("100.00"));
            assertThat(summary.collectedRevenue()).isEqualTo(new BigDecimal("118.00"));
        });
        verify(paymentRepository).sumVerifiedSettledAmountByInvoiceAndCurrency(invoiceId, PaymentStatus.SUCCESS, "INR");
        verify(paymentRepository, never()).sumAmountByInvoiceAndStatus(invoiceId, PaymentStatus.SUCCESS);
    }
}
