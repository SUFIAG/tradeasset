package com.tradesphere.ai.document.adapter.out.ocr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Mock OCR Service
 * 
 * In production, this would integrate with:
 * - Tesseract OCR (open source)
 * - AWS Textract
 * - Google Cloud Vision API
 * - Azure Computer Vision
 * - ABBYY FineReader
 */
@Service
@Slf4j
public class MockOCRService implements OCRService {
    
    private static final List<String> SUPPORTED_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/tiff",
            "image/bmp",
            "application/pdf"
    );
    
    @Override
    public OCRResult extractText(byte[] fileContent, String fileName, String mimeType) {
        log.info("Performing OCR on file: {}, type: {}, size: {} bytes",
                fileName, mimeType, fileContent.length);
        
        if (!isSupported(mimeType)) {
            return new OCRResult(
                    "",
                    0,
                    0.0,
                    "unknown",
                    false,
                    "Unsupported file type: " + mimeType
            );
        }
        
        // Mock OCR extraction
        String extractedText = generateMockExtractedText(fileName, mimeType);
        
        Integer pageCount = mimeType.equals("application/pdf") ? 
                (int) (Math.random() * 5) + 1 : 1;
        
        Double confidence = 0.85 + (Math.random() * 0.15); // 85-100%
        
        log.info("OCR completed: {} pages, confidence: {:.2f}%, {} characters extracted",
                pageCount, confidence * 100, extractedText.length());
        
        return new OCRResult(
                extractedText,
                pageCount,
                confidence,
                "en",
                true,
                null
        );
    }
    
    @Override
    public boolean isSupported(String mimeType) {
        return SUPPORTED_TYPES.contains(mimeType);
    }
    
    /**
     * Generate mock extracted text based on file type
     */
    private String generateMockExtractedText(String fileName, String mimeType) {
        String lowerFileName = fileName.toLowerCase();
        
        if (lowerFileName.contains("invoice") || lowerFileName.contains("commercial")) {
            return generateMockInvoiceText();
        } else if (lowerFileName.contains("bill") && lowerFileName.contains("lading")) {
            return generateMockBillOfLadingText();
        } else if (lowerFileName.contains("packing")) {
            return generateMockPackingListText();
        } else if (lowerFileName.contains("certificate")) {
            return generateMockCertificateText();
        } else {
            return generateGenericMockText();
        }
    }
    
    private String generateMockInvoiceText() {
        return """
                COMMERCIAL INVOICE
                
                Invoice No: INV-2026-001234
                Date: July 15, 2026
                
                Seller:
                ABC Trading Company Ltd.
                123 Business Street
                New York, NY 10001
                USA
                
                Buyer:
                XYZ Import Export Corp.
                456 Commerce Road
                London, SW1A 1AA
                United Kingdom
                
                Description of Goods:
                - Electronic Components (HS Code: 8542.31)
                  Quantity: 10,000 units
                  Unit Price: USD 12.50
                  Total: USD 125,000.00
                
                Subtotal: USD 125,000.00
                Shipping: USD 5,000.00
                Insurance: USD 2,000.00
                
                GRAND TOTAL: USD 132,000.00
                
                Payment Terms: LC at sight
                Incoterms: FOB New York
                """;
    }
    
    private String generateMockBillOfLadingText() {
        return """
                BILL OF LADING
                
                B/L No: BL-2026-567890
                Date of Issue: July 16, 2026
                
                Shipper:
                ABC Trading Company Ltd.
                123 Business Street, New York, NY 10001, USA
                
                Consignee:
                XYZ Import Export Corp.
                456 Commerce Road, London, SW1A 1AA, UK
                
                Notify Party: Same as consignee
                
                Vessel: MV OCEAN CARRIER
                Voyage No: 2026-07
                Port of Loading: New York, USA
                Port of Discharge: Southampton, UK
                
                Description of Goods:
                10 cartons of electronic components
                HS Code: 8542.31
                Gross Weight: 500 kg
                Net Weight: 450 kg
                Measurement: 5 CBM
                
                Container No: CONT1234567
                Seal No: SEAL789456
                
                Freight: Prepaid
                """;
    }
    
    private String generateMockPackingListText() {
        return """
                PACKING LIST
                
                Packing List No: PL-2026-001234
                Date: July 15, 2026
                Invoice No: INV-2026-001234
                
                Shipper: ABC Trading Company Ltd.
                Consignee: XYZ Import Export Corp.
                
                Packing Details:
                
                Carton 1-5:
                - Electronic Components Type A
                - Quantity: 5,000 units
                - Net Weight: 225 kg
                - Gross Weight: 250 kg
                
                Carton 6-10:
                - Electronic Components Type B
                - Quantity: 5,000 units
                - Net Weight: 225 kg
                - Gross Weight: 250 kg
                
                Total Cartons: 10
                Total Net Weight: 450 kg
                Total Gross Weight: 500 kg
                Total Volume: 5 CBM
                """;
    }
    
    private String generateMockCertificateText() {
        return """
                CERTIFICATE OF ORIGIN
                
                Certificate No: CO-2026-123456
                Issue Date: July 15, 2026
                
                Exporter: ABC Trading Company Ltd.
                Importer: XYZ Import Export Corp.
                
                Country of Origin: United States of America
                
                Description of Goods:
                Electronic Components
                HS Code: 8542.31
                Quantity: 10,000 units
                
                We hereby certify that the goods described above originated in the United States of America.
                
                Issued by:
                U.S. Chamber of Commerce
                
                Authorized Signature: _______________
                Date: July 15, 2026
                Stamp: [OFFICIAL SEAL]
                """;
    }
    
    private String generateGenericMockText() {
        return """
                DOCUMENT
                
                This is a sample document extracted via OCR.
                
                Document contains various text elements that would be
                extracted from the actual image or PDF file.
                
                Date: July 17, 2026
                Reference: DOC-2026-999999
                
                Content would be extracted here based on the
                actual document type and layout.
                """;
    }
}

