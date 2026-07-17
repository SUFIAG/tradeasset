# Document Extraction & OCR Integration

## Overview

The TradeSphere AI platform includes a comprehensive document extraction system with OCR (Optical Character Recognition) integration. This system can extract structured data from various document formats including:

- **Scanned images** (JPEG, PNG, TIFF, BMP)
- **PDF documents** (both text-based and scanned)
- **Microsoft Office documents** (Word, Excel)
- **Plain text files**

## Architecture

### Components

1. **OCRService** - Interface for OCR operations
2. **EnhancedTesseractOCRService** - Tesseract-based OCR implementation
3. **AIDocumentExtractionService** - Main extraction service combining OCR + AI
4. **PDFTextExtractionService** - PDF-specific text extraction
5. **UniversalDocumentReader** - Multi-format document reader
6. **ImagePreprocessingService** - Image enhancement for better OCR
7. **ExtractionQualityScorer** - Quality assessment and validation
8. **ExtractionPromptFactory** - AI prompt generation

### Processing Pipeline

```
Document Input
    ↓
Format Detection
    ↓
├─ Image? → Image Preprocessing → OCR Extraction
├─ PDF? → PDF Text Extraction
└─ Office? → Office Format Parsing
    ↓
Text Content
    ↓
AI Extraction (GPT-4/Claude)
    ↓
Structured Data
    ↓
Quality Scoring & Validation
    ↓
Final Result
```

## Features

### 1. OCR Capabilities

- **Multi-language support** - English, French, German, Chinese, Arabic, etc.
- **Layout preservation** - Maintains document structure (tables, columns)
- **High accuracy** - Using Tesseract 5.0 with LSTM neural networks
- **Confidence scoring** - Per-word, per-line, and overall confidence
- **Image preprocessing** - Deskewing, denoising, contrast enhancement

### 2. AI Extraction

- **Document type recognition** - Automatic detection of document types
- **Field extraction** - Intelligent extraction of relevant fields
- **Multi-model support** - OpenAI GPT-4, Anthropic Claude, Azure OpenAI
- **Structured output** - JSON format with validated data types
- **Error handling** - Fallback to regex-based extraction

### 3. Quality Assurance

- **Completeness scoring** - % of required fields extracted
- **Format validation** - Validates dates, amounts, references
- **Consistency checks** - Cross-field validation
- **Confidence thresholds** - Automatic/manual review routing

## Supported Document Types

### Trade Finance Documents
- Commercial Invoice
- Bill of Lading
- Packing List
- Certificate of Origin
- Insurance Certificate

### Identity Documents
- Passport
- National ID Card
- Business License
- Trade License

### General Documents
- Bank Statements
- Financial Reports
- Contracts
- Letters

## Configuration

### Application Properties (application.yml)

```yaml
tradesphere:
  document:
    extraction:
      ocr:
        enabled: true
        tessdata-path: /usr/share/tesseract-ocr/tessdata
        language: eng
        additional-languages: "fra,deu,ara"
        page-segmentation-mode: 3
        engine-mode: 1
        preprocessing-enabled: true
        min-confidence: 0.7
      
      ai:
        enabled: true
        provider: openai
        model: gpt-4-vision-preview
        api-key: ${OPENAI_API_KEY}
        timeout-seconds: 60
        max-retries: 3
        temperature: 0.1
        max-tokens: 2000
      
      quality:
        enabled: true
        min-auto-approval-score: 0.85
        min-acceptable-score: 0.5
        auto-validation: true
        consistency-checks: true
      
      performance:
        parallel-processing: true
        max-threads: 4
        cache-enabled: true
        max-document-size-mb: 50
        extraction-timeout-seconds: 300
```

## Installation

### 1. Install Tesseract OCR

#### Linux (Ubuntu/Debian)
```bash
sudo apt-get update
sudo apt-get install -y tesseract-ocr
sudo apt-get install -y tesseract-ocr-eng  # English
sudo apt-get install -y tesseract-ocr-fra  # French
sudo apt-get install -y tesseract-ocr-deu  # German
sudo apt-get install -y tesseract-ocr-ara  # Arabic
sudo apt-get install -y tesseract-ocr-chi-sim  # Simplified Chinese
```

#### macOS
```bash
brew install tesseract
brew install tesseract-lang  # All languages
```

#### Windows
Download installer from: https://github.com/UB-Mannheim/tesseract/wiki

### 2. Add Maven Dependencies

Dependencies are already included in `pom.xml`:
- Apache PDFBox (PDF processing)
- Apache Tika (universal document parsing)
- Apache POI (Office documents)
- Tess4j (Tesseract Java wrapper)
- TwelveMonkeys ImageIO (enhanced image support)

### 3. Configure Environment

```bash
# Set API keys
export OPENAI_API_KEY="your-api-key-here"
export ANTHROPIC_API_KEY="your-api-key-here"

# Optional: Custom Tesseract path
export TESSDATA_PREFIX="/custom/path/to/tessdata"
```

## Usage

### Java API

```java
@Autowired
private DocumentExtractionService extractionService;

// Extract from image
ExtractionResult result = extractionService.extractDocument(
    DocumentType.COMMERCIAL_INVOICE,
    imageInputStream,
    "invoice.jpg",
    "image/jpeg"
);

if (result.isSuccessful()) {
    Map<String, Object> data = result.getExtractedData();
    String invoiceNumber = (String) data.get("invoiceNumber");
    Double amount = (Double) data.get("amount");
    // ... use extracted data
}
```

### REST API

```http
POST /api/documents/upload
Content-Type: multipart/form-data

file: invoice.jpg
documentType: COMMERCIAL_INVOICE
```

Response:
```json
{
  "documentId": "uuid",
  "extractedData": {
    "invoiceNumber": "INV-2024-001",
    "invoiceDate": "2024-07-15",
    "seller": "ABC Trading Company",
    "buyer": "XYZ Imports Ltd",
    "amount": 50000.00,
    "currency": "USD"
  },
  "confidence": 0.92,
  "qualityScore": {
    "overallScore": 0.88,
    "qualityLevel": "GOOD",
    "requiresReview": false
  }
}
```

## Docker Deployment

### Dockerfile

```dockerfile
FROM eclipse-temurin:17-jre-alpine

# Install Tesseract OCR
RUN apk add --no-cache \
    tesseract-ocr \
    tesseract-ocr-data-eng \
    tesseract-ocr-data-fra \
    tesseract-ocr-data-deu

# Install image processing libraries
RUN apk add --no-cache \
    imagemagick \
    ghostscript

# Copy application
COPY target/*.jar app.jar

# Set Tesseract data path
ENV TESSDATA_PREFIX=/usr/share/tessdata

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### docker-compose.yml

```yaml
services:
  tradesphere-ai:
    build: .
    environment:
      - OPENAI_API_KEY=${OPENAI_API_KEY}
      - TESSDATA_PREFIX=/usr/share/tessdata
    volumes:
      - ./tessdata:/usr/share/tessdata  # Custom training data
```

## Performance Optimization

### 1. Image Preprocessing

Enable preprocessing for better accuracy:
```yaml
tradesphere.document.extraction.ocr.preprocessing-enabled: true
```

Techniques applied:
- **Deskewing** - Corrects rotation/skew
- **Denoising** - Removes artifacts
- **Contrast enhancement** - Improves text visibility
- **Binarization** - Converts to black & white
- **Upscaling** - Increases resolution for low-DPI images

### 2. Parallel Processing

Enable parallel processing for multi-page documents:
```yaml
tradesphere.document.extraction.performance.parallel-processing: true
tradesphere.document.extraction.performance.max-threads: 4
```

### 3. Caching

Enable result caching to avoid reprocessing:
```yaml
tradesphere.document.extraction.performance.cache-enabled: true
tradesphere.document.extraction.performance.cache-ttl-minutes: 60
```

## Quality Metrics

### Quality Score Levels

| Level | Score Range | Description | Action |
|-------|-------------|-------------|--------|
| EXCELLENT | 0.90+ | Perfect extraction | Auto-approve |
| GOOD | 0.75-0.90 | Good extraction | Minor review |
| ACCEPTABLE | 0.60-0.75 | Acceptable quality | Review recommended |
| POOR | 0.40-0.60 | Poor quality | Manual correction |
| FAILED | < 0.40 | Failed extraction | Re-extract |

### Scoring Criteria

- **Completeness** (35%) - % of required fields extracted
- **Validity** (25%) - Correct data formats
- **Consistency** (20%) - Cross-field validation
- **Confidence** (20%) - AI/OCR confidence

## Monitoring

### Metrics Exposed

- `document.extraction.total` - Total extractions
- `document.extraction.success` - Successful extractions
- `document.extraction.failed` - Failed extractions
- `document.extraction.duration` - Processing time
- `document.ocr.confidence` - Average OCR confidence
- `document.quality.score` - Average quality score

### Health Check

```http
GET /actuator/health
```

Includes OCR health check (Tesseract availability).

## Troubleshooting

### Issue: Tesseract not found

**Solution**: Install Tesseract and set `TESSDATA_PREFIX` environment variable.

### Issue: Low OCR confidence

**Solutions**:
1. Enable image preprocessing
2. Increase image resolution (300+ DPI recommended)
3. Use higher quality scans
4. Try different page segmentation modes

### Issue: Missing language support

**Solution**: Install additional language packs:
```bash
sudo apt-get install tesseract-ocr-[lang]
```

### Issue: Slow extraction

**Solutions**:
1. Enable parallel processing
2. Reduce image resolution (if too high)
3. Enable caching
4. Use faster AI model

## Best Practices

1. **Image Quality**
   - Use 300+ DPI for scanning
   - Ensure good lighting and contrast
   - Avoid skewed/rotated images

2. **Document Preparation**
   - Remove handwritten annotations
   - Ensure text is clearly visible
   - Use original documents when possible

3. **Performance**
   - Process in batches when possible
   - Use appropriate threading configuration
   - Enable caching for frequently accessed documents

4. **Security**
   - Store API keys in environment variables
   - Use secure connections (HTTPS)
   - Implement access controls
   - Encrypt stored documents

## Future Enhancements

- [ ] Support for handwriting recognition
- [ ] Table extraction with structure preservation
- [ ] Multi-page document batching
- [ ] Custom model training for specific document types
- [ ] Real-time extraction progress tracking
- [ ] Advanced layout analysis
- [ ] Form field detection
- [ ] Signature and stamp detection

## References

- [Tesseract OCR Documentation](https://tesseract-ocr.github.io/)
- [Apache PDFBox](https://pdfbox.apache.org/)
- [Apache Tika](https://tika.apache.org/)
- [OpenAI GPT-4 Vision](https://platform.openai.com/docs/guides/vision)

