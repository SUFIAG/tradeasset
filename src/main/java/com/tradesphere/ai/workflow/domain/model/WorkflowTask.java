package com.tradesphere.ai.workflow.domain.model;

import com.tradesphere.ai.shared.domain.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Workflow Task Entity
 * Represents individual approval/action tasks within a workflow
 *
 * Performance:
 * - Lightweight entity for fast queries
 * - Indexed fields for task lists
 * - Embedded in Workflow aggregate
 */
@Getter
@Setter(AccessLevel.PRIVATE)
public class WorkflowTask extends Entity<UUID> {

    private String taskName;
    private String taskDescription;
    private TaskStatus status;
    private TaskPriority priority;
    private String complexity; // LOW, MEDIUM, HIGH

    // Assignment
    private UUID assignedTo;
    private String assignedToName;
    private UUID assignedBy;
    private LocalDateTime assignedAt;

    // Execution
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String outcome; // APPROVED, REJECTED, etc.
    private String comments;

    // SLA Tracking
    private LocalDateTime dueDate;
    private boolean slaBreached;
    private LocalDateTime escalatedAt;
    private UUID escalatedTo;

    // Ordering
    private int sequenceOrder;

    /**
     * Private constructor
     */
    private WorkflowTask(UUID id) {
        super(id);
    }

    /**
     * Create new task
     */
    public static WorkflowTask create(
            String taskName,
            String description,
            TaskPriority priority,
            int sequenceOrder,
            int slaHours) {

        WorkflowTask task = new WorkflowTask(UUID.randomUUID());
        task.taskName = taskName;
        task.taskDescription = description;
        task.status = TaskStatus.CREATED;
        task.priority = priority;
        task.complexity = "MEDIUM"; // Default complexity
        task.sequenceOrder = sequenceOrder;
        task.dueDate = LocalDateTime.now().plusHours(slaHours);
        task.slaBreached = false;

        return task;
    }

    /**
     * Assign task to user
     */
    public void assign(UUID userId, String userName, UUID assignedBy) {
        if (status.isTerminal()) {
            throw WorkflowException.invalidStatus("Cannot assign completed task");
        }

        this.assignedTo = userId;
        this.assignedToName = userName;
        this.assignedBy = assignedBy;
        this.assignedAt = LocalDateTime.now();
        this.status = TaskStatus.ASSIGNED;
    }

    /**
     * Start working on task
     */
    public void start(UUID userId) {
        if (status != TaskStatus.ASSIGNED && status != TaskStatus.ESCALATED) {
            throw WorkflowException.invalidStatus("Task must be assigned before starting");
        }

        if (!userId.equals(assignedTo)) {
            throw WorkflowException.unauthorizedAction("Only assigned user can start task");
        }

        this.status = TaskStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
    }

    /**
     * Complete task with outcome
     */
    public void complete(UUID userId, String outcome, String comments) {
        if (status != TaskStatus.IN_PROGRESS && status != TaskStatus.ASSIGNED) {
            throw WorkflowException.invalidStatus("Task must be in progress to complete");
        }

        if (!userId.equals(assignedTo)) {
            throw WorkflowException.unauthorizedAction("Only assigned user can complete task");
        }

        this.status = TaskStatus.COMPLETED;
        this.outcome = outcome;
        this.comments = comments;
        this.completedAt = LocalDateTime.now();
    }

    /**
     * Reject task
     */
    public void reject(UUID userId, String reason) {
        if (!userId.equals(assignedTo)) {
            throw WorkflowException.unauthorizedAction("Only assigned user can reject task");
        }

        this.status = TaskStatus.REJECTED;
        this.outcome = "REJECTED";
        this.comments = reason;
        this.completedAt = LocalDateTime.now();
    }

    /**
     * Escalate task due to SLA breach
     */
    public void escalate(UUID escalateTo) {
        this.status = TaskStatus.ESCALATED;
        this.slaBreached = true;
        this.escalatedAt = LocalDateTime.now();
        this.escalatedTo = escalateTo;
    }

    /**
     * Check if task is overdue
     */
    public boolean isOverdue() {
        return !status.isTerminal() && LocalDateTime.now().isAfter(dueDate);
    }

    /**
     * Get time remaining in hours
     */
    public long getHoursRemaining() {
        if (status.isTerminal()) {
            return 0;
        }

        return java.time.Duration.between(LocalDateTime.now(), dueDate).toHours();
    }
}

