package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.DocumentTypeResponse;
import com.propertypilot.application.service.DocumentTypeService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentTypeController.class)
@AutoConfigureMockMvc(addFilters = false)
class DocumentTypeControllerTest
        extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DocumentTypeService
            documentTypeService;

    @Test
    void getByPropertyCategory_ShouldReturnDocumentTypes()
            throws Exception {

        DocumentTypeResponse response =
                buildResponse();

        when(documentTypeService
                .getByPropertyCategory(
                        "RESIDENTIAL"))
                .thenReturn(
                        List.of(response));

        mockMvc.perform(
                        get("/api/v1/document-types/property-category/RESIDENTIAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].propertyCategory")
                        .value("RESIDENTIAL"))
                .andExpect(jsonPath("$[0].documentCode")
                        .value("SALE_DEED"));
    }

    private DocumentTypeResponse
    buildResponse() {

        DocumentTypeResponse response =
                new DocumentTypeResponse();

        response.setDocumentTypeId(
                UUID.randomUUID());

        response.setPropertyCategory(
                "RESIDENTIAL");

        response.setDocumentCode(
                "SALE_DEED");

        response.setDocumentName(
                "Sale Deed");

        response.setMandatory(
                true);

        return response;
    }
}