package com.tradesphere.ai.collection.domain.model;

import java.util.UUID;

public record CollectionId(UUID value) {
    public static CollectionId of(UUID value) {
        return new CollectionId(value);
    }
    
    public static CollectionId generate() {
        return new CollectionId(UUID.randomUUID());
    }
    
    public UUID getValue() {
        return value;
    }
}

