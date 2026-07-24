package com.tradesphere.ai.workflow.application.port.in;

import lombok.Value;

import java.util.UUID;

/**
 * Command to reject a workflow task
 */
@Value
public class RejectTaskCommand {
    UUID workflowId;
    UUID taskId;
    UUID userId;
    String reason;
    UUID tenantId;
}


