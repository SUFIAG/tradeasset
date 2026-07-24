package com.tradesphere.ai.document.application.service.validation;

import com.tradesphere.ai.document.domain.model.DocumentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Document Field Validator
 */
@DisplayName("Document Field Validator Tests")
class DocumentFieldValidatorTest {

    private DocumentFieldValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DocumentFieldValidator();
    }

    @Nested
    @DisplayName("Commercial Invoice Validation Tests")
    class CommercialInvoiceValidationTests {

        @Test
        @DisplayName("Should pass validation for valid commercial invoice")
        void shouldPassValidationForValidInvoice() {
            // Given
            Map<String, Object> data = new HashMap<>();
            data.put("invoiceNumber", "INV-2024-001234");
            data.put("invoiceDate", "2024-07-15");
            data.put("sellerName", "ABC Trading Company");
            data.put("buyerName", "XYZ Imports Ltd");
            data.put("totalAmount", "50000.00");
            data.put("currency", "USD");
            data.put("paymentTerms", "LC at sight");
            data.put("incoterm", "FOB");

            // When
            ValidationResult result = validator.validateDocument(data, DocumentType.COMMERCIAL_INVOICE);

            // Then
            assertThat(result.isValid()).isTrue();
            assertThat(result.getScore()).isGreaterThan(80.0);
            assertThat(result.hasErrors()).isFalse();
        }

        @Test
        @DisplayName("Should fail validation when required fields are missing")
        void shouldFailWhenRequiredFieldsMissing() {
            // Given
            Map<String, Object> data = new HashMap<>();
            data.put("sellerName", "ABC Trading");
            // Missing invoiceNumber, invoiceDate, buyerName, totalAmount, currency

            // When
            ValidationResult result = validator.validateDocument(data, DocumentType.COMMERCIAL_INVOICE);

            // Then
            assertThat(result.isValid()).isFalse();
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getErrorCount()).isGreaterThan(0);
        }

        @Test
        @DisplayName("Should detect invalid invoice number format")
        void shouldDetectInvalidInvoiceNumberFormat() {
            // Given
            Map<String, Object> data = new HashMap<>();
            data.put("invoiceNumber", "123");  // Too short, doesn't match pattern
            data.put("invoiceDate", "2024-07-15");
            data.put("sellerName", "ABC Trading");
            data.put("buyerName", "XYZ Imports");
            data.put("totalAmount", "50000.00");
            data.put("currency", "USD");

            // When
            ValidationResult result = validator.validateDocument(data, DocumentType.COMMERCIAL_INVOICE);

            // Then
            assertThat(result.getWarningCount()).isGreaterThan(0);
        }

        @Test
        @DisplayName("Should detect invalid currency")
        void shouldDetectInvalidCurrency() {
            // Given
            Map<String, Object> data = new HashMap<>();
            data.put("invoiceNumber", "INV-2024-001");
            data.put("invoiceDate", "2024-07-15");
            data.put("sellerName", "ABC Trading");
            data.put("buyerName", "XYZ Imports");
            data.put("totalAmount", "50000.00");
            data.put("currency", "XXX");  // Invalid currency

            // When
            ValidationResult result = validator.validateDocument(data, DocumentType.COMMERCIAL_INVOICE);

            // Then
            assertThat(result.getWarningCount()).isGreaterThan(0);
        }

        @Test
        @DisplayName("Should detect invalid amount")
        void shouldDetectInvalidAmount() {
            // Given
            Map<String, Object> data = new HashMap<>();
            data.put("invoiceNumber", "INV-2024-001");
            data.put("invoiceDate", "2024-07-15");
            data.put("sellerName", "ABC Trading");
            data.put("buyerName", "XYZ Imports");
            data.put("totalAmount", "-5000.00");  // Negative amount
            data.put("currency", "USD");

            // When
            ValidationResult result = validator.validateDocument(data, DocumentType.COMMERCIAL_INVOICE);

            // Then
            assertThat(result.hasErrors()).isTrue();
        }

        @Test
        @DisplayName("Should validate incoterm")
        void shouldValidateIncoterm() {
            // Given - Valid Incoterm
            Map<String, Object> dataValid = new HashMap<>();
            dataValid.put("invoiceNumber", "INV-2024-001");
            dataValid.put("invoiceDate", "2024-07-15");
            dataValid.put("sellerName", "ABC");
            dataValid.put("buyerName", "XYZ");
            dataValid.put("totalAmount", "50000");
            dataValid.put("currency", "USD");
            dataValid.put("incoterm", "FOB");

            // When
            ValidationResult resultValid = validator.validateDocument(dataValid, DocumentType.COMMERCIAL_INVOICE);

            // Then
            assertThat(resultValid.getSuccessCount()).isGreaterThan(5);

            // Given - Invalid Incoterm
            Map<String, Object> dataInvalid = new HashMap<>();
            dataInvalid.put("invoiceNumber", "INV-2024-001");
            dataInvalid.put("invoiceDate", "2024-07-15");
            dataInvalid.put("sellerName", "ABC");
            dataInvalid.put("buyerName", "XYZ");
            dataInvalid.put("totalAmount", "50000");
            dataInvalid.put("currency", "USD");
            dataInvalid.put("incoterm", "INVALID");

            // When
            ValidationResult resultInvalid = validator.validateDocument(dataInvalid, DocumentType.COMMERCIAL_INVOICE);

            // Then
            assertThat(resultInvalid.getWarningCount()).isGreaterThan(0);
        }
    }

    @Nested
    @DisplayName("Bill of Lading Validation Tests")
    class BillOfLadingValidationTests {

        @Test
        @DisplayName("Should validate valid bill of lading")
        void shouldValidateValidBillOfLading() {
            // Given
            Map<String, Object> data = new HashMap<>();
            data.put("blNumber", "BL-2024-001234");
            data.put("vesselName", "MSC OSCAR");
            data.put("portOfLoading", "Shanghai");
            data.put("portOfDischarge", "Los Angeles");
            data.put("shipper", "ABC Exports");
            data.put("consignee", "XYZ Imports");
            data.put("dateOfShipment", "2024-07-01");

            // When
            ValidationResult result = validator.validateDocument(data, DocumentType.BILL_OF_LADING);

            // Then
            assertThat(result.isValid()).isTrue();
            assertThat(result.hasErrors()).isFalse();
        }

        @Test
        @DisplayName("Should fail when port names are too short")
        void shouldFailWhenPortNamesTooShort() {
            // Given
            Map<String, Object> data = new HashMap<>();
            data.put("blNumber", "BL-001");
            data.put("vesselName", "Ship");
            data.put("portOfLoading", "SH");  // Too short
            data.put("portOfDischarge", "LA");  // Too short
            data.put("shipper", "ABC");
            data.put("consignee", "XYZ");
            data.put("dateOfShipment", "2024-07-01");

            // When
            ValidationResult result = validator.validateDocument(data, DocumentType.BILL_OF_LADING);

            // Then
            assertThat(result.getWarningCount()).isGreaterThan(0);
        }
    }

    @Nested
    @DisplayName("Letter of Credit Validation Tests")
    class LetterOfCreditValidationTests {

        @Test
        @DisplayName("Should validate valid letter of credit")
        void shouldValidateValidLC() {
            // Given
            Map<String, Object> data = new HashMap<>();
            data.put("lcNumber", "LC-2024-00001234");
            data.put("issueDate", "2024-07-01");
            data.put("expiryDate", "2025-01-01");
            data.put("applicant", "ABC Trading Company");
            data.put("beneficiary", "XYZ Exports Ltd");
            data.put("amount", "1000000.00");
            data.put("currency", "USD");

            // When
            ValidationResult result = validator.validateDocument(data, DocumentType.LETTER_OF_CREDIT);

            // Then
            assertThat(result.isValid()).isTrue();
            assertThat(result.hasErrors()).isFalse();
        }

        @Test
        @DisplayName("Should detect expiry date before issue date")
        void shouldDetectExpiryBeforeIssue() {
            // Given
            Map<String, Object> data = new HashMap<>();
            data.put("lcNumber", "LC-2024-001");
            data.put("issueDate", "2024-07-01");
            data.put("expiryDate", "2024-06-01");  // Before issue date
            data.put("applicant", "ABC");
            data.put("beneficiary", "XYZ");
            data.put("amount", "100000");
            data.put("currency", "USD");

            // When
            ValidationResult result = validator.validateDocument(data, DocumentType.LETTER_OF_CREDIT);

            // Then
            assertThat(result.hasErrors()).isTrue();
        }
    }

    @Nested
    @DisplayName("Passport Validation Tests")
    class PassportValidationTests {

        @Test
        @DisplayName("Should validate valid passport")
        void shouldValidateValidPassport() {
            // Given
            Map<String, Object> data = new HashMap<>();
            data.put("passportNumber", "A12345678");
            data.put("fullName", "John Doe");
            data.put("nationality", "US");
            data.put("dateOfBirth", "1990-01-15");
            data.put("issueDate", "2020-01-01");
            data.put("expiryDate", "2030-01-01");

            // When
            ValidationResult result = validator.validateDocument(data, DocumentType.PASSPORT);

            // Then
            assertThat(result.isValid()).isTrue();
            assertThat(result.hasErrors()).isFalse();
        }

        @Test
        @DisplayName("Should validate passport number format")
        void shouldValidatePassportNumberFormat() {
            // Given - Valid format
            Map<String, Object> dataValid = new HashMap<>();
            dataValid.put("passportNumber", "AB1234567");
            dataValid.put("fullName", "John Doe");
            dataValid.put("nationality", "GB");
            dataValid.put("dateOfBirth", "1990-01-15");
            dataValid.put("issueDate", "2020-01-01");
            dataValid.put("expiryDate", "2030-01-01");

            // When
            ValidationResult result = validator.validateDocument(dataValid, DocumentType.PASSPORT);

            // Then
            assertThat(result.hasErrors()).isFalse();
        }
    }

    @Nested
    @DisplayName("Date Format Validation Tests")
    class DateFormatValidationTests {

        @Test
        @DisplayName("Should accept multiple date formats")
        void shouldAcceptMultipleDateFormats() {
            // Given
            String[] validDates = {
                "2024-07-15",
                "15/07/2024",
                "07/15/2024",
                "15-07-2024",
                "15.07.2024",
                "2024/07/15"
            };

            for (String date : validDates) {
                Map<String, Object> data = new HashMap<>();
                data.put("invoiceNumber", "INV-001");
                data.put("invoiceDate", date);
                data.put("sellerName", "ABC");
                data.put("buyerName", "XYZ");
                data.put("totalAmount", "1000");
                data.put("currency", "USD");

                // When
                ValidationResult result = validator.validateDocument(data, DocumentType.COMMERCIAL_INVOICE);

                // Then
                assertThat(result.getErrorCount())
                    .as("Date format %s should be valid", date)
                    .isEqualTo(0);
            }
        }

        @Test
        @DisplayName("Should reject invalid date formats")
        void shouldRejectInvalidDateFormats() {
            // Given
            Map<String, Object> data = new HashMap<>();
            data.put("invoiceNumber", "INV-001");
            data.put("invoiceDate", "invalid-date");
            data.put("sellerName", "ABC");
            data.put("buyerName", "XYZ");
            data.put("totalAmount", "1000");
            data.put("currency", "USD");

            // When
            ValidationResult result = validator.validateDocument(data, DocumentType.COMMERCIAL_INVOICE);

            // Then
            assertThat(result.hasErrors()).isTrue();
        }
    }

    @Nested
    @DisplayName("Validation Score Calculation Tests")
    class ValidationScoreCalculationTests {

        @Test
        @DisplayName("Should calculate validation score correctly")
        void shouldCalculateScoreCorrectly() {
            // Given - Perfect data
            Map<String, Object> perfectData = new HashMap<>();
            perfectData.put("invoiceNumber", "INV-2024-001");
            perfectData.put("invoiceDate", "2024-07-15");
            perfectData.put("sellerName", "ABC Trading");
            perfectData.put("buyerName", "XYZ Imports");
            perfectData.put("totalAmount", "50000.00");
            perfectData.put("currency", "USD");

            // When
            ValidationResult result = validator.validateDocument(perfectData, DocumentType.COMMERCIAL_INVOICE);

            // Then
            assertThat(result.getScore()).isGreaterThan(70.0);
        }

        @Test
        @DisplayName("Should have low score for data with many errors")
        void shouldHaveLowScoreForErrorData() {
            // Given - Bad data
            Map<String, Object> badData = new HashMap<>();
            badData.put("totalAmount", "-1000");  // Negative
            badData.put("currency", "INVALID");   // Invalid currency
            // Missing required fields

            // When
            ValidationResult result = validator.validateDocument(badData, DocumentType.COMMERCIAL_INVOICE);

            // Then
            assertThat(result.getScore()).isLessThan(30.0);
            assertThat(result.isValid()).isFalse();
        }
    }

    @Nested
    @DisplayName("Generic Document Validation Tests")
    class GenericDocumentValidationTests {

        @Test
        @DisplayName("Should handle unknown document type gracefully")
        void shouldHandleUnknownDocumentType() {
            // Given
            Map<String, Object> data = new HashMap<>();
            data.put("field1", "value1");
            data.put("field2", "value2");

            // When
            ValidationResult result = validator.validateDocument(data, DocumentType.OTHER);

            // Then
            assertThat(result).isNotNull();
            // Should not crash, should provide generic validation
        }

        @Test
        @DisplayName("Should handle empty data gracefully")
        void shouldHandleEmptyDataGracefully() {
            // Given
            Map<String, Object> emptyData = new HashMap<>();

            // When
            ValidationResult result = validator.validateDocument(emptyData, DocumentType.OTHER);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.isValid()).isFalse();
        }
    }
}

