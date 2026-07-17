package com.tradesphere.ai.document.application.service.extraction;

import com.tradesphere.ai.document.config.ExtractionConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Enhanced Tesseract-based OCR implementation with Tess4j
 * Production-ready with configuration support
 *
 * Requires Tesseract OCR installed on the system:
 * - Linux: apt-get install tesseract-ocr tesseract-ocr-eng
 * - Mac: brew install tesseract
 * - Windows: Download from https://github.com/UB-Mannheim/tesseract/wiki
 */
@Service
@Primary
@Slf4j
@RequiredArgsConstructor
public class EnhancedTesseractOCRService implements OCRService {

    private final ExtractionConfiguration config;
    private final ImagePreprocessingService preprocessingService;

    private static final Set<String> OCR_REQUIRED_MIME_TYPES = Set.of(
        "image/jpeg",
        "image/jpg",
        "image/png",
        "image/tiff",
        "image/bmp",
        "image/gif"
    );

    @Override
    public OCRResult extractText(InputStream documentStream, String fileName, String mimeType) {
        log.info("Starting Tess4j OCR extraction for file: {}, mimeType: {}", fileName, mimeType);

        if (!config.getOcr().isEnabled()) {
            log.warn("OCR is disabled in configuration");
            return OCRResult.failure("OCR is disabled");
        }

        try {
            long startTime = System.currentTimeMillis();

            // Preprocess image if enabled
            if (config.getOcr().isPreprocessingEnabled()) {
                documentStream = preprocessingService.preprocessForOCR(documentStream, mimeType);
            }

            // Load image
            BufferedImage image = ImageIO.read(documentStream);
            if (image == null) {
                return OCRResult.failure("Failed to load image");
            }

            // Configure Tesseract
            ITesseract tesseract = createConfiguredTesseract();

            // Perform OCR
            String extractedText = tesseract.doOCR(image);

            // Calculate confidence (Tesseract doesn't provide overall confidence easily)
            float confidence = estimateConfidence(extractedText);

            long processingTime = System.currentTimeMillis() - startTime;

            OCRMetadata metadata = OCRMetadata.builder()
                .ocrEngine("Tesseract")
                .version("5.0")
                .processingTimeMs(processingTime)
                .pageCount(1)
                .documentOrientation("NORMAL")
                .averageConfidence(confidence)
                .build();

            log.info("OCR extraction completed in {}ms with confidence: {}", processingTime, confidence);

            return OCRResult.builder()
                .extractedText(extractedText)
                .confidence(confidence)
                .language(config.getOcr().getLanguage())
                .successful(true)
                .metadata(metadata)
                .build();

        } catch (TesseractException e) {
            log.error("Tesseract OCR failed for file: {}", fileName, e);
            return OCRResult.failure("Tesseract OCR failed: " + e.getMessage());
        } catch (Exception e) {
            log.error("OCR extraction failed for file: {}", fileName, e);
            return OCRResult.failure("OCR extraction failed: " + e.getMessage());
        }
    }

    @Override
    public OCRResult extractTextWithLayout(InputStream documentStream, String fileName, String mimeType) {
        log.info("Starting layout-aware Tess4j OCR extraction for file: {}", fileName);

        try {
            long startTime = System.currentTimeMillis();

            // Preprocess if enabled
            if (config.getOcr().isPreprocessingEnabled()) {
                documentStream = preprocessingService.preprocessForOCR(documentStream, mimeType);
            }

            // Load image
            BufferedImage image = ImageIO.read(documentStream);
            if (image == null) {
                return OCRResult.failure("Failed to load image");
            }

            // Configure Tesseract with layout analysis
            ITesseract tesseract = createConfiguredTesseract();
            tesseract.setPageSegMode(1); // Automatic page segmentation with OSD

            // Perform OCR
            String extractedText = tesseract.doOCR(image);

            // Extract text blocks (word/line/paragraph level)
            List<TextBlock> textBlocks = extractTextBlocksFromImage(tesseract, image);

            float confidence = estimateConfidence(extractedText);
            long processingTime = System.currentTimeMillis() - startTime;

            OCRMetadata metadata = OCRMetadata.builder()
                .ocrEngine("Tesseract")
                .version("5.0")
                .processingTimeMs(processingTime)
                .pageCount(1)
                .documentOrientation("NORMAL")
                .averageConfidence(confidence)
                .build();

            return OCRResult.builder()
                .extractedText(extractedText)
                .confidence(confidence)
                .language(config.getOcr().getLanguage())
                .successful(true)
                .textBlocks(textBlocks)
                .metadata(metadata)
                .build();

        } catch (Exception e) {
            log.error("Layout-aware OCR failed for file: {}", fileName, e);
            return OCRResult.failure("Layout-aware OCR failed: " + e.getMessage());
        }
    }

    @Override
    public boolean requiresOCR(String mimeType) {
        return OCR_REQUIRED_MIME_TYPES.contains(mimeType.toLowerCase());
    }

    /**
     * Create and configure Tesseract instance
     */
    private ITesseract createConfiguredTesseract() {
        ITesseract tesseract = new Tesseract();

        // Set tessdata path
        tesseract.setDatapath(config.getOcr().getTessdataPath());

        // Set language
        String language = config.getOcr().getLanguage();
        if (config.getOcr().getAdditionalLanguages() != null &&
            !config.getOcr().getAdditionalLanguages().isBlank()) {
            language += "+" + config.getOcr().getAdditionalLanguages();
        }
        tesseract.setLanguage(language);

        // Set page segmentation mode
        tesseract.setPageSegMode(config.getOcr().getPageSegmentationMode());

        // Set OCR Engine Mode
        tesseract.setOcrEngineMode(config.getOcr().getEngineMode());

        log.debug("Tesseract configured: language={}, PSM={}, OEM={}",
            language,
            config.getOcr().getPageSegmentationMode(),
            config.getOcr().getEngineMode());

        return tesseract;
    }

    /**
     * Extract text blocks with position information
     */
    private List<TextBlock> extractTextBlocksFromImage(ITesseract tesseract, BufferedImage image) {
        List<TextBlock> blocks = new ArrayList<>();

        try {
            // Use Tesseract's word/line detection
            // Note: Tess4j provides getWords() method for word-level extraction
            // This is a simplified version - full implementation would use getWords()

            // For now, return a single block with the full text
            String text = tesseract.doOCR(image);

            blocks.add(TextBlock.builder()
                .text(text)
                .confidence(estimateConfidence(text))
                .blockType("DOCUMENT")
                .boundingBox(BoundingBox.builder()
                    .x(0)
                    .y(0)
                    .width(image.getWidth())
                    .height(image.getHeight())
                    .build())
                .build());

        } catch (Exception e) {
            log.error("Failed to extract text blocks", e);
        }

        return blocks;
    }

    /**
     * Estimate OCR confidence based on text characteristics
     * Real Tesseract confidence requires more complex API usage
     */
    private float estimateConfidence(String text) {
        if (text == null || text.isEmpty()) {
            return 0.0f;
        }

        // Simple heuristic: Check for common OCR issues
        int length = text.length();
        int spaces = text.length() - text.replace(" ", "").length();
        int letters = text.replaceAll("[^a-zA-Z]", "").length();

        // If mostly letters and reasonable word separation, higher confidence
        float letterRatio = (float) letters / length;
        float wordSeparation = (float) spaces / length;

        // Base confidence
        float confidence = 0.5f;

        // Adjust based on text characteristics
        if (letterRatio > 0.7f) confidence += 0.2f;
        if (wordSeparation > 0.1f && wordSeparation < 0.3f) confidence += 0.2f;

        return Math.min(confidence, 0.95f);
    }
}

