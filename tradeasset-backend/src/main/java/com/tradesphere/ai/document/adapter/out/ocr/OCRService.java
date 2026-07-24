package com.tradesphere.ai.document.adapter.out.ocr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * OCR Service Interface
 * Extracts text from images and PDFs
 */
public interface OCRService {

    /**
     * Extract text from file
     */
    OCRResult extractText(byte[] fileContent, String fileName, String mimeType);

    /**
     * Check if file type is supported
     */
    boolean isSupported(String mimeType);

    /**
     * OCR extraction result
     */
    record OCRResult(
            String extractedText,
            Integer pageCount,
            Double confidence,
            String language,
            boolean success,
            String errorMessage
    ) {}
}

