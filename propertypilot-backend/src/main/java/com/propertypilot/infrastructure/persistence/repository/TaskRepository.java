package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository
        extends JpaRepository<TaskEntity, UUID> {

    List<TaskEntity>
    findByServiceRequest_ServiceRequestId(
            UUID serviceRequestId);

    List<TaskEntity>
    findByAssigneeUser_UserId(
            UUID userId);

    List<TaskEntity>
    findByTaskStatus(
            String taskStatus);
}