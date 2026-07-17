package com.tradesphere.ai.document.application.service.validation;

import com.tradesphere.ai.document.domain.model.DocumentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Comprehensive field validation service for extracted document data
 * Validates business rules, data formats, and completeness
 */
@Service
@Slf4j
public class DocumentFieldValidator {

    // Regular expressions for common field patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{1,14}$");
    private static final Pattern IBAN_PATTERN = Pattern.compile("^[A-Z]{2}[0-9]{2}[A-Z0-9]{1,30}$");
    private static final Pattern SWIFT_CODE_PATTERN = Pattern.compile("^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$");
    private static final Pattern INVOICE_NUMBER_PATTERN = Pattern.compile("^[A-Z]{2,5}-\\d{4,10}(-\\d+)?$");
    private static final Pattern LC_NUMBER_PATTERN = Pattern.compile("^LC-\\d{4}-\\d{6,8}$");
    private static final Pattern PO_NUMBER_PATTERN = Pattern.compile("^PO-\\d{4,10}$");
    private static final Pattern CONTAINER_NUMBER_PATTERN = Pattern.compile("^[A-Z]{4}\\d{7}$");
    private static final Pattern HS_CODE_PATTERN = Pattern.compile("^\\d{4,10}$");
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("^[0-9]{1,3}(,[0-9]{3})*(\\.\\d{2})?$");
    private static final Pattern PASSPORT_PATTERN = Pattern.compile("^[A-Z]{1,2}\\d{6,9}$");
    private static final Pattern TAX_ID_PATTERN = Pattern.compile("^[A-Z0-9]{8,15}$");

    private static final Set<String> VALID_CURRENCIES = Set.of(
        "USD", "EUR", "GBP", "JPY", "CHF", "CAD", "AUD", "CNY", "INR", "SGD",
        "HKD", "AED", "SAR", "ZAR", "BRL", "MXN", "RUB", "KRW", "THB", "MYR"
    );

    private static final Set<String> VALID_COUNTRIES = Set.of(
        "US", "GB", "DE", "FR", "IT", "ES", "NL", "BE", "CH", "AT",
        "CN", "JP", "KR", "IN", "SG", "HK", "AE", "SA", "ZA", "BR",
        "MX", "CA", "AU", "NZ", "RU", "TR", "EG", "NG", "KE", "TH"
    );

    private static final Set<String> VALID_INCOTERMS = Set.of(
        "EXW", "FCA", "FAS", "FOB", "CFR", "CIF", "CPT", "CIP",
        "DAP", "DPU", "DDP"
    );

    private static final List<DateTimeFormatter> DATE_FORMATTERS = Arrays.asList(
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("MM/dd/yyyy"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),
        DateTimeFormatter.ofPattern("dd.MM.yyyy"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd")
    );

    /**
     * Validate extracted document fields based on document type
     */
    public ValidationResult validateDocument(Map<String, Object> extractedData, DocumentType documentType) {
        log.info("Validating document type: {}", documentType);

        ValidationResult result = new ValidationResult();

        switch (documentType) {
            case COMMERCIAL_INVOICE -> validateCommercialInvoice(extractedData, result);
            case BILL_OF_LADING -> validateBillOfLading(extractedData, result);
            case PACKING_LIST -> validatePackingList(extractedData, result);
            case CERTIFICATE_OF_ORIGIN -> validateCertificateOfOrigin(extractedData, result);
            case INSURANCE_CERTIFICATE -> validateInsuranceCertificate(extractedData, result);
            case LETTER_OF_CREDIT -> validateLetterOfCredit(extractedData, result);
            case PASSPORT -> validatePassport(extractedData, result);
            case NATIONAL_ID -> validateNationalId(extractedData, result);
            case BUSINESS_LICENSE -> validateBusinessLicense(extractedData, result);
            default -> validateGenericDocument(extractedData, result);
        }

        // Calculate overall validation score
        result.calculateScore();

        log.info("Validation completed with score: {}", result.getScore());
        return result;
    }

    private void validateCommercialInvoice(Map<String, Object> data, ValidationResult result) {
        // Required fields
        validateRequired(data, "invoiceNumber", result);
        validateRequired(data, "invoiceDate", result);
        validateRequired(data, "sellerName", result);
        validateRequired(data, "buyerName", result);
        validateRequired(data, "totalAmount", result);
        validateRequired(data, "currency", result);

        // Format validations
        validateInvoiceNumber(data.get("invoiceNumber"), result);
        validateDate(data.get("invoiceDate"), result, "invoiceDate");
        validateAmount(data.get("totalAmount"), result, "totalAmount");
        validateCurrency(data.get("currency"), result);

        // Business logic validations
        validatePositiveAmount(data.get("totalAmount"), result, "totalAmount");
        validateCompanyName(data.get("sellerName"), result, "sellerName");
        validateCompanyName(data.get("buyerName"), result, "buyerName");

        // Optional but recommended
        validateOptional(data, "paymentTerms", result);
        validateOptional(data, "incoterm", result);

        if (data.containsKey("incoterm")) {
            validateIncoterm(data.get("incoterm"), result);
        }

        // Line items validation
        if (data.containsKey("lineItems")) {
            validateLineItems(data.get("lineItems"), result);
        }
    }

    private void validateBillOfLading(Map<String, Object> data, ValidationResult result) {
        // Required fields
        validateRequired(data, "blNumber", result);
        validateRequired(data, "vesselName", result);
        validateRequired(data, "portOfLoading", result);
        validateRequired(data, "portOfDischarge", result);
        validateRequired(data, "shipper", result);
        validateRequired(data, "consignee", result);
        validateRequired(data, "dateOfShipment", result);

        // Format validations
        validateDocumentNumber(data.get("blNumber"), result, "blNumber");
        validateDate(data.get("dateOfShipment"), result, "dateOfShipment");
        validatePort(data.get("portOfLoading"), result, "portOfLoading");
        validatePort(data.get("portOfDischarge"), result, "portOfDischarge");

        // Container numbers
        if (data.containsKey("containerNumbers")) {
            validateContainerNumbers(data.get("containerNumbers"), result);
        }
    }

    private void validatePackingList(Map<String, Object> data, ValidationResult result) {
        validateRequired(data, "packingListNumber", result);
        validateRequired(data, "date", result);
        validateRequired(data, "totalPackages", result);
        validateRequired(data, "totalGrossWeight", result);

        validateDocumentNumber(data.get("packingListNumber"), result, "packingListNumber");
        validateDate(data.get("date"), result, "date");
        validatePositiveInteger(data.get("totalPackages"), result, "totalPackages");
        validatePositiveAmount(data.get("totalGrossWeight"), result, "totalGrossWeight");
    }

    private void validateCertificateOfOrigin(Map<String, Object> data, ValidationResult result) {
        validateRequired(data, "certificateNumber", result);
        validateRequired(data, "issueDate", result);
        validateRequired(data, "exporter", result);
        validateRequired(data, "consignee", result);
        validateRequired(data, "countryOfOrigin", result);

        validateDocumentNumber(data.get("certificateNumber"), result, "certificateNumber");
        validateDate(data.get("issueDate"), result, "issueDate");
        validateCountryCode(data.get("countryOfOrigin"), result);
    }

    private void validateInsuranceCertificate(Map<String, Object> data, ValidationResult result) {
        validateRequired(data, "policyNumber", result);
        validateRequired(data, "insurer", result);
        validateRequired(data, "insuredAmount", result);
        validateRequired(data, "currency", result);
        validateRequired(data, "effectiveDate", result);

        validateDocumentNumber(data.get("policyNumber"), result, "policyNumber");
        validateAmount(data.get("insuredAmount"), result, "insuredAmount");
        validateCurrency(data.get("currency"), result);
        validateDate(data.get("effectiveDate"), result, "effectiveDate");
    }

    private void validateLetterOfCredit(Map<String, Object> data, ValidationResult result) {
        validateRequired(data, "lcNumber", result);
        validateRequired(data, "issueDate", result);
        validateRequired(data, "expiryDate", result);
        validateRequired(data, "applicant", result);
        validateRequired(data, "beneficiary", result);
        validateRequired(data, "amount", result);
        validateRequired(data, "currency", result);

        validateLCNumber(data.get("lcNumber"), result);
        validateDate(data.get("issueDate"), result, "issueDate");
        validateDate(data.get("expiryDate"), result, "expiryDate");
        validateAmount(data.get("amount"), result, "amount");
        validateCurrency(data.get("currency"), result);

        // Date logic validation
        validateDateRange(data.get("issueDate"), data.get("expiryDate"), result);
    }

    private void validatePassport(Map<String, Object> data, ValidationResult result) {
        validateRequired(data, "passportNumber", result);
        validateRequired(data, "fullName", result);
        validateRequired(data, "nationality", result);
        validateRequired(data, "dateOfBirth", result);
        validateRequired(data, "issueDate", result);
        validateRequired(data, "expiryDate", result);

        validatePassportNumber(data.get("passportNumber"), result);
        validateCountryCode(data.get("nationality"), result);
        validateDate(data.get("dateOfBirth"), result, "dateOfBirth");
        validateDate(data.get("issueDate"), result, "issueDate");
        validateDate(data.get("expiryDate"), result, "expiryDate");
    }

    private void validateNationalId(Map<String, Object> data, ValidationResult result) {
        validateRequired(data, "idNumber", result);
        validateRequired(data, "fullName", result);
        validateRequired(data, "dateOfBirth", result);
        validateRequired(data, "nationality", result);

        validateDocumentNumber(data.get("idNumber"), result, "idNumber");
        validateDate(data.get("dateOfBirth"), result, "dateOfBirth");
        validateCountryCode(data.get("nationality"), result);
    }

    private void validateBusinessLicense(Map<String, Object> data, ValidationResult result) {
        validateRequired(data, "licenseNumber", result);
        validateRequired(data, "companyName", result);
        validateRequired(data, "issueDate", result);
        validateRequired(data, "issuingAuthority", result);

        validateDocumentNumber(data.get("licenseNumber"), result, "licenseNumber");
        validateDate(data.get("issueDate"), result, "issueDate");
        validateCompanyName(data.get("companyName"), result, "companyName");
    }

    private void validateGenericDocument(Map<String, Object> data, ValidationResult result) {
        // Generic validation for unknown document types
        if (data.isEmpty()) {
            result.addError("document", "No data extracted from document");
        } else {
            result.addInfo("document", "Generic document validation - " + data.size() + " fields extracted");
        }
    }

    // ===== Field-level validators =====

    private void validateRequired(Map<String, Object> data, String field, ValidationResult result) {
        Object value = data.get(field);
        if (value == null || value.toString().trim().isEmpty()) {
            result.addError(field, "Required field is missing or empty");
        } else {
            result.addSuccess(field, "Required field present");
        }
    }

    private void validateOptional(Map<String, Object> data, String field, ValidationResult result) {
        if (!data.containsKey(field)) {
            result.addWarning(field, "Optional field not found");
        }
    }

    private void validateInvoiceNumber(Object value, ValidationResult result) {
        if (value == null) return;

        String str = value.toString().trim();
        if (INVOICE_NUMBER_PATTERN.matcher(str).matches()) {
            result.addSuccess("invoiceNumber", "Valid invoice number format");
        } else {
            result.addWarning("invoiceNumber", "Invoice number format doesn't match standard pattern");
        }
    }

    private void validateLCNumber(Object value, ValidationResult result) {
        if (value == null) return;

        String str = value.toString().trim();
        if (LC_NUMBER_PATTERN.matcher(str).matches()) {
            result.addSuccess("lcNumber", "Valid LC number format");
        } else {
            result.addWarning("lcNumber", "LC number format doesn't match standard pattern");
        }
    }

    private void validateDocumentNumber(Object value, ValidationResult result, String fieldName) {
        if (value == null) return;

        String str = value.toString().trim();
        if (str.length() >= 5 && str.length() <= 50) {
            result.addSuccess(fieldName, "Valid document number");
        } else {
            result.addWarning(fieldName, "Document number length out of expected range");
        }
    }

    private void validateDate(Object value, ValidationResult result, String fieldName) {
        if (value == null) return;

        String str = value.toString().trim();
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                LocalDate.parse(str, formatter);
                result.addSuccess(fieldName, "Valid date format");
                return;
            } catch (DateTimeParseException ignored) {
            }
        }

        result.addError(fieldName, "Invalid date format");
    }

    private void validateAmount(Object value, ValidationResult result, String fieldName) {
        if (value == null) return;

        try {
            String str = value.toString().trim().replace(",", "");
            BigDecimal amount = new BigDecimal(str);
            if (amount.compareTo(BigDecimal.ZERO) >= 0) {
                result.addSuccess(fieldName, "Valid amount");
            } else {
                result.addError(fieldName, "Amount cannot be negative");
            }
        } catch (NumberFormatException e) {
            result.addError(fieldName, "Invalid amount format");
        }
    }

    private void validatePositiveAmount(Object value, ValidationResult result, String fieldName) {
        if (value == null) return;

        try {
            String str = value.toString().trim().replace(",", "");
            BigDecimal amount = new BigDecimal(str);
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                result.addSuccess(fieldName, "Valid positive amount");
            } else {
                result.addError(fieldName, "Amount must be greater than zero");
            }
        } catch (NumberFormatException e) {
            result.addError(fieldName, "Invalid amount format");
        }
    }

    private void validatePositiveInteger(Object value, ValidationResult result, String fieldName) {
        if (value == null) return;

        try {
            int intValue = Integer.parseInt(value.toString().trim());
            if (intValue > 0) {
                result.addSuccess(fieldName, "Valid positive integer");
            } else {
                result.addError(fieldName, "Value must be greater than zero");
            }
        } catch (NumberFormatException e) {
            result.addError(fieldName, "Invalid integer format");
        }
    }

    private void validateCurrency(Object value, ValidationResult result) {
        if (value == null) return;

        String currency = value.toString().trim().toUpperCase();
        if (VALID_CURRENCIES.contains(currency)) {
            result.addSuccess("currency", "Valid currency code");
        } else {
            result.addWarning("currency", "Unusual currency code: " + currency);
        }
    }

    private void validateCountryCode(Object value, ValidationResult result) {
        if (value == null) return;

        String country = value.toString().trim().toUpperCase();
        if (VALID_COUNTRIES.contains(country)) {
            result.addSuccess("countryCode", "Valid country code");
        } else {
            result.addWarning("countryCode", "Unrecognized country code: " + country);
        }
    }

    private void validateIncoterm(Object value, ValidationResult result) {
        if (value == null) return;

        String incoterm = value.toString().trim().toUpperCase();
        if (VALID_INCOTERMS.contains(incoterm)) {
            result.addSuccess("incoterm", "Valid Incoterm");
        } else {
            result.addWarning("incoterm", "Invalid Incoterm: " + incoterm);
        }
    }

    private void validateCompanyName(Object value, ValidationResult result, String fieldName) {
        if (value == null) return;

        String name = value.toString().trim();
        if (name.length() >= 3 && name.length() <= 255) {
            result.addSuccess(fieldName, "Valid company name");
        } else {
            result.addWarning(fieldName, "Company name length out of expected range");
        }
    }

    private void validatePort(Object value, ValidationResult result, String fieldName) {
        if (value == null) return;

        String port = value.toString().trim();
        if (port.length() >= 3) {
            result.addSuccess(fieldName, "Valid port name");
        } else {
            result.addWarning(fieldName, "Port name too short");
        }
    }

    private void validatePassportNumber(Object value, ValidationResult result) {
        if (value == null) return;

        String passport = value.toString().trim().toUpperCase();
        if (PASSPORT_PATTERN.matcher(passport).matches()) {
            result.addSuccess("passportNumber", "Valid passport number format");
        } else {
            result.addWarning("passportNumber", "Passport number format doesn't match standard pattern");
        }
    }

    private void validateContainerNumbers(Object value, ValidationResult result) {
        if (value == null) return;

        if (value instanceof List<?> list) {
            for (Object item : list) {
                String container = item.toString().trim().toUpperCase();
                if (!CONTAINER_NUMBER_PATTERN.matcher(container).matches()) {
                    result.addWarning("containerNumber", "Invalid container number format: " + container);
                }
            }
            result.addSuccess("containerNumbers", "Container numbers validated");
        }
    }

    private void validateLineItems(Object value, ValidationResult result) {
        if (value == null) return;

        if (value instanceof List<?> items) {
            if (items.isEmpty()) {
                result.addWarning("lineItems", "No line items found");
            } else {
                result.addSuccess("lineItems", items.size() + " line items found");
            }
        }
    }

    private void validateDateRange(Object startDate, Object endDate, ValidationResult result) {
        if (startDate == null || endDate == null) return;

        try {
            LocalDate start = parseDate(startDate.toString());
            LocalDate end = parseDate(endDate.toString());

            if (start != null && end != null) {
                if (end.isAfter(start)) {
                    result.addSuccess("dateRange", "Valid date range");
                } else {
                    result.addError("dateRange", "End date must be after start date");
                }
            }
        } catch (Exception e) {
            result.addWarning("dateRange", "Could not validate date range");
        }
    }

    private LocalDate parseDate(String dateStr) {
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(dateStr.trim(), formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        return null;
    }
}

