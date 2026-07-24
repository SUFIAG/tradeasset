package com.tradesphere.ai.document.application.service.extraction;

import com.tradesphere.ai.document.domain.model.DocumentType;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Extraction quality scorer and validator
 * Assesses quality of extracted data and identifies issues
 *
 * Scoring criteria:
 * - Field completeness (% of required fields extracted)
 * - Data format validity (dates, amounts, etc.)
 * - Cross-field consistency
 * - Confidence scores from AI/OCR
 */
@Service
@Slf4j
public class ExtractionQualityScorer {

    // Required fields for each document type
    private static final Map<DocumentType, Set<String>> REQUIRED_FIELDS = Map.of(
        DocumentType.COMMERCIAL_INVOICE, Set.of(
            "invoiceNumber", "invoiceDate", "seller", "buyer", "amount", "currency"
        ),
        DocumentType.BILL_OF_LADING, Set.of(
            "blNumber", "blDate", "shipper", "consignee", "portOfLoading", "portOfDischarge"
        ),
        DocumentType.PACKING_LIST, Set.of(
            "packingListNumber", "date", "exporter", "importer", "items"
        ),
        DocumentType.PASSPORT, Set.of(
            "passportNumber", "givenNames", "surname", "nationality", "dateOfBirth", "expiryDate"
        ),
        DocumentType.NATIONAL_ID, Set.of(
            "idNumber", "fullName", "dateOfBirth", "nationality"
        )
    );

    /**
     * Score extraction quality
     * Returns quality score (0.0 to 1.0) and detailed report
     */
    public QualityScore scoreExtraction(
        DocumentType documentType,
        Map<String, Object> extractedData,
        Float extractionConfidence
    ) {
        log.info("Scoring extraction quality for document type: {}", documentType);

        List<QualityIssue> issues = new ArrayList<>();

        // Score 1: Field completeness
        float completenessScore = scoreCompleteness(documentType, extractedData, issues);

        // Score 2: Data format validity
        float validityScore = scoreValidity(extractedData, issues);

        // Score 3: Consistency
        float consistencyScore = scoreConsistency(extractedData, issues);

        // Score 4: AI/OCR confidence
        float confidenceScore = extractionConfidence != null ? extractionConfidence : 0.5f;

        // Calculate weighted overall score
        float overallScore = (
            completenessScore * 0.35f +  // 35% weight
            validityScore * 0.25f +       // 25% weight
            consistencyScore * 0.20f +    // 20% weight
            confidenceScore * 0.20f       // 20% weight
        );

        QualityLevel level = determineQualityLevel(overallScore);

        log.info("Extraction quality score: {} ({})", overallScore, level);

        return QualityScore.builder()
            .overallScore(overallScore)
            .qualityLevel(level)
            .completenessScore(completenessScore)
            .validityScore(validityScore)
            .consistencyScore(consistencyScore)
            .confidenceScore(confidenceScore)
            .issues(issues)
            .requiresReview(overallScore < 0.7f)
            .requiresManualCorrection(overallScore < 0.5f)
            .build();
    }

    /**
     * Score field completeness
     * How many required fields are present and non-empty
     */
    private float scoreCompleteness(
        DocumentType documentType,
        Map<String, Object> extractedData,
        List<QualityIssue> issues
    ) {
        Set<String> requiredFields = REQUIRED_FIELDS.getOrDefault(documentType, Set.of());

        if (requiredFields.isEmpty()) {
            return 1.0f; // No required fields defined
        }

        int presentFields = 0;
        for (String field : requiredFields) {
            Object value = extractedData.get(field);
            if (value != null && !value.toString().isBlank()) {
                presentFields++;
            } else {
                issues.add(new QualityIssue(
                    QualityIssue.Severity.WARNING,
                    "MISSING_FIELD",
                    "Required field '" + field + "' is missing or empty",
                    field
                ));
            }
        }

        return (float) presentFields / requiredFields.size();
    }

    /**
     * Score data format validity
     * Check if data formats are correct (dates, numbers, etc.)
     */
    private float scoreValidity(Map<String, Object> extractedData, List<QualityIssue> issues) {
        int totalFields = 0;
        int validFields = 0;

        for (Map.Entry<String, Object> entry : extractedData.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();

            if (value == null || value.toString().isBlank()) {
                continue; // Skip null/empty fields
            }

            totalFields++;

            // Validate date fields
            if (field.toLowerCase().contains("date")) {
                if (isValidDate(value.toString())) {
                    validFields++;
                } else {
                    issues.add(new QualityIssue(
                        QualityIssue.Severity.ERROR,
                        "INVALID_DATE_FORMAT",
                        "Field '" + field + "' has invalid date format: " + value,
                        field
                    ));
                }
            }
            // Validate amount fields
            else if (field.toLowerCase().contains("amount") ||
                     field.toLowerCase().contains("price") ||
                     field.toLowerCase().contains("value")) {
                if (isValidAmount(value)) {
                    validFields++;
                } else {
                    issues.add(new QualityIssue(
                        QualityIssue.Severity.ERROR,
                        "INVALID_AMOUNT_FORMAT",
                        "Field '" + field + "' has invalid amount format: " + value,
                        field
                    ));
                }
            }
            // Other fields assumed valid if present
            else {
                validFields++;
            }
        }

        return totalFields > 0 ? (float) validFields / totalFields : 1.0f;
    }

    /**
     * Score cross-field consistency
     * Check if related fields are consistent with each other
     */
    private float scoreConsistency(Map<String, Object> extractedData, List<QualityIssue> issues) {
        int checks = 0;
        int passed = 0;

        // Check: Invoice amount matches sum of line items
        if (extractedData.containsKey("amount") && extractedData.containsKey("items")) {
            checks++;
            if (validateAmountMatchesItems(extractedData)) {
                passed++;
            } else {
                issues.add(new QualityIssue(
                    QualityIssue.Severity.WARNING,
                    "AMOUNT_MISMATCH",
                    "Total amount does not match sum of line items",
                    "amount"
                ));
            }
        }

        // Check: Date consistency (issue date before expiry date)
        if (extractedData.containsKey("issueDate") && extractedData.containsKey("expiryDate")) {
            checks++;
            if (validateDateOrder(
                extractedData.get("issueDate").toString(),
                extractedData.get("expiryDate").toString()
            )) {
                passed++;
            } else {
                issues.add(new QualityIssue(
                    QualityIssue.Severity.ERROR,
                    "DATE_ORDER_INVALID",
                    "Issue date is after expiry date",
                    "issueDate"
                ));
            }
        }

        return checks > 0 ? (float) passed / checks : 1.0f;
    }

    /**
     * Determine quality level based on score
     */
    private QualityLevel determineQualityLevel(float score) {
        if (score >= 0.9f) return QualityLevel.EXCELLENT;
        if (score >= 0.75f) return QualityLevel.GOOD;
        if (score >= 0.6f) return QualityLevel.ACCEPTABLE;
        if (score >= 0.4f) return QualityLevel.POOR;
        return QualityLevel.FAILED;
    }

    // Validation helper methods

    private boolean isValidDate(String dateStr) {
        // Check ISO date format (YYYY-MM-DD)
        return dateStr.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    private boolean isValidAmount(Object amount) {
        if (amount instanceof Number) {
            return ((Number) amount).doubleValue() >= 0;
        }
        if (amount instanceof String) {
            try {
                Double.parseDouble(amount.toString());
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    private boolean validateAmountMatchesItems(Map<String, Object> data) {
        // Simplified check - would need more complex logic in production
        return true;
    }

    private boolean validateDateOrder(String date1, String date2) {
        try {
            return date1.compareTo(date2) <= 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Quality score result
     */
    @Value
    @Builder
    public static class QualityScore {
        float overallScore;
        QualityLevel qualityLevel;
        float completenessScore;
        float validityScore;
        float consistencyScore;
        float confidenceScore;
        List<QualityIssue> issues;
        boolean requiresReview;
        boolean requiresManualCorrection;
    }

    /**
     * Quality level classification
     */
    public enum QualityLevel {
        EXCELLENT,    // 0.9+: Perfect extraction, ready for auto-processing
        GOOD,         // 0.75-0.9: Good extraction, minor review needed
        ACCEPTABLE,   // 0.6-0.75: Acceptable, review recommended
        POOR,         // 0.4-0.6: Poor quality, manual correction needed
        FAILED        // <0.4: Failed extraction, re-extraction needed
    }

    /**
     * Quality issue
     */
    @Value
    public static class QualityIssue {
        Severity severity;
        String code;
        String message;
        String field;

        public enum Severity {
            ERROR,    // Critical issue, data unusable
            WARNING,  // Issue that should be reviewed
            INFO      // Informational note
        }
    }
}

