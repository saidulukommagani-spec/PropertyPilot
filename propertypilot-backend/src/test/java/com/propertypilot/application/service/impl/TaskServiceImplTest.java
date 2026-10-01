package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateTaskRequest;
import com.propertypilot.application.dto.TaskResponse;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.entity.TaskEntity;
import com.propertypilot.infrastructure.persistence.entity.UserEntity;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestRepository;
import com.propertypilot.infrastructure.persistence.repository.TaskRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ServiceRequestRepository serviceRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskServiceImpl service;

    @Test
    void createTask_success_withAssignee() {

        UUID serviceRequestId =
                UUID.randomUUID();

        UUID userId =
                UUID.randomUUID();

        CreateTaskRequest request =
                new CreateTaskRequest();

        request.setServiceRequestId(
                serviceRequestId);

        request.setAssigneeUserId(
                userId);

        request.setTaskName(
                "Property Visit");

        request.setDueAt(
                OffsetDateTime.now().plusDays(1));

        ServiceRequestEntity serviceRequest =
                new ServiceRequestEntity();

        serviceRequest.setServiceRequestId(
                serviceRequestId);

        UserEntity user =
                new UserEntity();

        user.setUserId(
                userId);

        TaskEntity savedTask =
                new TaskEntity();

        savedTask.setTaskId(
                UUID.randomUUID());

        savedTask.setServiceRequest(
                serviceRequest);

        savedTask.setAssigneeUser(
                user);

        savedTask.setTaskName(
                "Property Visit");

        savedTask.setTaskStatus(
                "OPEN");

        when(serviceRequestRepository.findById(
                serviceRequestId))
                .thenReturn(
                        Optional.of(serviceRequest));

        when(userRepository.findById(
                userId))
                .thenReturn(
                        Optional.of(user));

        when(taskRepository.save(any()))
                .thenReturn(savedTask);

        TaskResponse response =
                service.createTask(
                        request);

        assertThat(response)
                .isNotNull();

        assertThat(
                response.getTaskName())
                .isEqualTo(
                        "Property Visit");

        assertThat(
                response.getAssigneeUserId())
                .isEqualTo(userId);

        verify(taskRepository)
                .save(any(TaskEntity.class));
    }

    @Test
    void createTask_success_withoutAssignee() {

        UUID serviceRequestId =
                UUID.randomUUID();

        CreateTaskRequest request =
                new CreateTaskRequest();

        request.setServiceRequestId(
                serviceRequestId);

        request.setTaskName(
                "Visit");

        ServiceRequestEntity serviceRequest =
                new ServiceRequestEntity();

        serviceRequest.setServiceRequestId(
                serviceRequestId);

        TaskEntity savedTask =
                new TaskEntity();

        savedTask.setTaskId(
                UUID.randomUUID());

        savedTask.setServiceRequest(
                serviceRequest);

        savedTask.setTaskName(
                "Visit");

        savedTask.setTaskStatus(
                "OPEN");

        when(serviceRequestRepository.findById(
                serviceRequestId))
                .thenReturn(
                        Optional.of(serviceRequest));

        when(taskRepository.save(any()))
                .thenReturn(savedTask);

        TaskResponse response =
                service.createTask(
                        request);

        assertThat(response)
                .isNotNull();

        assertThat(
                response.getAssigneeUserId())
                .isNull();

        verify(userRepository,
                never()).findById(any());
    }

    @Test
    void createTask_serviceRequestNotFound() {

        UUID serviceRequestId =
                UUID.randomUUID();

        CreateTaskRequest request =
                new CreateTaskRequest();

        request.setServiceRequestId(
                serviceRequestId);

        when(serviceRequestRepository.findById(
                serviceRequestId))
                .thenReturn(
                        Optional.empty());

        assertThatThrownBy(() ->
                service.createTask(request))
                .isInstanceOf(
                        ResourceNotFoundException.class)
                .hasMessage(
                        "Service request not found");
    }

    @Test
    void createTask_assigneeNotFound() {

        UUID serviceRequestId =
                UUID.randomUUID();

        UUID userId =
                UUID.randomUUID();

        CreateTaskRequest request =
                new CreateTaskRequest();

        request.setServiceRequestId(
                serviceRequestId);

        request.setAssigneeUserId(
                userId);

        ServiceRequestEntity serviceRequest =
                new ServiceRequestEntity();

        serviceRequest.setServiceRequestId(
                serviceRequestId);

        when(serviceRequestRepository.findById(
                serviceRequestId))
                .thenReturn(
                        Optional.of(serviceRequest));

        when(userRepository.findById(
                userId))
                .thenReturn(
                        Optional.empty());

        assertThatThrownBy(() ->
                service.createTask(request))
                .isInstanceOf(
                        ResourceNotFoundException.class)
                .hasMessage(
                        "User not found");
    }

    @Test
    void getTask_success() {

        UUID taskId =
                UUID.randomUUID();

        ServiceRequestEntity serviceRequest =
                new ServiceRequestEntity();

        serviceRequest.setServiceRequestId(
                UUID.randomUUID());

        TaskEntity task =
                new TaskEntity();

        task.setTaskId(taskId);
        task.setServiceRequest(serviceRequest);
        task.setTaskName("Visit");
        task.setTaskStatus("OPEN");

        when(taskRepository.findById(taskId))
                .thenReturn(
                        Optional.of(task));

        TaskResponse response =
                service.getTask(taskId);

        assertThat(response)
                .isNotNull();

        assertThat(
                response.getTaskId())
                .isEqualTo(taskId);
    }

    @Test
    void getTask_notFound() {

        UUID taskId =
                UUID.randomUUID();

        when(taskRepository.findById(taskId))
                .thenReturn(
                        Optional.empty());

        assertThatThrownBy(() ->
                service.getTask(taskId))
                .isInstanceOf(
                        ResourceNotFoundException.class)
                .hasMessage(
                        "Task not found");
    }

    @Test
    void startTask_success() {

        UUID taskId =
                UUID.randomUUID();

        ServiceRequestEntity serviceRequest =
                new ServiceRequestEntity();

        serviceRequest.setServiceRequestId(
                UUID.randomUUID());

        TaskEntity task =
                new TaskEntity();

        task.setTaskId(taskId);
        task.setServiceRequest(serviceRequest);
        task.setTaskStatus("OPEN");

        when(taskRepository.findById(taskId))
                .thenReturn(
                        Optional.of(task));

        when(taskRepository.save(any()))
                .thenReturn(task);

        TaskResponse response =
                service.startTask(taskId);

        assertThat(
                response.getTaskStatus())
                .isEqualTo(
                        "IN_PROGRESS");
    }

    @Test
    void startTask_notFound() {

        UUID taskId =
                UUID.randomUUID();

        when(taskRepository.findById(taskId))
                .thenReturn(
                        Optional.empty());

        assertThatThrownBy(() ->
                service.startTask(taskId))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }

    @Test
    void completeTask_success() {

        UUID taskId =
                UUID.randomUUID();

        ServiceRequestEntity serviceRequest =
                new ServiceRequestEntity();

        serviceRequest.setServiceRequestId(
                UUID.randomUUID());

        TaskEntity task =
                new TaskEntity();

        task.setTaskId(taskId);
        task.setServiceRequest(serviceRequest);
        task.setTaskStatus("OPEN");

        when(taskRepository.findById(taskId))
                .thenReturn(
                        Optional.of(task));

        when(taskRepository.save(any()))
                .thenReturn(task);

        TaskResponse response =
                service.completeTask(taskId);

        assertThat(
                response.getTaskStatus())
                .isEqualTo(
                        "COMPLETED");

        assertThat(
                response.getCompletedAt())
                .isNotNull();
    }

    @Test
    void completeTask_notFound() {

        UUID taskId =
                UUID.randomUUID();

        when(taskRepository.findById(taskId))
                .thenReturn(
                        Optional.empty());

        assertThatThrownBy(() ->
                service.completeTask(taskId))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }
}