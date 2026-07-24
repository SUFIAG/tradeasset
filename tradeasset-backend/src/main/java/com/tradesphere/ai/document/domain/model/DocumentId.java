package com.tradesphere.ai.document.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * Document ID value object
 * Strongly-typed identifier for Document aggregate
 */
@Getter
@EqualsAndHashCode
public class DocumentId {

    private final UUID value;

    private DocumentId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("Document ID cannot be null");
        }
        this.value = value;
    }

    public static DocumentId of(String id) {
        return new DocumentId(UUID.fromString(id));
    }

    public static DocumentId of(UUID id) {
        return new DocumentId(id);
    }

    public static DocumentId generate() {
        return new DocumentId(UUID.randomUUID());
    }

    public String asString() {
        return value.toString();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

