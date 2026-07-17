package com.tradesphere.ai.document.domain.model;

/**
 * Document type enumeration
 * Business-aligned document categories
 */
public enum DocumentType {
    
    // Trade Finance Documents
    COMMERCIAL_INVOICE("Commercial Invoice", "INV", true, true),
    BILL_OF_LADING("Bill of Lading", "BL", true, true),
    PACKING_LIST("Packing List", "PL", true, false),
    CERTIFICATE_OF_ORIGIN("Certificate of Origin", "COO", true, false),
    INSURANCE_CERTIFICATE("Insurance Certificate", "INS", true, false),
    INSPECTION_CERTIFICATE("Inspection Certificate", "IC", false, false),
    
    // KYC Documents
    PASSPORT("Passport", "PASS", true, false),
    NATIONAL_ID("National ID", "NID", true, false),
    BUSINESS_LICENSE("Business License", "BL", true, false),
    INCORPORATION_CERTIFICATE("Incorporation Certificate", "IC", true, false),
    TAX_CERTIFICATE("Tax Certificate", "TC", true, false),
    BANK_STATEMENT("Bank Statement", "BS", false, false),
    FINANCIAL_STATEMENT("Financial Statement", "FS", false, false),
    
    // Supporting Documents
    PROFORMA_INVOICE("Proforma Invoice", "PI", false, false),
    PURCHASE_ORDER("Purchase Order", "PO", false, false),
    LETTER_OF_CREDIT("Letter of Credit", "LC", true, true),
    AMENDMENT("LC Amendment", "AMD", true, true),
    
    // Internal Documents
    CREDIT_MEMO("Credit Memo", "CM", false, false),
    APPROVAL_FORM("Approval Form", "AF", false, false),
    CORRESPONDENCE("Correspondence", "CORR", false, false),
    
    // AI Generated
    EXTRACTED_DATA("Extracted Data", "EXT", false, false),
    COMPLIANCE_REPORT("Compliance Report", "CR", false, false),
    RISK_REPORT("Risk Report", "RR", false, false),
    
    OTHER("Other", "OTH", false, false);
    
    private final String displayName;
    private final String code;
    private final boolean requiresOCR;
    private final boolean criticalForLC;
    
    DocumentType(String displayName, String code, boolean requiresOCR, boolean criticalForLC) {
        this.displayName = displayName;
        this.code = code;
        this.requiresOCR = requiresOCR;
        this.criticalForLC = criticalForLC;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getCode() {
        return code;
    }
    
    public boolean requiresOCR() {
        return requiresOCR;
    }
    
    public boolean isCriticalForLC() {
        return criticalForLC;
    }
}

