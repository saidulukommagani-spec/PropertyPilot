package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.UserCreateRequest;
import com.propertypilot.application.dto.UserResponse;
import com.propertypilot.application.dto.UserUpdateRequest;
import com.propertypilot.application.service.UserService;
import com.propertypilot.domain.enums.CustomerStatus;
import com.propertypilot.domain.enums.CustomerType;
import com.propertypilot.domain.enums.UserStatus;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.RoleEntity;
import com.propertypilot.infrastructure.persistence.entity.UserEntity;
import com.propertypilot.infrastructure.persistence.entity.UserRoleEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerRepository;
import com.propertypilot.infrastructure.persistence.repository.RoleRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRoleRepository;
import com.propertypilot.web.exception.DuplicateResourceException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final CustomerRepository customerRepository;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            CustomerRepository customerRepository) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.customerRepository = customerRepository;
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

    /**
     * Centralized mapper for UserResponse.
     */
    private UserResponse mapToUserResponse(UserEntity user) {

        List<String> roles =
                userRoleRepository.findByUser_UserId(user.getUserId())
                        .stream()
                        .filter(userRole ->
                                userRole.getRole() != null
                                        && userRole.getRole().getStatus() != null)
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
