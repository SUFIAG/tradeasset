package com.tradesphere.ai.workflow.adapter.out.persistence;

import com.tradesphere.ai.workflow.domain.model.TaskPriority;
import com.tradesphere.ai.workflow.domain.model.TaskStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity for workflow tasks
 */
@Entity
@Table(name = "workflow_tasks")
@Data
public class WorkflowTaskJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id")
    private WorkflowJpaEntity workflow;

    @Column(name = "task_name", nullable = false)
    private String taskName;

    @Column(name = "task_description", length = 1000)
    private String taskDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private TaskPriority priority;

    @Column(name = "assigned_to")
    private UUID assignedTo;

    @Column(name = "assigned_to_name")
    private String assignedToName;

    @Column(name = "assigned_by")
    private UUID assignedBy;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "outcome")
    private String outcome;

    @Column(name = "comments", length = 2000)
    private String comments;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "sla_breached", nullable = false)
    private boolean slaBreached = false;

    @Column(name = "escalated_at")
    private LocalDateTime escalatedAt;

    @Column(name = "escalated_to")
    private UUID escalatedTo;

    @Column(name = "sequence_order", nullable = false)
    private int sequenceOrder;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}


