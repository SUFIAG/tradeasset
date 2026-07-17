package com.tradesphere.ai.document.application.service.extraction;

import lombok.Builder;
import lombok.Value;

import java.io.InputStream;
import java.util.List;

/**
 * OCR (Optical Character Recognition) Service
 * Extracts text from images and scanned documents
 */
public interface OCRService {

    /**
     * Extract text from document using OCR
     * Supports images and scanned PDFs
     */
    OCRResult extractText(InputStream documentStream, String fileName, String mimeType);

    /**
     * Extract text with layout analysis
     * Preserves document structure (tables, columns, etc.)
     */
    OCRResult extractTextWithLayout(InputStream documentStream, String fileName, String mimeType);

    /**
     * Check if document requires OCR processing
     */
    boolean requiresOCR(String mimeType);

    /**
     * OCR Result containing extracted text and metadata
     */
    @Value
    @Builder
    class OCRResult {
        String extractedText;
        Float confidence;
        String language;
        boolean successful;
        String errorMessage;
        List<TextBlock> textBlocks;
        OCRMetadata metadata;

        public static OCRResult success(String text, Float confidence, String language) {
            return OCRResult.builder()
                .extractedText(text)
                .confidence(confidence)
                .language(language)
                .successful(true)
                .build();
        }

        public static OCRResult failure(String errorMessage) {
            return OCRResult.builder()
                .successful(false)
                .errorMessage(errorMessage)
                .build();
        }
    }

    /**
     * Text block representing a region of text in the document
     */
    @Value
    @Builder
    class TextBlock {
        String text;
        Float confidence;
        BoundingBox boundingBox;
        String blockType; // PARAGRAPH, LINE, WORD, TABLE, etc.
    }

    /**
     * Bounding box coordinates for text blocks
     */
    @Value
    @Builder
    class BoundingBox {
        int x;
        int y;
        int width;
        int height;
    }

    /**
     * OCR processing metadata
     */
    @Value
    @Builder
    class OCRMetadata {
        String ocrEngine;
        String version;
        long processingTimeMs;
        int pageCount;
        String documentOrientation;
        Float averageConfidence;
    }
}

