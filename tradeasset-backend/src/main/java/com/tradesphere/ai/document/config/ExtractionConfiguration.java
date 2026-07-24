package com.tradesphere.ai.document.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * OCR and Document Extraction Configuration
 * Configurable settings for OCR and AI extraction
 */
@Configuration
@ConfigurationProperties(prefix = "tradesphere.document.extraction")
@Data
public class ExtractionConfiguration {

    /**
     * OCR settings
     */
    private OcrSettings ocr = new OcrSettings();

    /**
     * AI extraction settings
     */
    private AiSettings ai = new AiSettings();

    /**
     * Quality scoring settings
     */
    private QualitySettings quality = new QualitySettings();

    /**
     * Performance settings
     */
    private PerformanceSettings performance = new PerformanceSettings();

    @Data
    public static class OcrSettings {
        /**
         * Enable/disable OCR processing
         */
        private boolean enabled = true;

        /**
         * Tesseract data path (tessdata folder)
         * Default: /usr/share/tesseract-ocr/tessdata
         */
        private String tessdataPath = "/usr/share/tesseract-ocr/tessdata";

        /**
         * Default OCR language
         */
        private String language = "eng";

        /**
         * Additional languages (comma-separated)
         * e.g., "eng,fra,deu" for English, French, German
         */
        private String additionalLanguages = "";

        /**
         * Page segmentation mode
         * 1 = Automatic with OSD
         * 3 = Fully automatic (default)
         * 6 = Uniform block of text
         */
        private int pageSegmentationMode = 3;

        /**
         * OCR Engine Mode
         * 0 = Legacy engine
         * 1 = Neural nets LSTM engine
         * 2 = Legacy + LSTM engines
         * 3 = Default (based on availability)
         */
        private int engineMode = 1;

        /**
         * Enable image preprocessing for better OCR results
         */
        private boolean preprocessingEnabled = true;

        /**
         * Minimum confidence threshold (0.0 to 1.0)
         * OCR results below this will trigger warnings
         */
        private float minConfidence = 0.7f;
    }

    @Data
    public static class AiSettings {
        /**
         * Enable/disable AI extraction
         */
        private boolean enabled = true;

        /**
         * AI provider: openai, anthropic, azure, or custom
         */
        private String provider = "openai";

        /**
         * AI model to use
         */
        private String model = "gpt-4-vision-preview";

        /**
         * API key (use environment variable in production)
         */
        private String apiKey = "${OPENAI_API_KEY}";

        /**
         * API endpoint URL
         */
        private String apiUrl = "https://api.openai.com/v1";

        /**
         * Request timeout in seconds
         */
        private int timeoutSeconds = 60;

        /**
         * Maximum retries for failed requests
         */
        private int maxRetries = 3;

        /**
         * Temperature for AI model (0.0 to 1.0)
         * Lower = more deterministic, Higher = more creative
         */
        private float temperature = 0.1f;

        /**
         * Max tokens for AI response
         */
        private int maxTokens = 2000;
    }

    @Data
    public static class QualitySettings {
        /**
         * Enable quality scoring
         */
        private boolean enabled = true;

        /**
         * Minimum quality score for auto-approval (0.0 to 1.0)
         * Documents below this require manual review
         */
        private float minAutoApprovalScore = 0.85f;

        /**
         * Minimum quality score to accept (0.0 to 1.0)
         * Documents below this are rejected
         */
        private float minAcceptableScore = 0.5f;

        /**
         * Enable automatic validation of extracted data
         */
        private boolean autoValidation = true;

        /**
         * Enable cross-field consistency checks
         */
        private boolean consistencyChecks = true;
    }

    @Data
    public static class PerformanceSettings {
        /**
         * Enable parallel processing for multi-page documents
         */
        private boolean parallelProcessing = true;

        /**
         * Maximum number of parallel threads
         */
        private int maxThreads = 4;

        /**
         * Enable caching of extraction results
         */
        private boolean cacheEnabled = true;

        /**
         * Cache TTL in minutes
         */
        private int cacheTtlMinutes = 60;

        /**
         * Maximum document size in MB
         */
        private int maxDocumentSizeMb = 50;

        /**
         * Timeout for extraction in seconds
         */
        private int extractionTimeoutSeconds = 300;
    }
}

