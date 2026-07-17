# Quick Start Guide - OCR Document Extraction

## Get Started in 5 Minutes

This guide will help you quickly set up and test the OCR document extraction functionality.

---

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker (optional, for containerized deployment)

---

## Step 1: Install Tesseract OCR

### Windows
1. Download installer from: https://github.com/UB-Mannheim/tesseract/wiki
2. Run installer and note the installation path (e.g., `C:\Program Files\Tesseract-OCR`)
3. Add to PATH or set `TESSDATA_PREFIX` environment variable

### macOS
```bash
brew install tesseract
brew install tesseract-lang  # For additional languages
```

### Linux (Ubuntu/Debian)
```bash
sudo apt-get update
sudo apt-get install -y tesseract-ocr tesseract-ocr-eng
```

### Verify Installation
```bash
tesseract --version
# Should show: tesseract 5.x.x
```

---

## Step 2: Configure Application

### Option A: Environment Variables (Recommended)
```bash
# Windows PowerShell
$env:OPENAI_API_KEY="your-openai-api-key-here"
$env:TESSDATA_PREFIX="C:\Program Files\Tesseract-OCR\tessdata"

# Linux/macOS
export OPENAI_API_KEY="your-openai-api-key-here"
export TESSDATA_PREFIX="/usr/share/tesseract-ocr/tessdata"
```

### Option B: Edit application.yml
```yaml
tradesphere:
  document:
    extraction:
      ocr:
        enabled: true
        tessdata-path: /usr/share/tesseract-ocr/tessdata  # Update this path
      ai:
        api-key: your-openai-api-key-here  # Or use ${OPENAI_API_KEY}
```

---

## Step 3: Build and Run

### Using Maven
```bash
# Navigate to project directory
cd tradeasset

# Build the project
mvn clean install -DskipTests

# Run the application
mvn spring-boot:run
```

### Using Docker
```bash
# Build Docker image
docker build -f Dockerfile.ocr -t tradesphere-ai .

# Run container
docker run -p 8080:8080 \
  -e OPENAI_API_KEY=your-key \
  tradesphere-ai
```

---

## Step 4: Test the API

### Check if Service is Running
```bash
# Windows PowerShell
Invoke-WebRequest http://localhost:8080/actuator/health

# Linux/macOS
curl http://localhost:8080/actuator/health
```

Expected response:
```json
{"status":"UP"}
```

### Test Document Extraction

#### Using cURL (Linux/macOS)
```bash
curl -X POST http://localhost:8080/api/documents/extraction/extract \
  -F "file=@/path/to/your/invoice.jpg" \
  -F "documentType=COMMERCIAL_INVOICE" \
  -H "Accept: application/json"
```

#### Using PowerShell (Windows)
```powershell
$filePath = "C:\path\to\your\invoice.jpg"
$uri = "http://localhost:8080/api/documents/extraction/extract"

$form = @{
    file = Get-Item -Path $filePath
    documentType = "COMMERCIAL_INVOICE"
}

Invoke-RestMethod -Uri $uri -Method Post -Form $form
```

#### Using Swagger UI
1. Open browser: http://localhost:8080/swagger-ui.html
2. Navigate to "Document Extraction" section
3. Try out the `/api/documents/extraction/extract` endpoint
4. Upload a document and click "Execute"

---

## Step 5: Verify Results

### Successful Response Example
```json
{
  "success": true,
  "extractedData": {
    "invoiceNumber": "INV-2024-001",
    "invoiceDate": "2024-07-15",
    "seller": "ABC Trading Company",
    "buyer": "XYZ Imports Ltd",
    "amount": 50000.00,
    "currency": "USD",
    "items": [
      {
        "description": "Electronic Components",
        "quantity": 1000,
        "unitPrice": 50.00,
        "total": 50000.00
      }
    ]
  },
  "confidence": 0.92,
  "model": "gpt-4-vision",
  "qualityScore": {
    "overallScore": 0.88,
    "qualityLevel": "GOOD",
    "completenessScore": 0.95,
    "validityScore": 0.90,
    "requiresReview": false
  }
}
```

---

## Troubleshooting

### Issue: "Tesseract not found"

**Solution**:
1. Verify Tesseract is installed: `tesseract --version`
2. Set `TESSDATA_PREFIX` environment variable
3. Update `tessdata-path` in `application.yml`

### Issue: "OCR extraction failed"

**Possible causes**:
1. Image quality too low (use higher resolution)
2. Wrong language setting (check `language` config)
3. Unsupported image format (use JPEG, PNG, TIFF)

**Solutions**:
1. Enable preprocessing: `preprocessing-enabled: true`
2. Try different page segmentation mode (1, 3, or 6)
3. Convert image to supported format

### Issue: "AI extraction failed"

**Possible causes**:
1. Invalid API key
2. API quota exceeded
3. Network connectivity

**Solutions**:
1. Verify API key is set correctly
2. Check OpenAI dashboard for quota
3. Enable stub mode for testing (AI disabled)

### Issue: Low extraction confidence

**Solutions**:
1. Use higher quality scans (300+ DPI)
2. Enable image preprocessing
3. Ensure good lighting/contrast
4. Remove handwritten annotations

---

## Testing with Sample Documents

### Create Test Invoice (Save as test_invoice.txt)
```
COMMERCIAL INVOICE

Invoice No: INV-2024-TEST-001
Date: 2024-07-17

Seller: Test Trading Company
Address: 123 Export Street, New York, NY 10001

Buyer: Sample Imports Ltd
Address: 456 Import Avenue, London, UK

Description: Electronic Components
Quantity: 500 units
Unit Price: USD 100.00
Total Amount: USD 50,000.00

Payment Terms: Letter of Credit at sight
Incoterm: FOB New York
```

### Test with Plain Text
```bash
curl -X POST http://localhost:8080/api/documents/extraction/extract \
  -F "file=@test_invoice.txt" \
  -F "documentType=COMMERCIAL_INVOICE"
```

---

## Quick Configuration Reference

### Minimum Configuration (Development)
```yaml
tradesphere:
  document:
    extraction:
      ocr:
        enabled: true
      ai:
        enabled: false  # Use stub for testing
```

### Production Configuration
```yaml
tradesphere:
  document:
    extraction:
      ocr:
        enabled: true
        tessdata-path: /usr/share/tesseract-ocr/tessdata
        language: eng
        preprocessing-enabled: true
      ai:
        enabled: true
        provider: openai
        model: gpt-4-vision-preview
        api-key: ${OPENAI_API_KEY}
      quality:
        min-auto-approval-score: 0.85
```

---

## Next Steps

1. **Test with Real Documents**
   - Try different document types (Invoice, Bill of Lading, etc.)
   - Test with various image formats (JPEG, PNG, PDF)
   - Test with different quality levels

2. **Explore API Documentation**
   - Visit: http://localhost:8080/swagger-ui.html
   - Try all available endpoints
   - Review response schemas

3. **Monitor Performance**
   - Check metrics: http://localhost:8080/actuator/metrics
   - Monitor processing times
   - Analyze quality scores

4. **Integrate with Your Application**
   - Use the REST API from your frontend
   - Implement webhook for async processing
   - Add custom validation rules

---

## Useful Commands

### Maven
```bash
# Clean build
mvn clean install

# Run tests
mvn test

# Skip tests
mvn clean install -DskipTests

# Run specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Docker
```bash
# Build image
docker build -f Dockerfile.ocr -t tradesphere-ai:latest .

# Run with logs
docker run -p 8080:8080 tradesphere-ai:latest

# Run in background
docker run -d -p 8080:8080 tradesphere-ai:latest

# View logs
docker logs -f <container-id>

# Stop container
docker stop <container-id>
```

### Health Checks
```bash
# Application health
curl http://localhost:8080/actuator/health

# Supported document types
curl http://localhost:8080/api/documents/extraction/supported

# Check specific type support
curl http://localhost:8080/api/documents/extraction/supported/COMMERCIAL_INVOICE
```

---

## Support

- **Documentation**: See `OCR_INTEGRATION_README.md` for detailed documentation
- **Implementation Summary**: See `IMPLEMENTATION_SUMMARY_OCR.md` for technical details
- **Issues**: Check logs in `logs/` directory or use `docker logs`

---

## Quick Reference: Supported Document Types

- `COMMERCIAL_INVOICE` - Commercial invoices
- `BILL_OF_LADING` - Bill of lading documents
- `PACKING_LIST` - Packing lists
- `CERTIFICATE_OF_ORIGIN` - Certificates of origin
- `INSURANCE_CERTIFICATE` - Insurance certificates
- `PASSPORT` - Passport documents
- `NATIONAL_ID` - National ID cards
- `BUSINESS_LICENSE` - Business licenses

---

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `OPENAI_API_KEY` | OpenAI API key for AI extraction | None (required) |
| `TESSDATA_PREFIX` | Tesseract data directory | System default |
| `SPRING_PROFILES_ACTIVE` | Spring profile to activate | dev |
| `SERVER_PORT` | Application port | 8080 |

---

**You're all set! 🚀**

Start by testing with the Swagger UI at http://localhost:8080/swagger-ui.html

