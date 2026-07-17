package com.tradesphere.ai.document.application.service.validation;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Result of document field validation
 * Contains errors, warnings, and success messages
 */
@Data
@NoArgsConstructor
public class ValidationResult {

    private boolean valid = true;
    private double score = 0.0;

    private List<ValidationMessage> errors = new ArrayList<>();
    private List<ValidationMessage> warnings = new ArrayList<>();
    private List<ValidationMessage> successes = new ArrayList<>();

    private Map<String, String> fieldStatus = new HashMap<>();

    public void addError(String field, String message) {
        errors.add(new ValidationMessage(field, message, MessageType.ERROR));
        fieldStatus.put(field, "ERROR");
        valid = false;
    }

    public void addWarning(String field, String message) {
        warnings.add(new ValidationMessage(field, message, MessageType.WARNING));
        if (!fieldStatus.containsKey(field)) {
            fieldStatus.put(field, "WARNING");
        }
    }

    public void addSuccess(String field, String message) {
        successes.add(new ValidationMessage(field, message, MessageType.SUCCESS));
        if (!fieldStatus.containsKey(field)) {
            fieldStatus.put(field, "SUCCESS");
        }
    }

    public void addInfo(String field, String message) {
        successes.add(new ValidationMessage(field, message, MessageType.INFO));
    }

    /**
     * Calculate overall validation score (0-100)
     * Based on ratio of successful validations to total validations
     */
    public void calculateScore() {
        int total = errors.size() + warnings.size() + successes.size();
        if (total == 0) {
            score = 0.0;
            return;
        }

        // Errors: -10 points each
        // Warnings: -3 points each
        // Successes: +5 points each
        double points = (successes.size() * 5.0) - (errors.size() * 10.0) - (warnings.size() * 3.0);
        double maxPoints = total * 5.0;

        score = Math.max(0, Math.min(100, (points / maxPoints) * 100));
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    public int getErrorCount() {
        return errors.size();
    }

    public int getWarningCount() {
        return warnings.size();
    }

    public int getSuccessCount() {
        return successes.size();
    }

    public String getSummary() {
        return String.format("Validation: %s (Score: %.1f%%) - Errors: %d, Warnings: %d, Successes: %d",
            valid ? "PASSED" : "FAILED",
            score,
            errors.size(),
            warnings.size(),
            successes.size()
        );
    }

    @Data
    @NoArgsConstructor
    public static class ValidationMessage {
        private String field;
        private String message;
        private MessageType type;
        private long timestamp;

        public ValidationMessage(String field, String message, MessageType type) {
            this.field = field;
            this.message = message;
            this.type = type;
            this.timestamp = System.currentTimeMillis();
        }
    }

    public enum MessageType {
        ERROR,
        WARNING,
        SUCCESS,
        INFO
    }
}

