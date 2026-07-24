package com.tradesphere.ai.workflow.adapter.in.rest.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * REST DTO for rejecting a task
 */
@Data
public class RejectTaskRequest {
    
    @NotNull(message = "Task ID is required")
    private UUID taskId;
    
    @NotBlank(message = "Rejection reason is required")
    private String reason;
}


