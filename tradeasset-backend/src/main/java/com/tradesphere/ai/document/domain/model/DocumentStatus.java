package com.tradesphere.ai.document.domain.model;

/**
 * Document status lifecycle
 */
public enum DocumentStatus {

    /**
     * Document uploaded but not yet processed
     */
    UPLOADED,

    /**
     * Virus scanning in progress
     */
    SCANNING,

    /**
     * Document passed virus scan
     */
    SCAN_PASSED,

    /**
     * Document failed virus scan
     */
    SCAN_FAILED,

    /**
     * OCR/AI extraction in progress
     */
    EXTRACTING,

    /**
     * Extraction completed successfully
     */
    EXTRACTED,

    /**
     * Extraction failed
     */
    EXTRACTION_FAILED,

    /**
     * Under human review
     */
    UNDER_REVIEW,

    /**
     * Document verified and accepted
     */
    VERIFIED,

    /**
     * Document rejected
     */
    REJECTED,

    /**
     * Document archived
     */
    ARCHIVED,

    /**
     * Document deleted (soft delete)
     */
    DELETED;

    public boolean isProcessing() {
        return this == SCANNING || this == EXTRACTING;
    }

    public boolean isTerminal() {
        return this == SCAN_FAILED || this == DELETED || this == ARCHIVED;
    }

    public boolean canExtract() {
        return this == SCAN_PASSED || this == EXTRACTION_FAILED;
    }

    public boolean canReview() {
        return this == EXTRACTED || this == UNDER_REVIEW;
    }
}

