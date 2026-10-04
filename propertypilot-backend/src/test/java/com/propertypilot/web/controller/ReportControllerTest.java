package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.CreateReportRequest;
import com.propertypilot.application.dto.ReportResponse;
import com.propertypilot.application.service.ReportService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReportControllerTest
        extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReportService reportService;

    @Test
    void createReport()
            throws Exception {

        CreateReportRequest request =
                new CreateReportRequest();

        request.setServiceRequestId(
                UUID.randomUUID());

        request.setReportType(
                "INSPECTION");

        ReportResponse response =
                new ReportResponse();

        when(reportService.createReport(
                any()))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/reports")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(status().isCreated());
    }

    @Test
    void getReport()
            throws Exception {

        UUID reportId =
                UUID.randomUUID();

        when(reportService.getReport(
                reportId))
                .thenReturn(
                        new ReportResponse());

        mockMvc.perform(
                        get("/api/reports/{reportId}",
                                reportId))
                .andExpect(status().isOk());
    }

    @Test
    void submitReport()
            throws Exception {

        UUID reportId =
                UUID.randomUUID();

        when(reportService.submitReport(
                reportId))
                .thenReturn(
                        new ReportResponse());

        mockMvc.perform(
                        put("/api/reports/{reportId}/submit",
                                reportId))
                .andExpect(status().isOk());
    }

    @Test
    void approveReport()
            throws Exception {

        UUID reportId =
                UUID.randomUUID();

        when(reportService.approveReport(
                reportId))
                .thenReturn(
                        new ReportResponse());

        mockMvc.perform(
                        put("/api/reports/{reportId}/approve",
                                reportId))
                .andExpect(status().isOk());
    }

    @Test
    void deliverReport()
            throws Exception {

        UUID reportId =
                UUID.randomUUID();

        when(reportService.deliverReport(
                reportId))
                .thenReturn(
                        new ReportResponse());

        mockMvc.perform(
                        put("/api/reports/{reportId}/deliver",
                                reportId))
                .andExpect(status().isOk());
    }
}