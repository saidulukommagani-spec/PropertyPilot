package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateTaskRequest;
import com.propertypilot.application.dto.TaskResponse;
import com.propertypilot.application.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(
            TaskService taskService) {

        this.taskService = taskService;
    }

    @PostMapping
    public TaskResponse createTask(
            @RequestBody
            CreateTaskRequest request) {

                System.out.println(
            "TASK CONTROLLER HIT");
        return taskService.createTask(
                request);
    }

    @GetMapping("/{taskId}")
    public TaskResponse getTask(
            @PathVariable UUID taskId) {

        return taskService.getTask(
                taskId);
    }

    @PostMapping("/{taskId}/start")
    public TaskResponse startTask(
            @PathVariable UUID taskId) {

        return taskService.startTask(
                taskId);
    }

    @PostMapping("/{taskId}/complete")
    public TaskResponse completeTask(
            @PathVariable UUID taskId) {

        return taskService.completeTask(
                taskId);
    }
}