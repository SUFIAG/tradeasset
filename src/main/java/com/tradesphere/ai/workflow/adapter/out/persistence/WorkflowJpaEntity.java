package com.tradesphere.ai.workflow.adapter.out.persistence;

import com.tradesphere.ai.workflow.domain.model.WorkflowStatus;
import com.tradesphere.ai.workflow.domain.model.WorkflowType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA entity for workflows
 */
@Entity
@Table(name = "workflows", indexes = {
        @Index(name = "idx_workflow_tenant", columnList = "tenant_id"),
        @Index(name = "idx_workflow_entity", columnList = "entity_type, entity_id"),
        @Index(name = "idx_workflow_status", columnList = "status, tenant_id"),
        @Index(name = "idx_workflow_due_date", columnList = "due_date, status")
})
@Data
public class WorkflowJpaEntity {
    
    @Id
    private UUID id;
    
    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private WorkflowType type;
    
    @Column(name = "workflow_name", nullable = false)
    private String workflowName;
    
    @Column(name = "description", length = 1000)
    private String description;
    
    @Column(name = "entity_type", nullable = false)
    private String entityType;
    
    @Column(name = "entity_id", nullable = false)
    private UUID entityId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WorkflowStatus status;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "current_level", nullable = false)
    private int currentLevel = 0;
    
    @Column(name = "total_levels", nullable = false)
    private int totalLevels = 0;
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @Column(name = "sla_breached", nullable = false)
    private boolean slaBreached = false;
    
    @Column(name = "sla_hours", nullable = false)
    private int slaHours;
    
    @Column(name = "initiated_by", nullable = false)
    private UUID initiatedBy;
    
    @Column(name = "initiator_name", nullable = false)
    private String initiatorName;
    
    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequenceOrder ASC")
    private List<WorkflowTaskJpaEntity> tasks = new ArrayList<>();
    
    @Column(name = "completed_tasks", nullable = false)
    private int completedTasks = 0;
    
    @Column(name = "rejected_tasks", nullable = false)
    private int rejectedTasks = 0;
    
    @Column(name = "final_outcome")
    private String finalOutcome;
    
    @Column(name = "final_comments", length = 2000)
    private String finalComments;
    
    @Version
    @Column(name = "version")
    private Long version;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "updated_by")
    private String updatedBy;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Helper method to add task
    public void addTask(WorkflowTaskJpaEntity task) {
        tasks.add(task);
        task.setWorkflow(this);
    }
}


