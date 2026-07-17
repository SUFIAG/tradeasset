package com.tradesphere.ai.copilot.domain.model;

/**
 * Types of intents that can be recognized
 */
public enum IntentType {
    QUERY,      // Information/question
    CREATE,     // Create new entity
    UPDATE,     // Update existing entity
    DELETE,     // Delete entity
    SEARCH,     // Search/find
    HELP,       // Help request
    GREETING,   // Greeting/hello
    UNKNOWN     // Unknown intent
}

