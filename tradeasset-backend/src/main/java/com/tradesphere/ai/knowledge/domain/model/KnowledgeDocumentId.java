package com.tradesphere.ai.knowledge.domain.model;

import java.util.UUID;

public record KnowledgeDocumentId(UUID value) {
    public static KnowledgeDocumentId generate() {
        return new KnowledgeDocumentId(UUID.randomUUID());
    }
    
    public static KnowledgeDocumentId of(UUID value) {
        return new KnowledgeDocumentId(value);
    }
}

