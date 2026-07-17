package com.tradesphere.ai.shared.ai;

/**
 * AI Intent Types for Agent Routing
 * Defines different types of AI operations
 */
public enum AIIntent {
    // Document Operations
    EXTRACT_DOCUMENT_FIELDS,
    DETECT_DISCREPANCIES,
    VALIDATE_DOCUMENT,
    CLASSIFY_DOCUMENT,

    // Compliance Operations
    SCREEN_COMPLIANCE,
    CHECK_SANCTIONS,
    VERIFY_AML,
    DETECT_FRAUD,

    // Risk Operations
    ASSESS_RISK,
    CALCULATE_RISK_SCORE,
    PREDICT_DEFAULT,
    ANALYZE_COUNTRY_RISK,

    // Workflow Operations
    RECOMMEND_WORKFLOW,
    PREDICT_NEXT_ACTION,
    AUTO_ASSIGN,
    PRIORITIZE_TASKS,

    // Knowledge Operations
    QUERY_KNOWLEDGE,
    SEARCH_REGULATIONS,
    FIND_SIMILAR_CASES,
    GENERATE_REPORT,

    // General Operations
    GENERAL_QUERY,
    EXPLAIN_DECISION,
    TRANSLATE_DOCUMENT,
    SUMMARIZE_TEXT
}

