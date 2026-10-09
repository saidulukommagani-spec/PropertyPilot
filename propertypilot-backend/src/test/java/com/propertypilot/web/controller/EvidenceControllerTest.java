package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.CreateEvidenceRequest;
import com.propertypilot.application.dto.EvidenceResponse;
import com.propertypilot.application.service.EvidenceService;
import com.propertypilot.application.service.EvidenceStorageService;
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
import org.springframework.mock.web.MockMultipartFile;

import java.time.OffsetDateTime;
import java.io.IOException;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@WebMvcTest(EvidenceController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class EvidenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EvidenceService evidenceService;

    @MockitoBean
    private EvidenceStorageService evidenceStorageService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Direct evidence metadata creation is disabled")
    void createEvidence_withoutStoredUpload_isRejected()
            throws Exception {

        CreateEvidenceRequest request =
                new CreateEvidenceRequest();

        request.setVisitId(
                UUID.randomUUID());

        request.setEvidenceTypeId(
                UUID.randomUUID());

        request.setFileUrl(
                "https://files/sample.jpg");

        request.setMediaType(
                "image/jpeg");

        mockMvc.perform(
                        post("/api/evidence")
                                .contentType(
                                        "application/json")
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(
                        status().isBadRequest());
        verifyNoInteractions(evidenceService, evidenceStorageService);
    }

    @Test
    @DisplayName("Get Evidence")
    void getEvidence_success()
            throws Exception {

        UUID evidenceId =
                UUID.randomUUID();

        when(evidenceService.getEvidence(
                evidenceId))
                .thenReturn(
                        buildResponse());

        mockMvc.perform(
                        get(
                                "/api/evidence/{id}",
                                evidenceId))
                .andExpect(
                        status().isOk())
                .andExpect(
                        jsonPath(
                                "$.evidenceTypeCode")
                                .value("PHOTO"));
    }

    @Test
    @DisplayName("Upload requires exactly one parent association")
    void uploadEvidence_withoutVisitOrServiceRequest_isRejectedBeforeStorage() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "evidence.pdf", "application/pdf", new byte[]{1, 2, 3});

        mockMvc.perform(multipart("/api/evidence/upload")
                        .file(file)
                        .param("evidenceTypeId", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(evidenceStorageService, evidenceService);
    }

    @Test
    void uploadEvidence_cleansStoredFileWhenMetadataReadFails() throws Exception {
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        UUID requestId = UUID.randomUUID();
        when(file.getOriginalFilename()).thenReturn("evidence.pdf");
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getBytes()).thenThrow(new IOException("Unable to read upload"));
        when(evidenceStorageService.store(file)).thenReturn("stored-key.pdf");
        EvidenceController controller = new EvidenceController(evidenceService, evidenceStorageService);

        assertThatThrownBy(() -> controller.uploadEvidence(null, requestId, UUID.randomUUID(), file,
                null, null, null, null, null)).isInstanceOf(IOException.class);

        verify(evidenceStorageService).delete("stored-key.pdf");
        verifyNoInteractions(evidenceService);
    }

    private EvidenceResponse buildResponse() {

        EvidenceResponse response =
                new EvidenceResponse();

        response.setEvidenceId(
                UUID.randomUUID());

        response.setVisitId(
                UUID.randomUUID());

        response.setEvidenceTypeId(
                UUID.randomUUID());

        response.setEvidenceTypeCode(
                "PHOTO");

        response.setEvidenceTypeName(
                "Property Photo");

        response.setFileUrl(
                "https://files/sample.jpg");

        response.setMediaType(
                "image/jpeg");

        response.setChecksumSha256(
                "abc123");

        response.setCapturedAt(
                OffsetDateTime.now());

        return response;
    }
}
