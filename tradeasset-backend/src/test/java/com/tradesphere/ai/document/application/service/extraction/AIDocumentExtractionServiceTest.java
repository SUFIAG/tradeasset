package com.tradesphere.ai.document.application.service.extraction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradesphere.ai.document.domain.model.DocumentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Test suite for AI Document Extraction Service
 * Tests OCR integration and AI extraction
 */
@ExtendWith(MockitoExtension.class)
class AIDocumentExtractionServiceTest {

    @Mock
    private OCRService ocrService;

    @Mock
    private ExtractionPromptFactory promptFactory;

    @Mock
    private AIExtractionService aiExtractionService;

    @Mock
    private UniversalDocumentReader documentReader;

    private AIDocumentExtractionService extractionService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        extractionService = new AIDocumentExtractionService(
            ocrService,
            promptFactory,
            objectMapper,
            aiExtractionService,
            documentReader
        );
    }

    @Test
    void testExtractDocumentFromTextPDF() {
        // Given
        DocumentType docType = DocumentType.COMMERCIAL_INVOICE;
        String fileName = "invoice.pdf";
        String mimeType = "application/pdf";
        InputStream docStream = createSamplePDFStream();

        // When
        ExtractionResult result = extractionService.extractDocument(
            docType, docStream, fileName, mimeType
        );

        // Then
        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.getExtractedData()).isNotEmpty();
        assertThat(result.getConfidence()).isGreaterThan(0.0f);
    }

    @Test
    void testExtractDocumentFromImage() {
        // Given
        DocumentType docType = DocumentType.COMMERCIAL_INVOICE;
        String fileName = "invoice.jpg";
        String mimeType = "image/jpeg";
        InputStream docStream = createSampleImageStream();

        // Mock OCR service
        when(ocrService.requiresOCR(mimeType)).thenReturn(true);
        when(ocrService.extractText(any(), eq(fileName), eq(mimeType)))
            .thenReturn(createSuccessfulOCRResult());

        // When
        ExtractionResult result = extractionService.extractDocument(
            docType, docStream, fileName, mimeType
        );

        // Then
        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.getExtractedData()).isNotEmpty();
    }

    @Test
    void testSupportsExtraction() {
        // Test supported types
        assertThat(extractionService.supportsExtraction(DocumentType.COMMERCIAL_INVOICE)).isTrue();
        assertThat(extractionService.supportsExtraction(DocumentType.BILL_OF_LADING)).isTrue();
        assertThat(extractionService.supportsExtraction(DocumentType.PASSPORT)).isTrue();

        // Test unsupported types
        assertThat(extractionService.supportsExtraction(DocumentType.OTHER)).isFalse();
    }

    @Test
    void testExtractionWithLowConfidence() {
        // Given
        DocumentType docType = DocumentType.COMMERCIAL_INVOICE;
        String fileName = "poor_quality.jpg";
        String mimeType = "image/jpeg";
        InputStream docStream = createSampleImageStream();

        // Mock low confidence OCR
        when(ocrService.requiresOCR(mimeType)).thenReturn(true);
        when(ocrService.extractText(any(), eq(fileName), eq(mimeType)))
            .thenReturn(createLowConfidenceOCRResult());

        // When
        ExtractionResult result = extractionService.extractDocument(
            docType, docStream, fileName, mimeType
        );

        // Then
        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.getConfidence()).isLessThan(0.7f);
    }

    @Test
    void testExtractionFailure() {
        // Given
        DocumentType docType = DocumentType.COMMERCIAL_INVOICE;
        String fileName = "corrupted.jpg";
        String mimeType = "image/jpeg";
        InputStream docStream = createEmptyStream();

        // When
        ExtractionResult result = extractionService.extractDocument(
            docType, docStream, fileName, mimeType
        );

        // Then
        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.getErrorMessage()).isNotNull();
    }

    // Helper methods

    private InputStream createSamplePDFStream() {
        String content = "Sample PDF content";
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    private InputStream createSampleImageStream() {
        String content = "Sample image data";
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    private InputStream createEmptyStream() {
        return new ByteArrayInputStream(new byte[0]);
    }

    private OCRService.OCRResult createSuccessfulOCRResult() {
        return OCRService.OCRResult.builder()
            .extractedText("""
                COMMERCIAL INVOICE
                Invoice No: INV-2024-001
                Date: 2024-07-15
                Seller: ABC Trading
                Buyer: XYZ Imports
                Amount: 50000.00
                Currency: USD
                """)
            .confidence(0.9f)
            .language("eng")
            .successful(true)
            .build();
    }

    private OCRService.OCRResult createLowConfidenceOCRResult() {
        return OCRService.OCRResult.builder()
            .extractedText("Partially recognized text")
            .confidence(0.5f)
            .language("eng")
            .successful(true)
            .build();
    }
}

