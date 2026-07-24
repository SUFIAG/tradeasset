package com.tradesphere.ai.workflow.adapter.out.persistence;

import com.tradesphere.ai.workflow.domain.model.*;
import org.springframework.stereotype.Component;

/**
 * Mapper between workflow domain model and JPA entities
 */
@Component
public class WorkflowMapper {
    
    /**
     * Convert domain to JPA entity
     */
    public WorkflowJpaEntity toJpaEntity(Workflow workflow) {
        WorkflowJpaEntity entity = new WorkflowJpaEntity();
        entity.setId(workflow.getId().getValue());
        entity.setTenantId(workflow.getTenantId());
        entity.setType(workflow.getType());
        entity.setWorkflowNumber(workflow.getWorkflowNumber());
        entity.setDescription(workflow.getDescription());
        entity.setEntityType(workflow.getEntityType());
        entity.setEntityId(workflow.getEntityId());
        entity.setStatus(workflow.getStatus());
        entity.setStartedAt(workflow.getStartedAt());
        entity.setCompletedAt(workflow.getCompletedAt());
        entity.setCurrentLevel(workflow.getCurrentLevel());
        entity.setTotalLevels(workflow.getTotalLevels());
        entity.setDueDate(workflow.getDueDate());
        entity.setSlaBreached(workflow.isSlaBreached());
        entity.setSlaHours(workflow.getSlaHours());
        entity.setInitiatedBy(workflow.getInitiatedBy());
        entity.setInitiatorName(workflow.getInitiatorName());
        entity.setCompletedTasks(workflow.getCompletedTasksCount());
        entity.setRejectedTasks(workflow.getRejectedTasksCount());
        entity.setFinalOutcome(workflow.getFinalOutcome());
        entity.setFinalComments(workflow.getFinalComments());
        
        // Map tasks
        workflow.getTasks().forEach(task -> {
            WorkflowTaskJpaEntity taskEntity = toTaskJpaEntity(task);
            entity.addTask(taskEntity);
        });
        
        return entity;
    }
    
    /**
     * Convert JPA entity to domain
     */
    public Workflow toDomain(WorkflowJpaEntity entity) {
        // Use reflection or builder to reconstruct domain object
        // This is a simplified version - in production, you'd use a proper factory
        try {
            java.lang.reflect.Constructor<Workflow> constructor = 
                    Workflow.class.getDeclaredConstructor(WorkflowId.class);
            constructor.setAccessible(true);
            Workflow workflow = constructor.newInstance(WorkflowId.of(entity.getId()));
            
            // Set fields using reflection
            setField(workflow, "tenantId", entity.getTenantId());
            setField(workflow, "type", entity.getType());
            setField(workflow, "workflowNumber", entity.getWorkflowNumber());
            setField(workflow, "description", entity.getDescription());
            setField(workflow, "entityType", entity.getEntityType());
            setField(workflow, "entityId", entity.getEntityId());
            setField(workflow, "status", entity.getStatus());
            setField(workflow, "startedAt", entity.getStartedAt());
            setField(workflow, "completedAt", entity.getCompletedAt());
            setField(workflow, "currentLevel", entity.getCurrentLevel());
            setField(workflow, "totalLevels", entity.getTotalLevels());
            setField(workflow, "dueDate", entity.getDueDate());
            setField(workflow, "slaBreached", entity.isSlaBreached());
            setField(workflow, "slaHours", entity.getSlaHours());
            setField(workflow, "initiatedBy", entity.getInitiatedBy());
            setField(workflow, "initiatorName", entity.getInitiatorName());
            setField(workflow, "completedTasks", entity.getCompletedTasks());
            setField(workflow, "rejectedTasks", entity.getRejectedTasks());
            setField(workflow, "finalOutcome", entity.getFinalOutcome());
            setField(workflow, "finalComments", entity.getFinalComments());
            
            // Map tasks
            java.util.List<WorkflowTask> tasks = new java.util.ArrayList<>();
            entity.getTasks().forEach(taskEntity -> {
                tasks.add(toTaskDomain(taskEntity));
            });
            setField(workflow, "tasks", tasks);
            
            return workflow;
        } catch (Exception e) {
            throw new RuntimeException("Failed to map JPA entity to domain", e);
        }
    }
    
    private WorkflowTaskJpaEntity toTaskJpaEntity(WorkflowTask task) {
        WorkflowTaskJpaEntity entity = new WorkflowTaskJpaEntity();
        entity.setId(task.getId());
        entity.setTaskName(task.getTaskName());
        entity.setTaskDescription(task.getTaskDescription());
        entity.setStatus(task.getStatus());
        entity.setPriority(task.getPriority());
        entity.setAssignedTo(task.getAssignedTo());
        entity.setAssignedToName(task.getAssignedToName());
        entity.setAssignedBy(task.getAssignedBy());
        entity.setAssignedAt(task.getAssignedAt());
        entity.setStartedAt(task.getStartedAt());
        entity.setCompletedAt(task.getCompletedAt());
        entity.setOutcome(task.getOutcome());
        entity.setComments(task.getComments());
        entity.setDueDate(task.getDueDate());
        entity.setSlaBreached(task.isSlaBreached());
        entity.setEscalatedAt(task.getEscalatedAt());
        entity.setEscalatedTo(task.getEscalatedTo());
        entity.setSequenceOrder(task.getSequenceOrder());
        return entity;
    }
    
    private WorkflowTask toTaskDomain(WorkflowTaskJpaEntity entity) {
        try {
            java.lang.reflect.Constructor<WorkflowTask> constructor = 
                    WorkflowTask.class.getDeclaredConstructor(java.util.UUID.class);
            constructor.setAccessible(true);
            WorkflowTask task = constructor.newInstance(entity.getId());
            
            setField(task, "taskName", entity.getTaskName());
            setField(task, "taskDescription", entity.getTaskDescription());
            setField(task, "status", entity.getStatus());
            setField(task, "priority", entity.getPriority());
            setField(task, "assignedTo", entity.getAssignedTo());
            setField(task, "assignedToName", entity.getAssignedToName());
            setField(task, "assignedBy", entity.getAssignedBy());
            setField(task, "assignedAt", entity.getAssignedAt());
            setField(task, "startedAt", entity.getStartedAt());
            setField(task, "completedAt", entity.getCompletedAt());
            setField(task, "outcome", entity.getOutcome());
            setField(task, "comments", entity.getComments());
            setField(task, "dueDate", entity.getDueDate());
            setField(task, "slaBreached", entity.isSlaBreached());
            setField(task, "escalatedAt", entity.getEscalatedAt());
            setField(task, "escalatedTo", entity.getEscalatedTo());
            setField(task, "sequenceOrder", entity.getSequenceOrder());
            
            return task;
        } catch (Exception e) {
            throw new RuntimeException("Failed to map task JPA entity to domain", e);
        }
    }
    
    private void setField(Object object, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }
}


