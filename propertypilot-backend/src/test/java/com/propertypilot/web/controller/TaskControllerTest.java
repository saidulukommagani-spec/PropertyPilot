package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.CreateTaskRequest;
import com.propertypilot.application.dto.TaskResponse;
import com.propertypilot.application.service.TaskService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest
        extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @Test
    void createTask()
            throws Exception {

        CreateTaskRequest request =
                new CreateTaskRequest();

        request.setServiceRequestId(
                UUID.randomUUID());

        request.setAssigneeUserId(
                UUID.randomUUID());

        request.setTaskName(
                "Property Inspection");

        request.setDueAt(
                OffsetDateTime.now());

        when(taskService.createTask(
                any()))
                .thenReturn(
                        new TaskResponse());

        mockMvc.perform(
                        post("/api/tasks")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(status().isOk());
    }

    @Test
    void getTask()
            throws Exception {

        UUID taskId =
                UUID.randomUUID();

        when(taskService.getTask(
                taskId))
                .thenReturn(
                        new TaskResponse());

        mockMvc.perform(
                        get("/api/tasks/{taskId}",
                                taskId))
                .andExpect(status().isOk());
    }

    @Test
    void startTask()
            throws Exception {

        UUID taskId =
                UUID.randomUUID();

        when(taskService.startTask(
                taskId))
                .thenReturn(
                        new TaskResponse());

        mockMvc.perform(
                        post("/api/tasks/{taskId}/start",
                                taskId))
                .andExpect(status().isOk());
    }

    @Test
    void completeTask()
            throws Exception {

        UUID taskId =
                UUID.randomUUID();

        when(taskService.completeTask(
                taskId))
                .thenReturn(
                        new TaskResponse());

        mockMvc.perform(
                        post("/api/tasks/{taskId}/complete",
                                taskId))
                .andExpect(status().isOk());
    }
}