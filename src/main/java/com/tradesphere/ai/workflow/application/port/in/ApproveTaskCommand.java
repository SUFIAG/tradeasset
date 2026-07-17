package com.tradesphere.ai.workflow.application.port.in;

import lombok.Value;

import java.util.UUID;

/**
 * Command to approve a workflow task
 */
@Value
public class ApproveTaskCommand {
    UUID workflowId;
    UUID taskId;
    UUID userId;
    String comments;
    UUID tenantId;
}


