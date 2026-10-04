package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.CreateCustomerFavoriteRequest;
import com.propertypilot.application.dto.CustomerFavoriteResponse;
import com.propertypilot.application.service.CustomerFavoriteService;
import com.propertypilot.security.CustomUserDetailsService;
import com.propertypilot.security.JwtService;
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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerFavoriteController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class CustomerFavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerFavoriteService customerFavoriteService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Create Favorite")
    void createFavorite_success() throws Exception {

        UUID customerId =
                UUID.randomUUID();

        CreateCustomerFavoriteRequest request =
                new CreateCustomerFavoriteRequest();

        request.setFavoriteType("PROPERTY");
        request.setPropertyId(UUID.randomUUID());

        CustomerFavoriteResponse response =
                buildResponse();

    when(customerFavoriteService.createFavorite(
        org.mockito.ArgumentMatchers.eq(customerId),
        org.mockito.ArgumentMatchers.any(
                CreateCustomerFavoriteRequest.class)))
        .thenReturn(response);

        mockMvc.perform(
                        post("/api/customers/{customerId}/favorites",
                                customerId)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favoriteType")
                        .value("PROPERTY"));
    }

    @Test
    @DisplayName("Get Customer Favorites")
    void getCustomerFavorites_success()
            throws Exception {

        UUID customerId =
                UUID.randomUUID();

        when(customerFavoriteService
                .getCustomerFavorites(customerId))
                .thenReturn(
                        List.of(buildResponse()));

        mockMvc.perform(
                        get("/api/customers/{customerId}/favorites",
                                customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].favoriteType")
                        .value("PROPERTY"));
    }

    @Test
    @DisplayName("Delete Favorite")
    void deleteFavorite_success()
            throws Exception {

        UUID favoriteId =
                UUID.randomUUID();

        doNothing().when(
                        customerFavoriteService)
                .deleteFavorite(
                        favoriteId);

        mockMvc.perform(
                        delete("/api/customers/favorites/{favoriteId}",
                                favoriteId))
                .andExpect(status().isNoContent());
    }

    private CustomerFavoriteResponse
    buildResponse() {

        CustomerFavoriteResponse response =
                new CustomerFavoriteResponse();

        response.setCustomerFavoriteId(
                UUID.randomUUID());

        response.setCustomerId(
                UUID.randomUUID());

        response.setFavoriteType(
                "PROPERTY");

        response.setPropertyId(
                UUID.randomUUID());

        response.setMarketplaceListingId(
                UUID.randomUUID());

        return response;
    }
}