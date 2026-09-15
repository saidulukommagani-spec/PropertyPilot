package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateTaskRequest;
import com.propertypilot.application.dto.TaskResponse;
import com.propertypilot.application.service.TaskService;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.entity.TaskEntity;
import com.propertypilot.infrastructure.persistence.entity.UserEntity;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestRepository;
import com.propertypilot.infrastructure.persistence.repository.TaskRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class TaskServiceImpl
        implements TaskService {

    private final TaskRepository taskRepository;

    private final ServiceRequestRepository serviceRequestRepository;

    private final UserRepository userRepository;

    public TaskServiceImpl(
            TaskRepository taskRepository,
            ServiceRequestRepository serviceRequestRepository,
            UserRepository userRepository) {

        this.taskRepository = taskRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.userRepository = userRepository;
    }

    @Override
    public TaskResponse createTask(
            CreateTaskRequest request) {

        ServiceRequestEntity serviceRequest =
                serviceRequestRepository.findById(
                                request.getServiceRequestId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Service request not found"));

        UserEntity assignee = null;

        if (request.getAssigneeUserId() != null) {

            assignee = userRepository.findById(
                            request.getAssigneeUserId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User not found"));
        }

        TaskEntity task = new TaskEntity();

        task.setServiceRequest(serviceRequest);
        task.setAssigneeUser(assignee);
        task.setTaskName(request.getTaskName());
        task.setTaskStatus("OPEN");
        task.setDueAt(request.getDueAt());

        return buildResponse(
                taskRepository.save(task));
    }

    @Override
    public TaskResponse getTask(
            UUID taskId) {

        return buildResponse(
                taskRepository.findById(taskId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Task not found")));
    }

    @Override
    public TaskResponse startTask(
            UUID taskId) {

        TaskEntity task =
                taskRepository.findById(taskId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Task not found"));

        task.setTaskStatus("IN_PROGRESS");

        return buildResponse(
                taskRepository.save(task));
    }

    @Override
    public TaskResponse completeTask(
            UUID taskId) {

        TaskEntity task =
                taskRepository.findById(taskId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Task not found"));

        task.setTaskStatus("COMPLETED");
        task.setCompletedAt(
                OffsetDateTime.now());

        return buildResponse(
                taskRepository.save(task));
    }

    private TaskResponse buildResponse(
            TaskEntity task) {

        TaskResponse response =
                new TaskResponse();

        response.setTaskId(
                task.getTaskId());

        response.setServiceRequestId(
                task.getServiceRequest()
                        .getServiceRequestId());

        response.setTaskName(
                task.getTaskName());

        response.setTaskStatus(
                task.getTaskStatus());

        response.setDueAt(
                task.getDueAt());

        response.setCompletedAt(
                task.getCompletedAt());

        if (task.getAssigneeUser() != null) {

            response.setAssigneeUserId(
                    task.getAssigneeUser()
                            .getUserId());
        }

        return response;
    }
}