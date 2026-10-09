package com.propertypilot.security;

import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.DealEntity;
import com.propertypilot.infrastructure.persistence.entity.Document;
import com.propertypilot.infrastructure.persistence.entity.Property;
import com.propertypilot.infrastructure.persistence.entity.UserEntity;
import com.propertypilot.infrastructure.persistence.entity.UserRoleEntity;
import com.propertypilot.infrastructure.persistence.entity.AgentEntity;
import com.propertypilot.infrastructure.persistence.entity.AgentAssignmentEntity;
import com.propertypilot.infrastructure.persistence.entity.EvidenceEntity;
import com.propertypilot.infrastructure.persistence.entity.InvoiceEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.entity.VisitEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRoleRepository;
import com.propertypilot.infrastructure.persistence.repository.AgentRepository;
import com.propertypilot.infrastructure.persistence.repository.AgentAssignmentRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.propertypilot.infrastructure.persistence.entity.LeadEntity;
import java.util.UUID;
import java.time.OffsetDateTime;
import com.propertypilot.domain.enums.RoleStatus;

@Service
public class SecurityService {

    private final UserRepository userRepository;

    private final CustomerRepository customerRepository;

    private final UserRoleRepository userRoleRepository;

    private final AgentRepository agentRepository;
    private final AgentAssignmentRepository agentAssignmentRepository;

    public SecurityService(
            UserRepository userRepository,
            CustomerRepository customerRepository,
            UserRoleRepository userRoleRepository,
            AgentRepository agentRepository,
            AgentAssignmentRepository agentAssignmentRepository) {

        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.userRoleRepository = userRoleRepository;
        this.agentRepository = agentRepository;
        this.agentAssignmentRepository = agentAssignmentRepository;
    }

    public String getCurrentUsername() {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || "anonymousUser".equals(authentication.getName())) {

            throw new ResourceNotFoundException(
                    "No authenticated user found");
        }

        return authentication.getName();
    }

    public UserEntity getCurrentUser() {

        return userRepository.findByEmail(
                        getCurrentUsername())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Logged-in user not found"));
    }

    public UUID getCurrentUserId() {

        return getCurrentUser()
                .getUserId();
    }

    public CustomerEntity getCurrentCustomer() {

        return customerRepository.findByUser_Email(
                        getCurrentUsername())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found"));
    }
    public UUID getCurrentCustomerId() {

    return getCurrentCustomer()
            .getCustomerId();
}

    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    public boolean hasRole(String roleCode) {
        if (roleCode == null) {
            return false;
        }

        OffsetDateTime now = OffsetDateTime.now();
        return userRoleRepository.findByUser_UserId(getCurrentUserId())
                .stream()
                .filter(userRole -> userRole.getRole() != null
                        && userRole.getRole().getStatus() == RoleStatus.ACTIVE
                        && (userRole.getEffectiveFrom() == null || !userRole.getEffectiveFrom().isAfter(now))
                        && (userRole.getEffectiveTo() == null || userRole.getEffectiveTo().isAfter(now)))
                .map(UserRoleEntity::getRole)
                .anyMatch(role -> roleCode.equalsIgnoreCase(role.getRoleCode()));
    }

    public boolean isOperationsUser() {
        return isAdmin() || hasRole("OPERATIONS");
    }

    public void validateCustomerAccess(CustomerEntity customer) {
        if (customer == null) {
            throw new AccessDeniedException("Customer access denied");
        }
        validateCustomerAccess(customer.getCustomerId());
    }

    public void validateCustomerAccess(UUID customerId) {
        if (isOperationsUser()) {
            return;
        }
        if (!getCurrentCustomerId().equals(customerId)) {
            throw new AccessDeniedException("You do not have access to this customer");
        }
    }

    public void validateServiceRequestAccess(ServiceRequestEntity request) {
        if (request == null) {
            throw new AccessDeniedException("Service request access denied");
        }
        if (isOperationsUser()) {
            return;
        }

        try {
            validateCustomerAccess(request.getCustomer());
            return;
        } catch (ResourceNotFoundException | AccessDeniedException ignored) {
            // An agent may access a request only through their own assignment.
        }

        UUID currentUserId = getCurrentUserId();
        AgentEntity currentAgent = agentRepository
                .findByUser_UserIdAndStatus(currentUserId, "ACTIVE")
                .orElse(null);
        boolean requestAgentMatches = currentAgent != null
                && currentAgent.getAgentId().equals(request.getAssignedTo());
        boolean activeAssignmentMatches = currentAgent != null
                && request.getServiceRequestId() != null
                && agentAssignmentRepository
                    .findByServiceRequest_ServiceRequestIdAndAssignmentStatusIn(
                            request.getServiceRequestId(),
                            java.util.List.of("ASSIGNED", "ACCEPTED", "IN_PROGRESS"))
                    .filter(assignment -> assignment.getAgent() != null
                            && currentAgent.getAgentId().equals(assignment.getAgent().getAgentId()))
                    .isPresent();
        if (!requestAgentMatches && !activeAssignmentMatches) {
            throw new AccessDeniedException("This work is not assigned to you");
        }
    }

    public void validateAssignedAgent(UUID assignedAgentId) {
        if (isOperationsUser()) {
            return;
        }
        UUID userId = getCurrentUserId();
        AgentEntity currentAgent = agentRepository.findByUser_UserIdAndStatus(userId, "ACTIVE")
                .orElse(null);
        if (currentAgent == null || assignedAgentId == null
                || !currentAgent.getAgentId().equals(assignedAgentId)) {
            throw new AccessDeniedException("This work is not assigned to you");
        }
    }

    public void validateAssignmentAccess(AgentAssignmentEntity assignment) {
        if (assignment == null || assignment.getAgent() == null) {
            throw new AccessDeniedException("Assignment access denied");
        }
        if (isOperationsUser()) {
            return;
        }
        UUID currentUserId = getCurrentUserId();
        if (assignment.getAgent().getUser() == null
                || !currentUserId.equals(assignment.getAgent().getUser().getUserId())) {
            throw new AccessDeniedException("This assignment is not yours");
        }
    }

    public void validateVisitAccess(VisitEntity visit) {
        if (visit == null) {
            throw new AccessDeniedException("Visit access denied");
        }
        if (isOperationsUser()) {
            return;
        }
        if (visit.getServiceRequest() != null) {
            try {
                validateCustomerAccess(visit.getServiceRequest().getCustomer());
                return;
            } catch (ResourceNotFoundException | AccessDeniedException ignored) {
                // Continue with the assigned-agent check.
            }
        }
        if (visit.getAssignment() != null) {
            validateAssignmentAccess(visit.getAssignment());
            return;
        }
        if (visit.getAgent() != null && visit.getAgent().getUser() != null
                && getCurrentUserId().equals(visit.getAgent().getUser().getUserId())) {
            return;
        }
        throw new AccessDeniedException("You do not have access to this visit");
    }

    public void validateEvidenceAccess(EvidenceEntity evidence) {
        if (evidence == null) {
            throw new AccessDeniedException("Evidence access denied");
        }
        if (evidence.getVisit() != null) {
            validateVisitAccess(evidence.getVisit());
            return;
        }
        if (evidence.getServiceRequest() != null) {
            validateServiceRequestAccess(evidence.getServiceRequest());
            return;
        }
        throw new AccessDeniedException("Evidence access denied");
    }

    public void validateVisitExecutionAccess(VisitEntity visit) {
        if (visit == null) {
            throw new AccessDeniedException("Visit access denied");
        }
        if (isOperationsUser()) {
            return;
        }
        if (visit.getAssignment() != null) {
            validateAssignmentAccess(visit.getAssignment());
            return;
        }
        if (visit.getAgent() != null && visit.getAgent().getUser() != null
                && getCurrentUserId().equals(visit.getAgent().getUser().getUserId())) {
            return;
        }
        throw new AccessDeniedException("Only the assigned agent may execute this visit");
    }

    public void validateServiceRequestExecutionAccess(ServiceRequestEntity request) {
        if (request == null) {
            throw new AccessDeniedException("Service request access denied");
        }
        if (isOperationsUser()) {
            return;
        }
        UUID currentUserId = getCurrentUserId();
        AgentEntity currentAgent = agentRepository
                .findByUser_UserIdAndStatus(currentUserId, "ACTIVE")
                .orElse(null);
        boolean assigned = currentAgent != null
                && (currentAgent.getAgentId().equals(request.getAssignedTo())
                || agentAssignmentRepository
                .findByServiceRequest_ServiceRequestIdAndAssignmentStatusIn(
                        request.getServiceRequestId(), java.util.List.of("ASSIGNED", "ACCEPTED", "IN_PROGRESS"))
                .filter(assignment -> assignment.getAgent() != null
                        && currentAgent.getAgentId().equals(assignment.getAgent().getAgentId()))
                .isPresent());
        if (!assigned) {
            throw new AccessDeniedException("Only the assigned agent may add service evidence");
        }
    }

    public void validateInvoiceAccess(InvoiceEntity invoice) {
        if (invoice == null) {
            throw new AccessDeniedException("Invoice access denied");
        }
        validateCustomerAccess(invoice.getCustomer());
    }

    public void validateAdminAccess() {
        if (!isAdmin()) {
            throw new AccessDeniedException("Admin access required");
        }
    }

    public void validateOperationsAccess() {
        if (!isOperationsUser()) {
            throw new AccessDeniedException("Operations access required");
        }
    }

    /**
     * Property Ownership Validation
     */
    public void validatePropertyOwnership(
            Property property) {

        if (isOperationsUser()) {
            return;
        }

        UUID currentCustomerId =
                getCurrentCustomer()
                        .getCustomerId();

        UUID ownerCustomerId =
                property.getCustomer()
                        .getCustomerId();

        if (!currentCustomerId.equals(
                ownerCustomerId)) {

            throw new AccessDeniedException(
                    "You do not have access to this property");
        }
    }

    /**
     * Document Ownership Validation
     * Reuses property ownership validation.
     */
    public void validateDocumentOwnership(
            Document document) {

        validatePropertyOwnership(
                document.getProperty());
    }

public void validateLeadOwnership(
        LeadEntity lead) {

    if (isOperationsUser()) {
        return;
    }

    UUID currentUserId = getCurrentUserId();
    CustomerEntity currentCustomer = customerRepository.findByUser_Email(getCurrentUsername()).orElse(null);
    if (currentCustomer != null && lead.getCustomer() != null
            && currentCustomer.getCustomerId().equals(lead.getCustomer().getCustomerId())) {
        return;
    }
    if (lead.getAssignedAgent() != null && lead.getAssignedAgent().getUser() != null
            && currentUserId.equals(lead.getAssignedAgent().getUser().getUserId())) {
        return;
    }

    throw new AccessDeniedException("You do not have access to this lead");
}

public void validateDealAccess(
        DealEntity deal) {

    if (isAdmin()) {
        return;
    }

    UUID currentCustomerId =
            getCurrentCustomerId();

    if (deal.getSeller()
            .getCustomerId()
            .equals(currentCustomerId)) {

        return;
    }

    if (deal.getSelectedBuyerId() != null
            && deal.getSelectedBuyerId()
                    .equals(currentCustomerId)) {

        return;
    }

    throw new AccessDeniedException(
            "You are not authorized to access this deal");
}

}
