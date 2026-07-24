package com.tradesphere.ai.workflow.adapter.in.rest.dto;

import com.tradesphere.ai.workflow.domain.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST DTO for workflow response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowResponse {
    
    private UUID id;
    private WorkflowType type;
    private String workflowNumber;
    private String entityType;
    private UUID entityId;
    private WorkflowStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private int currentLevel;
    private int totalLevels;
    private LocalDateTime dueDate;
    private boolean slaBreached;
    private String initiatorName;
    private List<TaskResponse> tasks;
    private int completedTasks;
    private int rejectedTasks;
    private String finalOutcome;
    private String finalComments;
    
    /**
     * Convert domain model to DTO
     */
    public static WorkflowResponse fromDomain(Workflow workflow) {
        return WorkflowResponse.builder()
                .id(workflow.getId().getValue())
                .type(workflow.getType())
                .workflowNumber(workflow.getWorkflowNumber())
                .entityType(workflow.getEntityType())
                .entityId(workflow.getEntityId())
                .status(workflow.getStatus())
                .startedAt(workflow.getStartedAt())
                .completedAt(workflow.getCompletedAt())
                .currentLevel(workflow.getCurrentLevel())
                .totalLevels(workflow.getTotalLevels())
                .dueDate(workflow.getDueDate())
                .slaBreached(workflow.isSlaBreached())
                .initiatorName(workflow.getInitiatorName())
                .tasks(workflow.getTasks().stream()
                        .map(TaskResponse::fromDomain)
                        .collect(Collectors.toList()))
                .completedTasks(workflow.getCompletedTasksCount())
                .rejectedTasks(workflow.getRejectedTasksCount())
                .finalOutcome(workflow.getFinalOutcome())
                .finalComments(workflow.getFinalComments())
                .build();
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskResponse {
        private UUID id;
        private String taskName;
        private String taskDescription;
        private TaskStatus status;
        private TaskPriority priority;
        private String assignedToName;
        private LocalDateTime assignedAt;
        private LocalDateTime completedAt;
        private String outcome;
        private String comments;
        private LocalDateTime dueDate;
        private boolean slaBreached;
        private int sequenceOrder;
        private long hoursRemaining;
        
        public static TaskResponse fromDomain(WorkflowTask task) {
            return TaskResponse.builder()
                    .id(task.getId())
                    .taskName(task.getTaskName())
                    .taskDescription(task.getTaskDescription())
                    .status(task.getStatus())
                    .priority(task.getPriority())
                    .assignedToName(task.getAssignedToName())
                    .assignedAt(task.getAssignedAt())
                    .completedAt(task.getCompletedAt())
                    .outcome(task.getOutcome())
                    .comments(task.getComments())
                    .dueDate(task.getDueDate())
                    .slaBreached(task.isSlaBreached())
                    .sequenceOrder(task.getSequenceOrder())
                    .hoursRemaining(task.getHoursRemaining())
                    .build();
        }
    }
}


