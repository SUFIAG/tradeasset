package com.tradesphere.ai.document.application.service.extraction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.tika.Tika;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.mime.MediaType;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * Universal document text extraction service
 * Supports multiple formats: PDF, DOCX, images, etc.
 *
 * Uses Apache Tika for universal format support
 * For production, add dependency:
 * <dependency>
 *     <groupId>org.apache.tika</groupId>
 *     <artifactId>tika-core</artifactId>
 *     <version>2.9.1</version>
 * </dependency>
 * <dependency>
 *     <groupId>org.apache.tika</groupId>
 *     <artifactId>tika-parsers-standard-package</artifactId>
 *     <version>2.9.1</version>
 * </dependency>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UniversalDocumentReader {

    private final PDFTextExtractionService pdfExtractor;
    private final OCRService ocrService;

    /**
     * Extract text from any supported document format
     * Automatically detects format and uses appropriate extraction method
     */
    public String extractText(InputStream documentStream, String fileName, String mimeType) {
        log.info("Extracting text from document: {}, mimeType: {}", fileName, mimeType);

        try {
            return switch (mimeType.toLowerCase()) {
                case "application/pdf" -> extractFromPDF(documentStream);
                case "image/jpeg", "image/jpg", "image/png", "image/tiff", "image/bmp" ->
                    extractFromImage(documentStream, fileName, mimeType);
                case "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document" ->
                    extractFromWord(documentStream);
                case "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" ->
                    extractFromExcel(documentStream);
                case "text/plain", "text/csv" ->
                    extractFromText(documentStream);
                default -> extractUsingTika(documentStream);
            };

        } catch (Exception e) {
            log.error("Text extraction failed for: {}", fileName, e);
            throw new RuntimeException("Failed to extract text from document", e);
        }
    }

    /**
     * Extract text from PDF
     */
    private String extractFromPDF(InputStream pdfStream) {
        log.debug("Extracting from PDF");
        return pdfExtractor.extractText(pdfStream);
    }

    /**
     * Extract text from image using OCR
     */
    private String extractFromImage(InputStream imageStream, String fileName, String mimeType) {
        log.debug("Extracting from image using OCR");
        OCRService.OCRResult result = ocrService.extractText(imageStream, fileName, mimeType);

        if (!result.isSuccessful()) {
            throw new RuntimeException("OCR extraction failed: " + result.getErrorMessage());
        }

        return result.getExtractedText();
    }

    /**
     * Extract text from Microsoft Word documents
     */
    private String extractFromWord(InputStream wordStream) {
        log.debug("Extracting from Word document");

        try {
            XWPFDocument document = new XWPFDocument(wordStream);
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            String text = extractor.getText();

            log.info("Successfully extracted text from Word document ({} characters)",
                text != null ? text.length() : 0);

            extractor.close();
            document.close();

            return text;

        } catch (Exception e) {
            log.error("Word extraction failed", e);
            throw new RuntimeException("Failed to extract from Word document", e);
        }
    }

    /**
     * Extract text from Excel spreadsheets
     */
    private String extractFromExcel(InputStream excelStream) {
        log.debug("Extracting from Excel document");

        try {
            Workbook workbook = WorkbookFactory.create(excelStream);
            StringBuilder text = new StringBuilder();

            DataFormatter formatter = new DataFormatter();

            for (Sheet sheet : workbook) {
                text.append("Sheet: ").append(sheet.getSheetName()).append("\n");

                for (Row row : sheet) {
                    for (Cell cell : row) {
                        String cellValue = formatter.formatCellValue(cell);
                        if (cellValue != null && !cellValue.trim().isEmpty()) {
                            text.append(cellValue).append("\t");
                        }
                    }
                    text.append("\n");
                }
                text.append("\n");
            }

            workbook.close();

            log.info("Successfully extracted text from Excel document ({} characters)",
                text.length());

            return text.toString();

        } catch (Exception e) {
            log.error("Excel extraction failed", e);
            throw new RuntimeException("Failed to extract from Excel document", e);
        }
    }

    /**
     * Extract text from plain text files
     */
    private String extractFromText(InputStream textStream) {
        log.debug("Reading plain text file");

        try {
            String text = new String(textStream.readAllBytes());
            log.info("Successfully read text file ({} characters)", text.length());
            return text;
        } catch (Exception e) {
            log.error("Text file reading failed", e);
            throw new RuntimeException("Failed to read text file", e);
        }
    }

    /**
     * Universal extraction using Apache Tika
     * Fallback for unknown formats
     */
    private String extractUsingTika(InputStream documentStream) {
        log.debug("Using Apache Tika for extraction");

        try {
            Tika tika = new Tika();
            String text = tika.parseToString(documentStream);

            log.info("Successfully extracted text using Tika ({} characters)",
                text != null ? text.length() : 0);

            return text;

        } catch (Exception e) {
            log.error("Tika extraction failed", e);
            throw new RuntimeException("Failed to extract using Tika", e);
        }
    }

    /**
     * Detect document MIME type from content
     * Useful when MIME type is unknown or incorrect
     */
    public String detectMimeType(InputStream documentStream, String fileName) {
        log.info("Detecting MIME type for: {}", fileName);

        try {
            Detector detector = new DefaultDetector();
            Metadata metadata = new Metadata();
            metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, fileName);

            MediaType mediaType = detector.detect(documentStream, metadata);
            String detectedType = mediaType.toString();

            log.info("Detected MIME type: {} for file: {}", detectedType, fileName);

            return detectedType;

        } catch (Exception e) {
            log.error("MIME type detection failed", e);

            // Fallback to extension-based detection
            return detectMimeTypeByExtension(fileName);
        }
    }

    /**
     * Fallback MIME type detection based on file extension
     */
    private String detectMimeTypeByExtension(String fileName) {
        if (fileName == null) {
            return "application/octet-stream";
        }

        String lowerName = fileName.toLowerCase();

        if (lowerName.endsWith(".pdf")) return "application/pdf";
        if (lowerName.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        if (lowerName.endsWith(".doc")) return "application/msword";
        if (lowerName.endsWith(".xlsx")) return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        if (lowerName.endsWith(".xls")) return "application/vnd.ms-excel";
        if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) return "image/jpeg";
        if (lowerName.endsWith(".png")) return "image/png";
        if (lowerName.endsWith(".tiff") || lowerName.endsWith(".tif")) return "image/tiff";
        if (lowerName.endsWith(".txt")) return "text/plain";
        if (lowerName.endsWith(".csv")) return "text/csv";

        return "application/octet-stream";
    }

    /**
     * Extract metadata from document
     */
    public DocumentMetadata extractMetadata(InputStream documentStream) {
        log.info("Extracting document metadata");

        try {
            Tika tika = new Tika();
            Metadata metadata = new Metadata();

            // Parse document to extract metadata
            tika.parse(documentStream, metadata);

            return DocumentMetadata.builder()
                .title(metadata.get(TikaCoreProperties.TITLE))
                .author(metadata.get(TikaCoreProperties.CREATOR))
                .subject(metadata.get(TikaCoreProperties.SUBJECT))
                .creator(metadata.get(TikaCoreProperties.CREATOR))
                .creationDate(metadata.get(TikaCoreProperties.CREATED))
                .modificationDate(metadata.get(TikaCoreProperties.MODIFIED))
                .pageCount(getPageCount(metadata))
                .language(metadata.get(TikaCoreProperties.LANGUAGE))
                .format(metadata.get(HttpHeaders.CONTENT_TYPE))
                .build();

        } catch (Exception e) {
            log.error("Metadata extraction failed", e);
            return DocumentMetadata.builder().build();
        }
    }

    /**
     * Extract page count from metadata
     */
    private int getPageCount(Metadata metadata) {
        String pageCountStr = metadata.get("xmpTPg:NPages");
        if (pageCountStr != null) {
            try {
                return Integer.parseInt(pageCountStr);
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
        return 0;
    }

    /**
     * Document metadata
     */
    @lombok.Builder
    @lombok.Value
    public static class DocumentMetadata {
        String title;
        String author;
        String subject;
        String creator;
        String creationDate;
        String modificationDate;
        int pageCount;
        String language;
        String format;
    }
}
