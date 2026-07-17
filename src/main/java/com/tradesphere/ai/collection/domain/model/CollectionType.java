package com.tradesphere.ai.collection.domain.model;

public enum CollectionType {
    DOCUMENTS_AGAINST_PAYMENT,   // D/P - Payment required before document release
    DOCUMENTS_AGAINST_ACCEPTANCE;  // D/A - Acceptance of draft before document release
    
    public String getShortCode() {
        return this == DOCUMENTS_AGAINST_PAYMENT ? "D/P" : "D/A";
    }
}

