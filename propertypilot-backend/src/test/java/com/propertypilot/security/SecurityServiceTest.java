package com.propertypilot.security;

import com.propertypilot.infrastructure.persistence.entity.*;
import com.propertypilot.infrastructure.persistence.repository.CustomerRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRoleRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private SecurityService securityService;

    private UUID userId;
    private UUID customerId;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        userId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        SecurityContextHolder.getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                "admin@test.com",
                                "password"));
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    private UserEntity createUser() {

        UserEntity user = new UserEntity();
        user.setUserId(userId);
        user.setEmail("admin@test.com");

        return user;
    }

    private CustomerEntity createCustomer(UUID id) {

        CustomerEntity customer = new CustomerEntity();
        customer.setCustomerId(id);

        return customer;
    }

    private void mockAdmin() {

        UserEntity user = createUser();

        RoleEntity role = new RoleEntity();
        role.setRoleCode("ADMIN");

        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setRole(role);

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(user));

        when(userRoleRepository.findByUser_UserId(userId))
                .thenReturn(List.of(userRole));
    }

    private void mockNonAdmin() {

        UserEntity user = createUser();

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(user));

        when(userRoleRepository.findByUser_UserId(userId))
                .thenReturn(List.of());
    }

    @Test
    void getCurrentUsername_ShouldReturnUsername() {

        assertEquals(
                "admin@test.com",
                securityService.getCurrentUsername());
    }

    @Test
    void getCurrentUsername_ShouldThrowException() {

        SecurityContextHolder.clearContext();

        assertThrows(
                ResourceNotFoundException.class,
                () -> securityService.getCurrentUsername());
    }

    @Test
    void getCurrentUser_ShouldReturnUser() {

        UserEntity user = createUser();

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(user));

        assertEquals(
                userId,
                securityService.getCurrentUser().getUserId());
    }

    @Test
    void getCurrentUser_ShouldThrowException() {

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> securityService.getCurrentUser());
    }

    @Test
    void getCurrentUserId_ShouldReturnUserId() {

        UserEntity user = createUser();

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(user));

        assertEquals(
                userId,
                securityService.getCurrentUserId());
    }

    @Test
    void getCurrentCustomer_ShouldReturnCustomer() {

        CustomerEntity customer =
                createCustomer(customerId);

        when(customerRepository.findByUser_Email(anyString()))
                .thenReturn(Optional.of(customer));

        assertEquals(
                customerId,
                securityService.getCurrentCustomer()
                        .getCustomerId());
    }

    @Test
    void getCurrentCustomer_ShouldThrowException() {

        when(customerRepository.findByUser_Email(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> securityService.getCurrentCustomer());
    }

    @Test
    void getCurrentCustomerId_ShouldReturnCustomerId() {

        CustomerEntity customer =
                createCustomer(customerId);

        when(customerRepository.findByUser_Email(anyString()))
                .thenReturn(Optional.of(customer));

        assertEquals(
                customerId,
                securityService.getCurrentCustomerId());
    }

    @Test
    void isAdmin_ShouldReturnTrue() {

        mockAdmin();

        assertTrue(
                securityService.isAdmin());
    }

    @Test
    void isAdmin_ShouldReturnFalse() {

        mockNonAdmin();

        assertFalse(
                securityService.isAdmin());
    }

    @Test
    void validateAdminAccess_ShouldPass() {

        mockAdmin();

        assertDoesNotThrow(
                () -> securityService.validateAdminAccess());
    }

    @Test
    void validateAdminAccess_ShouldThrowException() {

        mockNonAdmin();

        assertThrows(
                AccessDeniedException.class,
                () -> securityService.validateAdminAccess());
    }

    @Test
    void validatePropertyOwnership_ShouldPassForAdmin() {

        mockAdmin();

        Property property = mock(Property.class);

        assertDoesNotThrow(
                () -> securityService.validatePropertyOwnership(property));
    }

    @Test
    void validatePropertyOwnership_ShouldPassForOwner() {

        mockNonAdmin();

        CustomerEntity customer =
                createCustomer(customerId);

        when(customerRepository.findByUser_Email(anyString()))
                .thenReturn(Optional.of(customer));

        Property property = mock(Property.class);

        when(property.getCustomer())
                .thenReturn(customer);

        assertDoesNotThrow(
                () -> securityService.validatePropertyOwnership(property));
    }

    @Test
    void validatePropertyOwnership_ShouldThrowException() {

        mockNonAdmin();

        CustomerEntity currentCustomer =
                createCustomer(customerId);

        CustomerEntity ownerCustomer =
                createCustomer(UUID.randomUUID());

        when(customerRepository.findByUser_Email(anyString()))
                .thenReturn(Optional.of(currentCustomer));

        Property property = mock(Property.class);

        when(property.getCustomer())
                .thenReturn(ownerCustomer);

        assertThrows(
                AccessDeniedException.class,
                () -> securityService.validatePropertyOwnership(property));
    }

    @Test
    void validateDocumentOwnership_ShouldPass() {

        mockAdmin();

        Document document = mock(Document.class);

        assertDoesNotThrow(
                () -> securityService.validateDocumentOwnership(document));
    }

    @Test
    void validateDocumentOwnership_ShouldThrowException() {

        mockNonAdmin();

        CustomerEntity currentCustomer =
                createCustomer(customerId);

        CustomerEntity ownerCustomer =
                createCustomer(UUID.randomUUID());

        when(customerRepository.findByUser_Email(anyString()))
                .thenReturn(Optional.of(currentCustomer));

        Property property = mock(Property.class);
        Document document = mock(Document.class);

        when(property.getCustomer())
                .thenReturn(ownerCustomer);

        when(document.getProperty())
                .thenReturn(property);

        assertThrows(
                AccessDeniedException.class,
                () -> securityService.validateDocumentOwnership(document));
    }

    @Test
    void validateLeadOwnership_ShouldPassForAdmin() {

        mockAdmin();

        LeadEntity lead = mock(LeadEntity.class);

        assertDoesNotThrow(
                () -> securityService.validateLeadOwnership(lead));
    }

    @Test
    void validateLeadOwnership_ShouldPassForOwner() {

        mockNonAdmin();

        CustomerEntity customer =
                createCustomer(customerId);

        when(customerRepository.findByUser_Email(anyString()))
                .thenReturn(Optional.of(customer));

        LeadEntity lead = mock(LeadEntity.class);

        when(lead.getCustomer())
                .thenReturn(customer);

        assertDoesNotThrow(
                () -> securityService.validateLeadOwnership(lead));
    }

    @Test
    void validateLeadOwnership_ShouldThrowException() {

        mockNonAdmin();

        CustomerEntity currentCustomer =
                createCustomer(customerId);

        CustomerEntity leadCustomer =
                createCustomer(UUID.randomUUID());

        when(customerRepository.findByUser_Email(anyString()))
                .thenReturn(Optional.of(currentCustomer));

        LeadEntity lead = mock(LeadEntity.class);

        when(lead.getCustomer())
                .thenReturn(leadCustomer);

        assertThrows(
                AccessDeniedException.class,
                () -> securityService.validateLeadOwnership(lead));
    }

    @Test
    void validateDealAccess_ShouldPassForAdmin() {

        mockAdmin();

        DealEntity deal = mock(DealEntity.class);

        assertDoesNotThrow(
                () -> securityService.validateDealAccess(deal));
    }

    @Test
    void validateDealAccess_ShouldPassForSeller() {

        mockNonAdmin();

        CustomerEntity customer =
                createCustomer(customerId);

        when(customerRepository.findByUser_Email(anyString()))
                .thenReturn(Optional.of(customer));

        DealEntity deal = mock(DealEntity.class);

        when(deal.getSeller())
                .thenReturn(customer);

        assertDoesNotThrow(
                () -> securityService.validateDealAccess(deal));
    }

    @Test
    void validateDealAccess_ShouldPassForBuyer() {

        mockNonAdmin();

        CustomerEntity customer =
                createCustomer(customerId);

        when(customerRepository.findByUser_Email(anyString()))
                .thenReturn(Optional.of(customer));

        DealEntity deal = mock(DealEntity.class);

        when(deal.getSeller())
                .thenReturn(createCustomer(UUID.randomUUID()));

        when(deal.getSelectedBuyerId())
                .thenReturn(customerId);

        assertDoesNotThrow(
                () -> securityService.validateDealAccess(deal));
    }

    @Test
    void validateDealAccess_ShouldThrowException() {

        mockNonAdmin();

        CustomerEntity customer =
                createCustomer(customerId);

        when(customerRepository.findByUser_Email(anyString()))
                .thenReturn(Optional.of(customer));

        DealEntity deal = mock(DealEntity.class);

        when(deal.getSeller())
                .thenReturn(createCustomer(UUID.randomUUID()));

        when(deal.getSelectedBuyerId())
                .thenReturn(UUID.randomUUID());

        assertThrows(
                AccessDeniedException.class,
                () -> securityService.validateDealAccess(deal));
    }
}