package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.UserCreateRequest;
import com.propertypilot.application.dto.UserResponse;
import com.propertypilot.application.dto.UserUpdateRequest;
import com.propertypilot.application.dto.UserAccessProvisionRequest;
import com.propertypilot.application.service.UserService;
import com.propertypilot.domain.enums.CustomerStatus;
import com.propertypilot.domain.enums.CustomerType;
import com.propertypilot.domain.enums.UserStatus;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.RoleEntity;
import com.propertypilot.infrastructure.persistence.entity.UserEntity;
import com.propertypilot.infrastructure.persistence.entity.UserRoleEntity;
import com.propertypilot.infrastructure.persistence.entity.AgentEntity;
import com.propertypilot.infrastructure.persistence.repository.AgentRepository;
import com.propertypilot.infrastructure.persistence.repository.CustomerRepository;
import com.propertypilot.infrastructure.persistence.repository.RoleRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRoleRepository;
import com.propertypilot.web.exception.DuplicateResourceException;
import com.propertypilot.security.SecurityService;
import com.propertypilot.domain.enums.RoleStatus;
import org.springframework.security.access.AccessDeniedException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final CustomerRepository customerRepository;
    private final AgentRepository agentRepository;
    private final SecurityService securityService;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            CustomerRepository customerRepository,
            AgentRepository agentRepository,
            SecurityService securityService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.customerRepository = customerRepository;
        this.agentRepository = agentRepository;
        this.securityService = securityService;
    }

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {

        String normalizedEmail =
                request.email().trim().toLowerCase();

        userRepository.findByEmail(normalizedEmail)
                .ifPresent(user -> {
                    throw new DuplicateResourceException(
                            "Email already exists");
                });

        userRepository.findByMobileNumber(
                        request.mobileNumber())
                .ifPresent(user -> {
                    throw new DuplicateResourceException(
                            "Mobile number already exists");
                });

        UserEntity userEntity = new UserEntity();

        userEntity.setFullName(request.fullName());
        userEntity.setEmail(normalizedEmail);
        userEntity.setMobileNumber(request.mobileNumber());

        userEntity.setPasswordHash(
                passwordEncoder.encode(request.password()));

        userEntity.setStatus(UserStatus.ACTIVE);

        UserEntity savedUser =
                userRepository.save(userEntity);

        RoleEntity customerRole =
                roleRepository.findByRoleCode("CUSTOMER")
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "CUSTOMER role not found"));

        UserRoleEntity userRole =
                new UserRoleEntity();

        userRole.setUser(savedUser);
        userRole.setRole(customerRole);
        userRole.setEffectiveFrom(OffsetDateTime.now());

        userRoleRepository.save(userRole);

        CustomerEntity customer =
                new CustomerEntity();

        customer.setCustomerId(UUID.randomUUID());
        customer.setUser(savedUser);
        customer.setCustomerType(CustomerType.INDIVIDUAL);
        customer.setStatus(CustomerStatus.ACTIVE);

        customerRepository.save(customer);

        return mapToUserResponse(savedUser);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .toList();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUserById(UUID userId) {

        UserEntity user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found: " + userId));

        return mapToUserResponse(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(
            UUID userId,
            UserUpdateRequest request) {

        UserEntity user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found: " + userId));

        user.setFullName(request.fullName());

        String normalizedEmail =
                request.email().trim().toLowerCase();

        String normalizedMobile = request.mobileNumber().trim();
        if (userRepository.existsByEmailAndUserIdNot(normalizedEmail, userId)) {
            throw new DuplicateResourceException("Email already exists");
        }
        if (userRepository.existsByMobileNumberAndUserIdNot(normalizedMobile, userId)) {
            throw new DuplicateResourceException("Mobile number already exists");
        }

        user.setEmail(normalizedEmail);
        user.setMobileNumber(normalizedMobile);

        UserEntity updatedUser =
                userRepository.save(user);

        return mapToUserResponse(updatedUser);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(UUID userId) {

        UserEntity user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found: " + userId));

        userRepository.delete(user);
    }

    @Override
    public UserResponse getCurrentUser(String email) {

        String normalizedEmail =
                email.trim().toLowerCase();

        UserEntity user =
                userRepository.findByEmail(normalizedEmail)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found: " + email));

        return mapToUserResponse(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserResponse provisionAccess(UUID userId, UserAccessProvisionRequest request) {
        UserEntity target = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        UUID actorId = securityService.getCurrentUserId();
        if (actorId.equals(userId)) {
            throw new AccessDeniedException("Administrators cannot change their own access");
        }

        String roleCode = request.roleCode().trim().toUpperCase();
        RoleEntity targetRole = roleRepository.findByRoleCode(roleCode)
                .filter(role -> role.getStatus() == RoleStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Selected access role is unavailable"));
        List<UserRoleEntity> assignments = userRoleRepository.findByUser_UserIdAndEffectiveToIsNull(userId);
        if (assignments.stream().anyMatch(assignment -> assignment.getRole() != null
                && "ADMIN".equals(assignment.getRole().getRoleCode()))) {
            throw new AccessDeniedException("Administrator accounts must be managed through a separate privileged process");
        }
        if ("AGENT".equals(roleCode)) {
            ensureAgentProfile(target, request.region());
        } else {
            deactivateAgentProfile(target, actorId);
        }

        OffsetDateTime now = OffsetDateTime.now();
        Set<String> retainedRoles = request.retainCustomerAccess()
                ? Set.of("CUSTOMER", roleCode)
                : Set.of(roleCode);
        UserRoleEntity targetAssignment = assignments.stream()
                .filter(assignment -> assignment.getRole() != null && roleCode.equals(assignment.getRole().getRoleCode()))
                .findFirst().orElse(null);
        boolean targetRoleAlreadyActive = targetAssignment != null;
        if (targetAssignment != null && targetAssignment.getEffectiveFrom() != null
                && targetAssignment.getEffectiveFrom().isAfter(now)) {
            targetAssignment.setEffectiveFrom(now);
        }

        for (UserRoleEntity assignment : assignments) {
            String assignedCode = assignment.getRole() == null ? "" : assignment.getRole().getRoleCode();
            if (roleCode.equals(assignedCode) && targetRoleAlreadyActive) {
                assignment.setChangeReason(request.reason().trim());
                assignment.setUpdatedBy(actorId);
            }
            if (!retainedRoles.contains(assignedCode)) {
                OffsetDateTime effectiveTo = now;
                if (assignment.getEffectiveFrom() != null && !effectiveTo.isAfter(assignment.getEffectiveFrom())) {
                    effectiveTo = assignment.getEffectiveFrom().plusSeconds(1);
                }
                assignment.setEffectiveTo(effectiveTo);
                assignment.setUpdatedBy(actorId);
                assignment.setChangeReason(request.reason().trim());
            }
        }
        if (request.retainCustomerAccess()) {
            boolean customerAccessActive = assignments.stream().anyMatch(assignment ->
                    assignment.getRole() != null && "CUSTOMER".equals(assignment.getRole().getRoleCode()));
            if (!customerAccessActive) {
                RoleEntity customerRole = roleRepository.findByRoleCode("CUSTOMER")
                        .filter(role -> role.getStatus() == RoleStatus.ACTIVE)
                        .orElseThrow(() -> new IllegalArgumentException("Customer access role is unavailable"));
                UserRoleEntity customerAssignment = new UserRoleEntity();
                customerAssignment.setUser(target);
                customerAssignment.setRole(customerRole);
                customerAssignment.setEffectiveFrom(now);
                customerAssignment.setCreatedBy(actorId);
                customerAssignment.setUpdatedBy(actorId);
                customerAssignment.setChangeReason(request.reason().trim());
                assignments.add(customerAssignment);
            }
        }
        if (!targetRoleAlreadyActive) {
            UserRoleEntity newAssignment = new UserRoleEntity();
            newAssignment.setUser(target);
            newAssignment.setRole(targetRole);
            newAssignment.setEffectiveFrom(now);
            newAssignment.setCreatedBy(actorId);
            newAssignment.setUpdatedBy(actorId);
            newAssignment.setChangeReason(request.reason().trim());
            assignments.add(newAssignment);
        }
        userRoleRepository.saveAll(assignments);
        return mapToUserResponse(target);
    }

    private void ensureAgentProfile(UserEntity user, String region) {
        AgentEntity agent = agentRepository.findByUser_UserId(user.getUserId()).orElseGet(() -> {
            AgentEntity created = new AgentEntity();
            created.setUser(user);
            created.setAgentCode(generateAgentCode());
            created.setCommissionRate(BigDecimal.ZERO);
            created.setStatus("ACTIVE");
            return created;
        });
        if (region != null && !region.isBlank()) {
            agent.setRegion(region.trim());
        }
        agent.setStatus("ACTIVE");
        agentRepository.save(agent);
    }

    private void deactivateAgentProfile(UserEntity user, UUID actorId) {
        agentRepository.findByUser_UserId(user.getUserId()).ifPresent(agent -> {
            agent.setStatus("INACTIVE");
            agent.setUpdatedBy(actorId);
            agentRepository.save(agent);
        });
    }

    private String generateAgentCode() {
        String code;
        do {
            code = "PPAG-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        } while (agentRepository.existsByAgentCode(code));
        return code;
    }

    /**
     * Centralized mapper for UserResponse.
     */
    private UserResponse mapToUserResponse(UserEntity user) {

        List<String> roles =
                userRoleRepository.findByUser_UserId(user.getUserId())
                        .stream()
                        .filter(userRole ->
                                userRole.getRole() != null
                                        && userRole.getRole().getStatus() == RoleStatus.ACTIVE
                                        && (userRole.getEffectiveFrom() == null || !userRole.getEffectiveFrom().isAfter(OffsetDateTime.now()))
                                        && (userRole.getEffectiveTo() == null || userRole.getEffectiveTo().isAfter(OffsetDateTime.now())))
                        .map(userRole ->
                                userRole.getRole().getRoleCode())
                        .distinct()
                        .sorted()
                        .toList();

        return new UserResponse(
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getMobileNumber(),
                user.getStatus().name(),
                roles
        );
    }
}
