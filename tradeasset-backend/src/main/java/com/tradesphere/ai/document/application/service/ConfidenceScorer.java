package com.tradesphere.ai.document.application.service;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

/**
 * Confidence Scorer for extracted document fields
 * Calculates confidence scores for extraction accuracy
 */
public class ConfidenceScorer {

    /**
     * Calculate overall confidence score for extraction
     */
    public static ConfidenceScore calculate(
            Map<String, Object> extractedFields,
            Double ocrConfidence,
            Double aiConfidence) {

        if (extractedFields == null || extractedFields.isEmpty()) {
            return ConfidenceScore.builder()
                    .overallScore(0.0)
                    .ocrScore(0.0)
                    .aiScore(0.0)
                    .fieldCompleteness(0.0)
                    .reliable(false)
                    .build();
        }

        // Calculate field completeness
        int totalExpectedFields = 10; // Varies by document type
        int extractedFieldCount = extractedFields.size();
        double fieldCompleteness = Math.min(1.0, (double) extractedFieldCount / totalExpectedFields);

        // Normalize confidence scores
        double normalizedOcr = ocrConfidence != null ? ocrConfidence : 0.5;
        double normalizedAi = aiConfidence != null ? aiConfidence : 0.5;

        // Calculate weighted overall score
        double overallScore = (normalizedOcr * 0.3) +
                             (normalizedAi * 0.4) +
                             (fieldCompleteness * 0.3);

        boolean reliable = overallScore >= 0.75;
        boolean requiresReview = overallScore < 0.75;

        return ConfidenceScore.builder()
                .overallScore(overallScore)
                .ocrScore(normalizedOcr)
                .aiScore(normalizedAi)
                .fieldCompleteness(fieldCompleteness)
                .reliable(reliable)
                .requiresReview(requiresReview)
                .build();
    }

    /**
     * Confidence score result
     */
    @Value
    @Builder
    public static class ConfidenceScore {
        Double overallScore;      // 0.0 - 1.0
        Double ocrScore;           // 0.0 - 1.0
        Double aiScore;            // 0.0 - 1.0
        Double fieldCompleteness;  // 0.0 - 1.0
        boolean reliable;          // >= 0.75
        boolean requiresReview;    // < 0.75

        /**
         * Get percentage string
         */
        public String getPercentage() {
            return String.format("%.1f%%", overallScore * 100);
        }

        /**
         * Get confidence level
         */
        public String getLevel() {
            if (overallScore >= 0.9) return "VERY_HIGH";
            if (overallScore >= 0.75) return "HIGH";
            if (overallScore >= 0.5) return "MEDIUM";
            if (overallScore >= 0.25) return "LOW";
            return "VERY_LOW";
        }
    }
}

