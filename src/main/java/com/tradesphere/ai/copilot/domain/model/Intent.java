package com.tradesphere.ai.copilot.domain.model;

/**
 * Intent Enum - User's intent in the conversation
 */
public enum Intent {
    QUERY,              // Informational query
    CREATE,             // Create new entity (LC, customer, etc.)
    UPDATE,             // Update existing entity
    SEARCH,             // Search/find
    DELETE,             // Delete entity
    APPROVE,            // Approve workflow
    REJECT,             // Reject workflow
    HELP,               // Help request
    GREETING,           // Greeting/small talk
    FAREWELL,           // Goodbye
    NAVIGATION,         // Navigate to section
    REPORT,             // Generate report
    EXPORT,             // Export data
    SYSTEM,             // System message
    ERROR,              // Error occurred
    UNKNOWN             // Intent not recognized
}

