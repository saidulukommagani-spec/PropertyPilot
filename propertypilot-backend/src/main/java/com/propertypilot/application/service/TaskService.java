package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreateTaskRequest;
import com.propertypilot.application.dto.TaskResponse;

import java.util.UUID;

public interface TaskService {

    TaskResponse createTask(
            CreateTaskRequest request);

    TaskResponse getTask(
            UUID taskId);

    TaskResponse startTask(
            UUID taskId);

    TaskResponse completeTask(
            UUID taskId);
}