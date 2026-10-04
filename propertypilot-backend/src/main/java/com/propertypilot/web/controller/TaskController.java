package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateTaskRequest;
import com.propertypilot.application.dto.TaskResponse;
import com.propertypilot.application.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@Tag(
        name = "Task Management",
        description = "APIs for task lifecycle management"
)
public class TaskController {

    private final TaskService taskService;

    public TaskController(
            TaskService taskService) {

        this.taskService = taskService;
    }

    @Operation(
            summary = "Create task",
            description =
                    "Creates a task for a service request"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Task created successfully"
    )
    @PostMapping
    public TaskResponse createTask(
            @RequestBody
            CreateTaskRequest request) {

        return taskService.createTask(
                request);
    }

    @Operation(
            summary = "Get task",
            description =
                    "Retrieves task by id"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Task retrieved successfully"
    )
    @GetMapping("/{taskId}")
    public TaskResponse getTask(
            @PathVariable UUID taskId) {

        return taskService.getTask(
                taskId);
    }

    @Operation(
            summary = "Start task",
            description =
                    "Moves task to IN_PROGRESS state"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Task started successfully"
    )
    @PostMapping("/{taskId}/start")
    public TaskResponse startTask(
            @PathVariable UUID taskId) {

        return taskService.startTask(
                taskId);
    }

    @Operation(
            summary = "Complete task",
            description =
                    "Marks task as completed"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Task completed successfully"
    )
    @PostMapping("/{taskId}/complete")
    public TaskResponse completeTask(
            @PathVariable UUID taskId) {

        return taskService.completeTask(
                taskId);
    }
}