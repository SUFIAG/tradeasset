package com.tradesphere.ai.workflow.domain.model;

import com.tradesphere.ai.shared.domain.AggregateRoot;
import com.tradesphere.ai.workflow.domain.event.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Workflow Aggregate Root
 * Manages multi-level approval workflows with SLA tracking
 *
 * Domain Responsibilities:
 * - Workflow lifecycle management
 * - Task orchestration and sequencing
 * - Approval routing and escalation
 * - SLA monitoring and enforcement
 * - Audit trail maintenance
 *
 * Business Rules:
 * - Tasks execute in sequence order
 * - Each level must be approved before next
 * - SLA breaches trigger automatic escalation
 * - Rejection at any level fails entire workflow
 * - All approvals required for completion
 */
@Getter
@Setter(AccessLevel.PRIVATE)
public class Workflow extends AggregateRoot<WorkflowId> {

    // Workflow Definition
    private UUID tenantId;
    private WorkflowType type;
    private String workflowName;
    private String description;
    private TaskPriority priority;

    // Reference to business entity
    private String entityType;  // Customer, LC, Document, etc.
    private UUID entityId;

    // Status & Lifecycle
    private WorkflowStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private int currentLevel;
    private int totalLevels;

    // SLA Management
    private LocalDateTime dueDate;
    private boolean slaBreached;
    private int slaHours;

    // Initiator
    private UUID initiatedBy;
    private String initiatorName;

    // Tasks
    private List<WorkflowTask> tasks;

    // Completion tracking
    private int completedTasks;
    private int rejectedTasks;

    // Outcome
    private String finalOutcome; // APPROVED, REJECTED, EXPIRED
    private String finalComments;

    /**
     * Private constructor
     */
    private Workflow(WorkflowId id) {
        super(id);
        this.tasks = new ArrayList<>();
    }

    /**
     * Create new workflow
     */
    public static Workflow create(
            UUID tenantId,
            WorkflowType type,
            String entityType,
            UUID entityId,
            UUID initiatedBy,
            String initiatorName,
            int slaHours) {

        if (tenantId == null) throw WorkflowException.validationFailed("Tenant ID is required");
        if (type == null) throw WorkflowException.validationFailed("Workflow type is required");
        if (entityId == null) throw WorkflowException.validationFailed("Entity ID is required");
        if (initiatedBy == null) throw WorkflowException.validationFailed("Initiator is required");

        Workflow workflow = new Workflow(WorkflowId.generate());
        workflow.tenantId = tenantId;
        workflow.type = type;
        workflow.workflowName = type.name();
        workflow.entityType = entityType;
        workflow.entityId = entityId;
        workflow.status = WorkflowStatus.PENDING;
        workflow.priority = TaskPriority.NORMAL; // Default priority
        workflow.initiatedBy = initiatedBy;
        workflow.initiatorName = initiatorName;
        workflow.slaHours = slaHours;
        workflow.currentLevel = 0;
        workflow.completedTasks = 0;
        workflow.rejectedTasks = 0;
        workflow.slaBreached = false;

        // Register domain event
        workflow.registerEvent(new WorkflowCreatedEvent(
                workflow.getId().getValue(),
                workflow.getTenantId(),
                workflow.getType(),
                workflow.getEntityType(),
                workflow.getEntityId()
        ));

        return workflow;
    }

    /**
     * Add task to workflow
     */
    public void addTask(
            String taskName,
            String description,
            TaskPriority priority,
            int sequenceOrder,
            int taskSlaHours) {

        if (status != WorkflowStatus.PENDING) {
            throw WorkflowException.invalidStatus("Cannot add tasks to started workflow");
        }

        WorkflowTask task = WorkflowTask.create(taskName, description, priority, sequenceOrder, taskSlaHours);
        tasks.add(task);
        totalLevels = Math.max(totalLevels, sequenceOrder);
    }

    /**
     * Start workflow execution
     */
    public void start() {
        if (status != WorkflowStatus.PENDING) {
            throw WorkflowException.invalidStatus("Workflow already started");
        }

        if (tasks.isEmpty()) {
            throw WorkflowException.validationFailed("Cannot start workflow without tasks");
        }

        this.status = WorkflowStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
        this.dueDate = LocalDateTime.now().plusHours(slaHours);
        this.currentLevel = 1;

        // Assign first level tasks
        assignTasksForCurrentLevel();

        registerEvent(new WorkflowStartedEvent(
                getId().getValue(),
                tenantId,
                entityType,
                entityId
        ));
    }

    /**
     * Approve current task
     */
    public void approveTask(UUID taskId, UUID userId, String comments) {
        if (!status.isActive()) {
            throw WorkflowException.invalidStatus("Workflow is not active");
        }

        WorkflowTask task = findTask(taskId);
        task.complete(userId, "APPROVED", comments);
        completedTasks++;

        // Check if current level is complete
        if (isCurrentLevelComplete()) {
            moveToNextLevel();
        }

        registerEvent(new WorkflowTaskApprovedEvent(
                getId().getValue(),
                tenantId,
                taskId,
                userId,
                currentLevel
        ));
    }

    /**
     * Reject task (fails entire workflow)
     */
    public void rejectTask(UUID taskId, UUID userId, String reason) {
        if (!status.isActive()) {
            throw WorkflowException.invalidStatus("Workflow is not active");
        }

        WorkflowTask task = findTask(taskId);
        task.reject(userId, reason);
        rejectedTasks++;

        // Rejection fails the workflow
        this.status = WorkflowStatus.REJECTED;
        this.finalOutcome = "REJECTED";
        this.finalComments = reason;
        this.completedAt = LocalDateTime.now();

        registerEvent(new WorkflowRejectedEvent(
                getId().getValue(),
                tenantId,
                entityType,
                entityId,
                userId,
                reason
        ));
    }

    /**
     * Escalate task due to SLA breach
     */
    public void escalateTask(UUID taskId, UUID escalateTo) {
        WorkflowTask task = findTask(taskId);
        task.escalate(escalateTo);
        this.slaBreached = true;

        registerEvent(new WorkflowTaskEscalatedEvent(
                getId().getValue(),
                tenantId,
                taskId,
                escalateTo
        ));
    }

    /**
     * Assign task to user
     */
    public void assignTask(UUID taskId, UUID userId, String userName, UUID assignedBy) {
        WorkflowTask task = findTask(taskId);
        task.assign(userId, userName, assignedBy);
    }

    /**
     * Cancel workflow
     */
    public void cancel(String reason) {
        if (status.isTerminal()) {
            throw WorkflowException.invalidStatus("Cannot cancel completed workflow");
        }

        this.status = WorkflowStatus.CANCELLED;
        this.finalOutcome = "CANCELLED";
        this.finalComments = reason;
        this.completedAt = LocalDateTime.now();

        registerEvent(new WorkflowCancelledEvent(
                getId().getValue(),
                tenantId,
                reason
        ));
    }

    /**
     * Check if workflow is overdue
     */
    public boolean isOverdue() {
        return !status.isTerminal() && LocalDateTime.now().isAfter(dueDate);
    }

    /**
     * Get pending tasks
     */
    public List<WorkflowTask> getPendingTasks() {
        return tasks.stream()
                .filter(t -> !t.getStatus().isTerminal())
                .toList();
    }

    /**
     * Get completed tasks
     */
    public List<WorkflowTask> getCompletedTasks() {
        return tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
                .toList();
    }

    /**
     * Get completed tasks count
     */
    public int getCompletedTasksCount() {
        return completedTasks;
    }

    /**
     * Get rejected tasks count
     */
    public int getRejectedTasksCount() {
        return rejectedTasks;
    }

    /**
     * Get tasks for specific level
     */
    public List<WorkflowTask> getTasksForLevel(int level) {
        return tasks.stream()
                .filter(t -> t.getSequenceOrder() == level)
                .toList();
    }

    // Private helper methods

    private void assignTasksForCurrentLevel() {
        List<WorkflowTask> currentTasks = getTasksForLevel(currentLevel);
        // Tasks will be assigned when workflow is started
        // Assignment logic will be in the application service
    }

    private boolean isCurrentLevelComplete() {
        List<WorkflowTask> currentTasks = getTasksForLevel(currentLevel);
        return currentTasks.stream().allMatch(t -> t.getStatus() == TaskStatus.COMPLETED);
    }

    private void moveToNextLevel() {
        if (currentLevel >= totalLevels) {
            // All levels complete - workflow complete
            completeWorkflow();
        } else {
            currentLevel++;
            assignTasksForCurrentLevel();
        }
    }

    private void completeWorkflow() {
        this.status = WorkflowStatus.COMPLETED;
        this.finalOutcome = "APPROVED";
        this.completedAt = LocalDateTime.now();

        registerEvent(new WorkflowCompletedEvent(
                getId().getValue(),
                tenantId,
                entityType,
                entityId,
                finalOutcome
        ));
    }

    private WorkflowTask findTask(UUID taskId) {
        return tasks.stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> WorkflowException.notFound("Task not found: " + taskId));
    }
}


