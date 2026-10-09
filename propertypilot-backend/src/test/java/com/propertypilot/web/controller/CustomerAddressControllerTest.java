package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.CreateCustomerAddressRequest;
import com.propertypilot.application.dto.CustomerAddressResponse;
import com.propertypilot.application.dto.UpdateCustomerAddressRequest;
import com.propertypilot.application.service.CustomerAddressService;
import com.propertypilot.security.CustomUserDetailsService;
import com.propertypilot.security.JwtService;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerAddressController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class CustomerAddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerAddressService customerAddressService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private SecurityService securityService;

    @Test
    @DisplayName("Create Address")
    void createAddress_success() throws Exception {

        UUID customerId = UUID.randomUUID();

        when(customerAddressService.createAddress(
                eq(customerId),
                any(CreateCustomerAddressRequest.class)))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        post("/api/customers/{customerId}/addresses",
                                customerId)
                                .contentType("application/json")
                                .content("""
                                        {
                                          "localityId":"11111111-1111-1111-1111-111111111111",
                                          "addressType":"HOME",
                                          "addressLine1":"Flat 101",
                                          "addressLine2":"Near Park",
                                          "city":"Hyderabad",
                                          "postalCode":"500070",
                                          "countryCode":"IN",
                                          "isPrimary":true
                                        }
                                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.city")
                        .value("Hyderabad"));
    }

    @Test
    @DisplayName("Get Customer Addresses")
    void getAddresses_success() throws Exception {

        UUID customerId = UUID.randomUUID();

        when(customerAddressService.getCustomerAddresses(
                customerId))
                .thenReturn(List.of(buildResponse()));

        mockMvc.perform(
                        get("/api/customers/{customerId}/addresses",
                                customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].city")
                        .value("Hyderabad"));
    }

    @Test
    @DisplayName("Update Address")
    void updateAddress_success() throws Exception {

        UUID addressId = UUID.randomUUID();

        when(customerAddressService.updateAddress(
                eq(addressId),
                any(UpdateCustomerAddressRequest.class)))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        put("/api/customers/addresses/{addressId}",
                                addressId)
                                .contentType("application/json")
                                .content("""
                                        {
                                          "localityId":"11111111-1111-1111-1111-111111111111",
                                          "addressType":"HOME",
                                          "addressLine1":"Flat 202",
                                          "addressLine2":"Updated",
                                          "city":"Hyderabad",
                                          "postalCode":"500070",
                                          "countryCode":"IN",
                                          "isPrimary":true,
                                          "status":"ACTIVE"
                                        }
                                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city")
                        .value("Hyderabad"));
    }

    @Test
    @DisplayName("Delete Address")
    void deleteAddress_success() throws Exception {

        UUID addressId = UUID.randomUUID();

        doNothing().when(customerAddressService)
                .deleteAddress(addressId);

        mockMvc.perform(
                        delete("/api/customers/addresses/{addressId}",
                                addressId))
                .andExpect(status().isNoContent());
    }

    private CustomerAddressResponse buildResponse() {

        return new CustomerAddressResponse(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Vanasthalipuram",
                "HOME",
                "Flat 101",
                "Near Park",
                "Hyderabad",
                "500070",
                "IN",
                true,
                "ACTIVE"
        );
    }
}
