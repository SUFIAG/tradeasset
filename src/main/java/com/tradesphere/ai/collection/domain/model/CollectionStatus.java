package com.tradesphere.ai.collection.domain.model;

public enum CollectionStatus {
    DRAFT,
    SUBMITTED,
    IN_TRANSIT,
    DOCUMENTS_RECEIVED,
    PRESENTED,
    ACCEPTED,
    PAID,
    REJECTED,
    CANCELLED;

    public boolean isTerminal() {
        return this == PAID || this == REJECTED || this == CANCELLED;
    }
}

