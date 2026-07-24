package com.tradesphere.ai.document.application.service.extraction;

import com.tradesphere.ai.document.domain.model.DocumentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Factory for creating AI extraction prompts
 * Specialized prompts for each document type
 *
 * Prompt Engineering Best Practices:
 * - Clear instructions
 * - JSON output format
 * - Field descriptions
 * - Example outputs
 * - Error handling instructions
 */
@Component
@Slf4j
public class ExtractionPromptFactory {

    /**
     * Create extraction prompt for specific document type
     * High-quality prompts for accurate extraction
     */
    public String createExtractionPrompt(DocumentType documentType, String documentText) {
        String basePrompt = getBasePrompt();
        String typeSpecificPrompt = getTypeSpecificPrompt(documentType);

        return String.format("""
            %s
            
            %s
            
            DOCUMENT TEXT TO EXTRACT:
            ```
            %s
            ```
            
            Extract the information and respond ONLY with valid JSON. No additional text.
            """, basePrompt, typeSpecificPrompt, documentText);
    }

    /**
     * Base prompt with general instructions
     */
    private String getBasePrompt() {
        return """
            You are an expert document data extraction AI for trade finance operations.
            Your task is to extract structured data from documents with high accuracy.
            
            INSTRUCTIONS:
            1. Extract all relevant fields from the document
            2. Return ONLY valid JSON format
            3. Use null for missing fields
            4. Preserve original values (don't translate or modify)
            5. Extract dates in ISO 8601 format (YYYY-MM-DD)
            6. Extract amounts as numbers (without currency symbols)
            7. Be precise - extract exactly what's written
            """;
    }

    /**
     * Get document type-specific extraction prompt
     */
    private String getTypeSpecificPrompt(DocumentType documentType) {
        return switch (documentType) {
            case COMMERCIAL_INVOICE -> getCommercialInvoicePrompt();
            case BILL_OF_LADING -> getBillOfLadingPrompt();
            case PACKING_LIST -> getPackingListPrompt();
            case CERTIFICATE_OF_ORIGIN -> getCertificateOfOriginPrompt();
            case INSURANCE_CERTIFICATE -> getInsuranceCertificatePrompt();
            case PASSPORT -> getPassportPrompt();
            case NATIONAL_ID -> getNationalIDPrompt();
            case BUSINESS_LICENSE -> getBusinessLicensePrompt();
            default -> getGenericPrompt();
        };
    }

    private String getCommercialInvoicePrompt() {
        return """
            EXTRACT COMMERCIAL INVOICE DATA:
            
            Required fields:
            {
              "invoiceNumber": "Invoice number/reference",
              "invoiceDate": "Date of invoice (YYYY-MM-DD)",
              "seller": "Seller/Exporter name",
              "sellerAddress": "Seller address",
              "buyer": "Buyer/Importer name",
              "buyerAddress": "Buyer address",
              "amount": "Total invoice amount (number only)",
              "currency": "Currency code (USD, EUR, etc.)",
              "items": [
                {
                  "description": "Item description",
                  "quantity": "Quantity",
                  "unitPrice": "Unit price",
                  "total": "Line total"
                }
              ],
              "paymentTerms": "Payment terms",
              "incoterm": "Incoterm (FOB, CIF, etc.)",
              "portOfLoading": "Port of loading",
              "portOfDischarge": "Port of discharge",
              "lcNumber": "LC number if mentioned",
              "bankDetails": "Beneficiary bank details"
            }
            """;
    }

    private String getBillOfLadingPrompt() {
        return """
            EXTRACT BILL OF LADING DATA:
            
            Required fields:
            {
              "blNumber": "B/L number",
              "blDate": "B/L date (YYYY-MM-DD)",
              "blType": "Type (Master B/L, House B/L, etc.)",
              "shipper": "Shipper name",
              "shipperAddress": "Shipper address",
              "consignee": "Consignee name",
              "consigneeAddress": "Consignee address",
              "notifyParty": "Notify party",
              "vessel": "Vessel name",
              "voyage": "Voyage number",
              "portOfLoading": "Port of loading",
              "portOfDischarge": "Port of discharge",
              "placeOfReceipt": "Place of receipt",
              "placeOfDelivery": "Place of delivery",
              "containerNumbers": ["Container numbers"],
              "sealNumbers": ["Seal numbers"],
              "cargoDescription": "Description of goods",
              "numberOfPackages": "Number of packages",
              "grossWeight": "Gross weight",
              "measurement": "Measurement/volume",
              "freight": "Freight terms (Prepaid/Collect)",
              "shippedOnBoard": "Shipped on board date (YYYY-MM-DD)"
            }
            """;
    }

    private String getPackingListPrompt() {
        return """
            EXTRACT PACKING LIST DATA:
            
            Required fields:
            {
              "packingListNumber": "Packing list number",
              "date": "Date (YYYY-MM-DD)",
              "exporter": "Exporter name",
              "importer": "Importer name",
              "invoiceReference": "Related invoice number",
              "items": [
                {
                  "itemNumber": "Item/product number",
                  "description": "Description",
                  "quantity": "Quantity",
                  "unit": "Unit of measure",
                  "netWeight": "Net weight",
                  "grossWeight": "Gross weight",
                  "cartonNumber": "Carton/package number"
                }
              ],
              "totalPackages": "Total number of packages",
              "totalNetWeight": "Total net weight",
              "totalGrossWeight": "Total gross weight",
              "containerNumbers": ["Container numbers"]
            }
            """;
    }

    private String getCertificateOfOriginPrompt() {
        return """
            EXTRACT CERTIFICATE OF ORIGIN DATA:
            
            Required fields:
            {
              "certificateNumber": "Certificate number",
              "date": "Issue date (YYYY-MM-DD)",
              "origin": "Country of origin",
              "exporter": "Exporter name and address",
              "importer": "Importer name and address",
              "goods": "Description of goods",
              "hsCode": "HS code/Tariff number",
              "invoiceNumber": "Related invoice number",
              "invoiceDate": "Invoice date (YYYY-MM-DD)",
              "quantity": "Quantity",
              "issuingAuthority": "Issuing authority/chamber",
              "signatory": "Signatory name",
              "signatureDate": "Signature date (YYYY-MM-DD)"
            }
            """;
    }

    private String getInsuranceCertificatePrompt() {
        return """
            EXTRACT INSURANCE CERTIFICATE DATA:
            
            Required fields:
            {
              "certificateNumber": "Certificate/Policy number",
              "date": "Issue date (YYYY-MM-DD)",
              "insuredParty": "Insured party name",
              "beneficiary": "Beneficiary name",
              "insuredAmount": "Insured amount (number)",
              "currency": "Currency code",
              "coverage": "Type of coverage",
              "goods": "Description of insured goods",
              "vessel": "Vessel name",
              "voyage": "Voyage",
              "from": "From (loading port)",
              "to": "To (destination)",
              "invoiceNumber": "Related invoice number",
              "blNumber": "Related B/L number",
              "insuranceCompany": "Insurance company name",
              "validFrom": "Valid from date (YYYY-MM-DD)",
              "validTo": "Valid to date (YYYY-MM-DD)"
            }
            """;
    }

    private String getPassportPrompt() {
        return """
            EXTRACT PASSPORT DATA:
            
            Required fields:
            {
              "passportNumber": "Passport number",
              "givenNames": "Given names",
              "surname": "Surname",
              "nationality": "Nationality",
              "dateOfBirth": "Date of birth (YYYY-MM-DD)",
              "placeOfBirth": "Place of birth",
              "sex": "Sex (M/F)",
              "issueDate": "Issue date (YYYY-MM-DD)",
              "expiryDate": "Expiry date (YYYY-MM-DD)",
              "issuingAuthority": "Issuing authority",
              "countryCode": "Country code"
            }
            """;
    }

    private String getNationalIDPrompt() {
        return """
            EXTRACT NATIONAL ID DATA:
            
            Required fields:
            {
              "idNumber": "ID card number",
              "fullName": "Full name",
              "dateOfBirth": "Date of birth (YYYY-MM-DD)",
              "nationality": "Nationality",
              "sex": "Sex",
              "address": "Address",
              "issueDate": "Issue date (YYYY-MM-DD)",
              "expiryDate": "Expiry date (YYYY-MM-DD)",
              "issuingAuthority": "Issuing authority"
            }
            """;
    }

    private String getBusinessLicensePrompt() {
        return """
            EXTRACT BUSINESS LICENSE DATA:
            
            Required fields:
            {
              "licenseNumber": "License/Registration number",
              "companyName": "Company name",
              "legalForm": "Legal form/entity type",
              "registrationDate": "Registration date (YYYY-MM-DD)",
              "businessAddress": "Business address",
              "businessActivity": "Business activity/scope",
              "authorizedCapital": "Authorized capital",
              "paidUpCapital": "Paid up capital",
              "directors": ["Director names"],
              "issuingAuthority": "Issuing authority",
              "issueDate": "Issue date (YYYY-MM-DD)",
              "expiryDate": "Expiry date if applicable (YYYY-MM-DD)",
              "taxNumber": "Tax ID number"
            }
            """;
    }

    private String getGenericPrompt() {
        return """
            EXTRACT ALL RELEVANT DATA:
            
            Extract any structured information you can identify from this document.
            Return a JSON object with key-value pairs for all extracted fields.
            Use descriptive field names.
            
            Example:
            {
              "documentType": "Type if identifiable",
              "referenceNumber": "Any reference/ID number",
              "date": "Any dates found (YYYY-MM-DD)",
              "parties": ["Names of parties involved"],
              "amounts": ["Monetary amounts with currency"],
              "locations": ["Locations mentioned"],
              ...other relevant fields...
            }
            """;
    }
}

