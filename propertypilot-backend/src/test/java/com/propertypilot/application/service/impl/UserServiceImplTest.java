package com.propertypilot.application.service.impl;


import com.propertypilot.application.dto.UserCreateRequest;
import com.propertypilot.application.dto.UserResponse;
import com.propertypilot.application.dto.UserUpdateRequest;
import com.propertypilot.infrastructure.persistence.entity.RoleEntity;
import com.propertypilot.infrastructure.persistence.entity.UserEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerRepository;
import com.propertypilot.infrastructure.persistence.repository.RoleRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRoleRepository;
import com.propertypilot.web.exception.DuplicateResourceException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.propertypilot.domain.enums.UserStatus;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private UserServiceImpl service;


    private UserEntity user() {

    UserEntity user = new UserEntity();

    user.setUserId(UUID.randomUUID());
    user.setFullName("John Doe");
    user.setEmail("john@test.com");
    user.setMobileNumber("9999999999");
    user.setStatus(UserStatus.ACTIVE);

    return user;
}

private RoleEntity customerRole() {

    RoleEntity role = new RoleEntity();

    role.setRoleId(UUID.randomUUID());
    role.setRoleCode("CUSTOMER");
    role.setRoleName("Customer");

    return role;
}

@Test
void createUser_success() {

    UserCreateRequest request =
            new UserCreateRequest(
                    "John",
                    "john@test.com",
                    "9999999999",
                    "password123");

    when(userRepository.findByEmail(any()))
            .thenReturn(Optional.empty());

    when(userRepository.findByMobileNumber(any()))
            .thenReturn(Optional.empty());

    when(passwordEncoder.encode(any()))
            .thenReturn("encoded");

    when(userRepository.save(any()))
            .thenAnswer(i -> {

                UserEntity user =
                        i.getArgument(0);

                user.setUserId(UUID.randomUUID());

                return user;
            });

    when(roleRepository.findByRoleCode("CUSTOMER"))
            .thenReturn(Optional.of(customerRole()));

    UserResponse response =
            service.createUser(request);

    assertThat(response.fullName())
            .isEqualTo("John");

    verify(userRoleRepository)
            .save(any());

    verify(customerRepository)
            .save(any());
}
@Test
void createUser_duplicateEmail() {

    when(userRepository.findByEmail(any()))
            .thenReturn(Optional.of(user()));

    UserCreateRequest request =
            new UserCreateRequest(
                    "John",
                    "john@test.com",
                    "9999999999",
                    "password123");

    assertThatThrownBy(() ->
            service.createUser(request))
            .isInstanceOf(
                    DuplicateResourceException.class);
}

@Test
void createUser_duplicateMobile() {

    when(userRepository.findByEmail(any()))
            .thenReturn(Optional.empty());

    when(userRepository.findByMobileNumber(any()))
            .thenReturn(Optional.of(user()));

    UserCreateRequest request =
            new UserCreateRequest(
                    "John",
                    "john@test.com",
                    "9999999999",
                    "password123");

    assertThatThrownBy(() ->
            service.createUser(request))
            .isInstanceOf(
                    DuplicateResourceException.class);
}

@Test
void createUser_customerRoleMissing() {

    when(userRepository.findByEmail(any()))
            .thenReturn(Optional.empty());

    when(userRepository.findByMobileNumber(any()))
            .thenReturn(Optional.empty());

    when(passwordEncoder.encode(any()))
            .thenReturn("encoded");

    when(userRepository.save(any()))
            .thenReturn(user());

    when(roleRepository.findByRoleCode("CUSTOMER"))
            .thenReturn(Optional.empty());

    UserCreateRequest request =
            new UserCreateRequest(
                    "John",
                    "john@test.com",
                    "9999999999",
                    "password123");

    assertThatThrownBy(() ->
            service.createUser(request))
            .isInstanceOf(RuntimeException.class);
}

@Test
void createUser_normalizesEmail() {

    when(userRepository.findByEmail(any()))
            .thenReturn(Optional.empty());

    when(userRepository.findByMobileNumber(any()))
            .thenReturn(Optional.empty());

    when(passwordEncoder.encode(any()))
            .thenReturn("encoded");

    when(userRepository.save(any()))
            .thenAnswer(i -> i.getArgument(0));

    when(roleRepository.findByRoleCode("CUSTOMER"))
            .thenReturn(Optional.of(customerRole()));

    UserCreateRequest request =
            new UserCreateRequest(
                    "John",
                    " JOHN@TEST.COM ",
                    "9999999999",
                    "password123");

    service.createUser(request);

    verify(userRepository)
            .findByEmail("john@test.com");
}

@Test
void getAllUsers_success() {

    when(userRepository.findAll())
            .thenReturn(List.of(
                    user(),
                    user()));

    assertThat(
            service.getAllUsers())
            .hasSize(2);
}

@Test
void getAllUsers_empty() {

    when(userRepository.findAll())
            .thenReturn(List.of());

    assertThat(
            service.getAllUsers())
            .isEmpty();
}

@Test
void getUserById_success() {

    UserEntity user = user();

    when(userRepository.findById(
            user.getUserId()))
            .thenReturn(Optional.of(user));

    UserResponse response =
            service.getUserById(
                    user.getUserId());

    assertThat(response.fullName())
            .isEqualTo("John Doe");
}

@Test
void getUserById_notFound() {

    UUID id = UUID.randomUUID();

    when(userRepository.findById(id))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.getUserById(id))
            .isInstanceOf(RuntimeException.class);
}

@Test
void updateUser_success() {

    UserEntity user = user();

    when(userRepository.findById(
            user.getUserId()))
            .thenReturn(Optional.of(user));

    when(userRepository.save(any()))
            .thenAnswer(i -> i.getArgument(0));

    UserUpdateRequest request =
            new UserUpdateRequest(
                    "Updated",
                    "updated@test.com",
                    "8888888888");

    UserResponse response =
            service.updateUser(
                    user.getUserId(),
                    request);

    assertThat(response.fullName())
            .isEqualTo("Updated");
}

@Test
void updateUser_notFound() {

    UUID id = UUID.randomUUID();

    when(userRepository.findById(id))
            .thenReturn(Optional.empty());

    UserUpdateRequest request =
            new UserUpdateRequest(
                    "Updated",
                    "updated@test.com",
                    "8888888888");

    assertThatThrownBy(() ->
            service.updateUser(id, request))
            .isInstanceOf(RuntimeException.class);
}

@Test
void deleteUser_success() {

    UserEntity user = user();

    when(userRepository.findById(
            user.getUserId()))
            .thenReturn(Optional.of(user));

    service.deleteUser(
            user.getUserId());

    verify(userRepository)
            .delete(user);
}

@Test
void deleteUser_notFound() {

    UUID id = UUID.randomUUID();

    when(userRepository.findById(id))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.deleteUser(id))
            .isInstanceOf(RuntimeException.class);
}

@Test
void getCurrentUser_success() {

    UserEntity user = user();

    when(userRepository.findByEmail(
            "john@test.com"))
            .thenReturn(Optional.of(user));

    UserResponse response =
            service.getCurrentUser(
                    "john@test.com");

    assertThat(response.fullName())
            .isEqualTo("John Doe");
}

@Test
void getCurrentUser_notFound() {

    when(userRepository.findByEmail(any()))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.getCurrentUser(
                    "john@test.com"))
            .isInstanceOf(RuntimeException.class);
}


}