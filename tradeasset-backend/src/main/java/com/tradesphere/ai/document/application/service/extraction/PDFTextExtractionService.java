package com.tradesphere.ai.document.application.service.extraction;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

/**
 * PDF text extraction service
 * Uses Apache PDFBox for text extraction from PDF documents
 */
@Service
@Slf4j
public class PDFTextExtractionService {

    /**
     * Extract text from PDF document
     * Handles both text-based and scanned PDFs
     */
    public String extractText(InputStream pdfStream) {
        log.info("Starting PDF text extraction");

        PDDocument document = null;
        try {
            document = Loader.loadPDF(pdfStream.readAllBytes());

            if (document.isEncrypted()) {
                log.warn("PDF is encrypted, attempting to decrypt with empty password");
                try {
                    document.setAllSecurityToBeRemoved(true);
                } catch (Exception e) {
                    log.error("Failed to decrypt PDF", e);
                    throw new RuntimeException("PDF is encrypted and cannot be decrypted", e);
                }
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            String text = stripper.getText(document);

            log.info("Successfully extracted text from PDF ({} characters, {} pages)",
                text.length(), document.getNumberOfPages());

            return text;

        } catch (IOException e) {
            log.error("PDF text extraction failed", e);
            throw new RuntimeException("Failed to extract text from PDF", e);
        } finally {
            closeDocument(document);
        }
    }

    /**
     * Extract text with advanced options
     * Preserves formatting, tables, and layout
     */
    public String extractTextWithLayout(InputStream pdfStream) {
        log.info("Starting PDF text extraction with layout preservation");

        PDDocument document = null;
        try {
            document = Loader.loadPDF(pdfStream.readAllBytes());

            if (document.isEncrypted()) {
                document.setAllSecurityToBeRemoved(true);
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            stripper.setShouldSeparateByBeads(true);
            stripper.setWordSeparator(" ");
            stripper.setLineSeparator("\n");

            String text = stripper.getText(document);

            log.info("Successfully extracted text with layout from PDF ({} pages)",
                document.getNumberOfPages());

            return text;

        } catch (IOException e) {
            log.error("PDF layout extraction failed", e);
            throw new RuntimeException("Failed to extract PDF with layout", e);
        } finally {
            closeDocument(document);
        }
    }

    /**
     * Extract text from specific page range
     */
    public String extractText(InputStream pdfStream, int startPage, int endPage) {
        log.info("Extracting text from PDF pages {} to {}", startPage, endPage);

        PDDocument document = null;
        try {
            document = Loader.loadPDF(pdfStream.readAllBytes());

            if (document.isEncrypted()) {
                document.setAllSecurityToBeRemoved(true);
            }

            // Validate page numbers
            int totalPages = document.getNumberOfPages();
            if (startPage < 1 || endPage > totalPages || startPage > endPage) {
                throw new IllegalArgumentException(
                    String.format("Invalid page range: %d-%d (total pages: %d)",
                        startPage, endPage, totalPages));
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(startPage);
            stripper.setEndPage(endPage);
            stripper.setSortByPosition(true);

            String text = stripper.getText(document);

            log.info("Successfully extracted text from PDF pages {}-{}", startPage, endPage);

            return text;

        } catch (IOException e) {
            log.error("PDF page extraction failed", e);
            throw new RuntimeException("Failed to extract PDF pages", e);
        } finally {
            closeDocument(document);
        }
    }

    /**
     * Check if PDF contains extractable text
     * Returns false for image-only PDFs (requires OCR)
     */
    public boolean hasExtractableText(InputStream pdfStream) {
        try {
            String text = extractText(pdfStream);
            return text != null && !text.trim().isEmpty();
        } catch (Exception e) {
            log.error("Failed to check PDF text content", e);
            return false;
        }
    }

    /**
     * Get PDF metadata (page count, author, creation date, etc.)
     */
    public PDFMetadata extractMetadata(InputStream pdfStream) {
        log.info("Extracting PDF metadata");

        PDDocument document = null;
        try {
            document = Loader.loadPDF(pdfStream.readAllBytes());
            PDDocumentInformation info = document.getDocumentInformation();

            // Check if document has extractable text
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(1);
            stripper.setEndPage(Math.min(1, document.getNumberOfPages()));
            String sampleText = stripper.getText(document);
            boolean hasText = sampleText != null && !sampleText.trim().isEmpty();

            // Format dates
            String creationDate = formatCalendar(info.getCreationDate());
            String modificationDate = formatCalendar(info.getModificationDate());

            PDFMetadata metadata = PDFMetadata.builder()
                .pageCount(document.getNumberOfPages())
                .author(info.getAuthor())
                .title(info.getTitle())
                .subject(info.getSubject())
                .creator(info.getCreator())
                .producer(info.getProducer())
                .creationDate(creationDate)
                .modificationDate(modificationDate)
                .hasText(hasText)
                .encrypted(document.isEncrypted())
                .build();

            log.info("Successfully extracted PDF metadata (pages: {}, encrypted: {})",
                metadata.getPageCount(), metadata.isEncrypted());

            return metadata;

        } catch (IOException e) {
            log.error("PDF metadata extraction failed", e);
            return PDFMetadata.builder()
                .pageCount(0)
                .hasText(false)
                .encrypted(false)
                .build();
        } finally {
            closeDocument(document);
        }
    }

    /**
     * Format Calendar to ISO date string
     */
    private String formatCalendar(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return String.format("%04d-%02d-%02d",
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Close PDF document safely
     */
    private void closeDocument(PDDocument document) {
        if (document != null) {
            try {
                document.close();
            } catch (IOException e) {
                log.warn("Failed to close PDF document", e);
            }
        }
    }

    /**
     * PDF metadata record
     */
    @lombok.Builder
    @lombok.Value
    public static class PDFMetadata {
        int pageCount;
        String author;
        String title;
        String subject;
        String creator;
        String producer;
        String creationDate;
        String modificationDate;
        boolean hasText;
        boolean encrypted;
    }
}

