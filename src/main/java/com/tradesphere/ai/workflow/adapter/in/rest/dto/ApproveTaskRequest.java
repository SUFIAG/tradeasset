package com.tradesphere.ai.workflow.adapter.in.rest.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * REST DTO for approving a task
 */
@Data
public class ApproveTaskRequest {
    
    @NotNull(message = "Task ID is required")
    private UUID taskId;
    
    private String comments;
}


