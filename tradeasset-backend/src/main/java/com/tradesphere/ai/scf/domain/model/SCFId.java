package com.tradesphere.ai.scf.domain.model;

import java.util.UUID;

public record SCFId(UUID value) {
    public static SCFId of(UUID value) {
        return new SCFId(value);
    }
    
    public static SCFId generate() {
        return new SCFId(UUID.randomUUID());
    }
    
    public UUID getValue() {
        return value;
    }
}

