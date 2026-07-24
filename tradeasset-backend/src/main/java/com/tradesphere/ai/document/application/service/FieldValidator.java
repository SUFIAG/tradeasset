package com.tradesphere.ai.document.application.service;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

/**
 * Field Validator for extracted document fields
 * Validates extracted fields against business rules
 */
@Value
@Builder
public class FieldValidator {
    
    /**
     * Validate all extracted fields
     */
    public static ValidationResult validate(Map<String, Object> extractedFields, String documentType) {
        ValidationResult.ValidationResultBuilder result = ValidationResult.builder();
        result.valid(true);
        int errorCount = 0;
        
        if (extractedFields == null || extractedFields.isEmpty()) {
            result.valid(false);
            result.errorMessage("No fields extracted");
            return result.build();
        }
        
        // Validate based on document type
        switch (documentType.toUpperCase()) {
            case "COMMERCIAL_INVOICE" -> errorCount += validateInvoice(extractedFields, result);
            case "BILL_OF_LADING" -> errorCount += validateBillOfLading(extractedFields, result);
            case "PACKING_LIST" -> errorCount += validatePackingList(extractedFields, result);
            case "CERTIFICATE_OF_ORIGIN" -> errorCount += validateCertificate(extractedFields, result);
            default -> errorCount += validateGeneric(extractedFields, result);
        }
        
        if (errorCount > 0) {
            result.valid(false);
            result.errorMessage(errorCount + " validation errors found");
        }
        
        return result.build();
    }
    
    private static int validateInvoice(Map<String, Object> fields, ValidationResult.ValidationResultBuilder result) {
        int errors = 0;
        
        // Required fields for commercial invoice
        if (!fields.containsKey("invoiceNumber") || fields.get("invoiceNumber") == null) {
            result.addError("Invoice number is required");
            errors++;
        }
        
        if (!fields.containsKey("invoiceDate") || fields.get("invoiceDate") == null) {
            result.addError("Invoice date is required");
            errors++;
        }
        
        if (!fields.containsKey("totalAmount") || fields.get("totalAmount") == null) {
            result.addError("Total amount is required");
            errors++;
        }
        
        if (!fields.containsKey("seller") || fields.get("seller") == null) {
            result.addError("Seller information is required");
            errors++;
        }
        
        if (!fields.containsKey("buyer") || fields.get("buyer") == null) {
            result.addError("Buyer information is required");
            errors++;
        }
        
        // Validate amount format
        Object totalAmount = fields.get("totalAmount");
        if (totalAmount != null && !isValidAmount(totalAmount.toString())) {
            result.addError("Invalid amount format");
            errors++;
        }
        
        return errors;
    }
    
    private static int validateBillOfLading(Map<String, Object> fields, ValidationResult.ValidationResultBuilder result) {
        int errors = 0;
        
        if (!fields.containsKey("blNumber")) {
            result.addError("B/L number is required");
            errors++;
        }
        
        if (!fields.containsKey("shipper")) {
            result.addError("Shipper is required");
            errors++;
        }
        
        if (!fields.containsKey("consignee")) {
            result.addError("Consignee is required");
            errors++;
        }
        
        if (!fields.containsKey("vesselName")) {
            result.addError("Vessel name is required");
            errors++;
        }
        
        return errors;
    }
    
    private static int validatePackingList(Map<String, Object> fields, ValidationResult.ValidationResultBuilder result) {
        int errors = 0;
        
        if (!fields.containsKey("packingListNumber")) {
            result.addError("Packing list number is required");
            errors++;
        }
        
        if (!fields.containsKey("totalPackages")) {
            result.addError("Total packages is required");
            errors++;
        }
        
        if (!fields.containsKey("totalGrossWeight")) {
            result.addError("Total gross weight is required");
            errors++;
        }
        
        return errors;
    }
    
    private static int validateCertificate(Map<String, Object> fields, ValidationResult.ValidationResultBuilder result) {
        int errors = 0;
        
        if (!fields.containsKey("certificateNumber")) {
            result.addError("Certificate number is required");
            errors++;
        }
        
        if (!fields.containsKey("countryOfOrigin")) {
            result.addError("Country of origin is required");
            errors++;
        }
        
        return errors;
    }
    
    private static int validateGeneric(Map<String, Object> fields, ValidationResult.ValidationResultBuilder result) {
        // Basic validation for generic documents
        return 0;
    }
    
    private static boolean isValidAmount(String amount) {
        try {
            String cleanAmount = amount.replaceAll("[^0-9.]", "");
            Double.parseDouble(cleanAmount);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validation result
     */
    @Value
    @Builder
    public static class ValidationResult {
        boolean valid;
        String errorMessage;
        java.util.List<String> errors;
        
        public static class ValidationResultBuilder {
            private java.util.List<String> errors = new java.util.ArrayList<>();
            
            public ValidationResultBuilder addError(String error) {
                this.errors.add(error);
                return this;
            }
        }
    }
}

