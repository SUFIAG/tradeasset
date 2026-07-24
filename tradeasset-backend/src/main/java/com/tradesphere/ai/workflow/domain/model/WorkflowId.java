package com.tradesphere.ai.workflow.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * Workflow ID value object
 * Strongly-typed identifier for Workflow aggregate
 */
@Getter
@EqualsAndHashCode
public class WorkflowId {

    private final UUID value;

    private WorkflowId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("Workflow ID cannot be null");
        }
        this.value = value;
    }

    public static WorkflowId of(String id) {
        return new WorkflowId(UUID.fromString(id));
    }

    public static WorkflowId of(UUID id) {
        return new WorkflowId(id);
    }

    public static WorkflowId generate() {
        return new WorkflowId(UUID.randomUUID());
    }

    public String asString() {
        return value.toString();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

