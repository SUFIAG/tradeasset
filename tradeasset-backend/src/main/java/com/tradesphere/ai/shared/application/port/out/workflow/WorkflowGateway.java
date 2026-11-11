package com.tradesphere.ai.shared.application.port.out.workflow;

import com.tradesphere.ai.shared.domain.valueobject.Money;

import java.util.Set;
import java.util.UUID;

/**
 * Cross-module approval workflow (REQ-WFL-001..005). Implemented by the workflow module. Owning
 * modules open a task when a record needs a human decision and close it when the decision is made.
 * The task is a work queue item; the decision itself is recorded by the owning module through
 * {@code MakerChecker}.
 */
public interface WorkflowGateway {

    /**
     * Opens a task routed by type, amount and risk. The maker is never a valid assignee.
     *
     * @return the task ID
     */
    UUID openTask(OpenTask request);

    /** Completes every open task of the given type for the record. No-op when none is open. */
    void completeTasks(UUID tenantId, String entityType, UUID entityId, String taskType, UUID completedBy,
                       String outcome);

    /** Cancels every open task for the record, for example when it is withdrawn. */
    void cancelTasks(UUID tenantId, String entityType, UUID entityId, String reason);

    /**
     * @param taskType       stable code, for example LC_APPROVAL, LC_ISSUANCE, LC_PAYMENT_AUTHORIZATION,
     *                       CUSTOMER_APPROVAL, CREDIT_LIMIT_APPROVAL, SCREENING_REVIEW
     * @param candidateRoles roles allowed to work the task
     * @param amount         may be null; drives routing and priority
     * @param riskRating     LOW, MEDIUM, HIGH, CRITICAL or null
     */
    record OpenTask(UUID tenantId, String entityType, UUID entityId, String reference, String taskType,
                    String title, Money amount, String riskRating, UUID makerId, Set<String> candidateRoles) {
    }
}
