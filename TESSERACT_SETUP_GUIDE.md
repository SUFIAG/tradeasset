# Tesseract OCR Installation & Setup Guide

## Overview
This guide provides instructions for installing and configuring Tesseract OCR for the TradeSphere AI Document Processing system.

## Table of Contents
1. [Windows Installation](#windows-installation)
2. [Linux Installation](#linux-installation)
3. [Docker Installation](#docker-installation)
4. [Configuration](#configuration)
5. [Testing](#testing)
6. [Troubleshooting](#troubleshooting)

---

## Windows Installation

### Method 1: Using Installer (Recommended)

1. **Download Tesseract Installer**
   ```
   https://github.com/UB-Mannheim/tesseract/wiki
   ```
   Download the latest installer (e.g., `tesseract-ocr-w64-setup-5.3.1.exe`)

2. **Run Installer**
   - Execute the downloaded installer
   - Choose installation directory (e.g., `C:\Program Files\Tesseract-OCR`)
   - Select language data files (at minimum: English, or all for multi-language support)
   - Complete installation

3. **Add to System PATH**
   ```powershell
   # Add Tesseract to PATH
   $tesseractPath = "C:\Program Files\Tesseract-OCR"
   [Environment]::SetEnvironmentVariable("Path", $env:Path + ";$tesseractPath", [EnvironmentVariableTarget]::Machine)
   ```

4. **Verify Installation**
   ```powershell
   tesseract --version
   ```
   Expected output:
   ```
   tesseract 5.3.1
    leptonica-1.82.0
   ```

### Method 2: Using Chocolatey

```powershell
# Install Chocolatey (if not installed)
Set-ExecutionPolicy Bypass -Scope Process -Force
[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Install Tesseract
choco install tesseract -y

# Verify
tesseract --version
```

---

## Linux Installation

### Ubuntu/Debian

```bash
# Update package list
sudo apt-get update

# Install Tesseract and language packs
sudo apt-get install -y tesseract-ocr tesseract-ocr-eng

# Install additional languages (optional)
sudo apt-get install -y tesseract-ocr-fra tesseract-ocr-deu tesseract-ocr-chi-sim

# Verify installation
tesseract --version
which tesseract
```

### CentOS/RHEL

```bash
# Enable EPEL repository
sudo yum install -y epel-release

# Install Tesseract
sudo yum install -y tesseract tesseract-langpack-eng

# Verify
tesseract --version
```

### Alpine Linux (for Docker)

```bash
# Install Tesseract
apk add --no-cache tesseract-ocr tesseract-ocr-data-eng

# Verify
tesseract --version
```

---

## Docker Installation

### Update Dockerfile

Add to your `Dockerfile.ocr` or main `Dockerfile`:

```dockerfile
FROM eclipse-temurin:17-jdk-alpine

# Install Tesseract OCR
RUN apk add --no-cache \
    tesseract-ocr \
    tesseract-ocr-data-eng \
    tesseract-ocr-data-fra \
    tesseract-ocr-data-deu \
    tesseract-ocr-data-chi_sim \
    tesseract-ocr-data-ara

# Verify installation
RUN tesseract --version

# Copy application
WORKDIR /app
COPY target/*.jar app.jar

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Build and Test

```bash
# Build Docker image
docker build -t tradeasset-ocr -f Dockerfile.ocr .

# Test Tesseract in container
docker run --rm tradeasset-ocr tesseract --version

# Run container
docker-compose up -d
```

---

## Configuration

### Application Configuration

Update `application.yml` with Tesseract settings:

```yaml
document:
  ocr:
    enabled: true
    engine: tesseract
    tesseract:
      # Windows
      path: "C:\\Program Files\\Tesseract-OCR\\tesseract.exe"
      # Linux/Docker
      # path: "/usr/bin/tesseract"
      
      data-path: "C:\\Program Files\\Tesseract-OCR\\tessdata"
      # Linux/Docker
      # data-path: "/usr/share/tesseract-ocr/5/tessdata"
      
      language: "eng"
      additional-languages:
        - fra
        - deu
        - chi_sim
      
      # OCR Engine Mode
      # 0 = Legacy engine only
      # 1 = Neural nets LSTM engine only
      # 2 = Legacy + LSTM engines
      # 3 = Default, based on what is available
      oem: 3
      
      # Page Segmentation Mode
      # 0 = Orientation and script detection (OSD) only
      # 1 = Automatic page segmentation with OSD
      # 3 = Fully automatic page segmentation, but no OSD (Default)
      # 6 = Assume a single uniform block of text
      # 11 = Sparse text. Find as much text as possible in no particular order
      psm: 3
      
      # Image preprocessing
      preprocessing:
        enabled: true
        deskew: true
        denoise: true
        sharpen: true
        threshold: true
        
      # Performance
      timeout-seconds: 30
      max-parallel: 4
```

### Environment Variables

```bash
# Set Tesseract path
export TESSDATA_PREFIX=/usr/share/tesseract-ocr/5/tessdata

# For Windows
set TESSDATA_PREFIX=C:\Program Files\Tesseract-OCR\tessdata
```

---

## Testing

### Test Script (PowerShell)

Create `test-ocr.ps1`:

```powershell
# Test Tesseract OCR Installation
Write-Host "Testing Tesseract OCR Installation..." -ForegroundColor Green

# Check if Tesseract is in PATH
$tesseractExe = Get-Command tesseract -ErrorAction SilentlyContinue

if ($tesseractExe) {
    Write-Host "✓ Tesseract found at: $($tesseractExe.Source)" -ForegroundColor Green
    
    # Get version
    $version = tesseract --version 2>&1 | Select-Object -First 1
    Write-Host "✓ Version: $version" -ForegroundColor Green
    
    # List available languages
    Write-Host "`nAvailable Languages:" -ForegroundColor Yellow
    tesseract --list-langs
    
    # Test OCR on sample image (if available)
    if (Test-Path "sample-document.jpg") {
        Write-Host "`nTesting OCR on sample document..." -ForegroundColor Yellow
        tesseract sample-document.jpg output -l eng
        
        if (Test-Path "output.txt") {
            Write-Host "✓ OCR test successful! Output saved to output.txt" -ForegroundColor Green
            Get-Content output.txt
        }
    }
    
} else {
    Write-Host "✗ Tesseract not found in PATH" -ForegroundColor Red
    Write-Host "Please install Tesseract and add it to your system PATH" -ForegroundColor Yellow
}
```

### Test Script (Bash)

Create `test-ocr.sh`:

```bash
#!/bin/bash

echo "Testing Tesseract OCR Installation..."

# Check if Tesseract is installed
if command -v tesseract &> /dev/null; then
    echo "✓ Tesseract found at: $(which tesseract)"
    
    # Get version
    echo "✓ Version: $(tesseract --version 2>&1 | head -n 1)"
    
    # List available languages
    echo -e "\nAvailable Languages:"
    tesseract --list-langs
    
    # Test OCR on sample image (if available)
    if [ -f "sample-document.jpg" ]; then
        echo -e "\nTesting OCR on sample document..."
        tesseract sample-document.jpg output -l eng
        
        if [ -f "output.txt" ]; then
            echo "✓ OCR test successful! Output:"
            cat output.txt
        fi
    fi
    
else
    echo "✗ Tesseract not found"
    echo "Please install Tesseract OCR"
fi
```

### Java Integration Test

```java
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class TesseractTest {
    public static void main(String[] args) {
        try {
            Tesseract tesseract = new Tesseract();
            
            // Windows
            tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
            // Linux
            // tesseract.setDatapath("/usr/share/tesseract-ocr/5/tessdata");
            
            tesseract.setLanguage("eng");
            tesseract.setOcrEngineMode(1); // LSTM only
            tesseract.setPageSegMode(3);   // Auto
            
            String result = tesseract.doOCR(new File("test-image.jpg"));
            System.out.println("OCR Result:");
            System.out.println(result);
            
        } catch (TesseractException e) {
            System.err.println("Tesseract error: " + e.getMessage());
        }
    }
}
```

---

## Troubleshooting

### Common Issues

#### 1. "tesseract: command not found"

**Solution:**
```powershell
# Windows - Add to PATH
$env:Path += ";C:\Program Files\Tesseract-OCR"

# Linux - Reinstall
sudo apt-get install --reinstall tesseract-ocr
```

#### 2. "Error opening data file"

**Solution:**
```bash
# Check tessdata location
tesseract --version | grep "tessdata"

# Set environment variable
export TESSDATA_PREFIX=/usr/share/tesseract-ocr/5/tessdata
```

#### 3. Poor OCR accuracy

**Solutions:**
- Improve image quality (resolution, contrast)
- Use image preprocessing
- Try different Page Segmentation Mode (PSM)
- Ensure correct language is selected

#### 4. Java "UnsatisfiedLinkError"

**Solution:**
Add tess4j dependency to `pom.xml`:
```xml
<dependency>
    <groupId>net.sourceforge.tess4j</groupId>
    <artifactId>tess4j</artifactId>
    <version>5.7.0</version>
</dependency>
```

#### 5. Slow OCR processing

**Solutions:**
- Enable multi-threading
- Reduce image resolution (300 DPI is optimal)
- Use LSTM engine only (OEM 1)
- Implement caching

---

## Language Packs

### Download Additional Languages

**Windows:**
```powershell
# Download from GitHub
$langs = @("fra", "deu", "chi_sim", "ara", "spa")
$tessdata = "C:\Program Files\Tesseract-OCR\tessdata"

foreach ($lang in $langs) {
    $url = "https://github.com/tesseract-ocr/tessdata/raw/main/$lang.traineddata"
    Invoke-WebRequest -Uri $url -OutFile "$tessdata\$lang.traineddata"
}
```

**Linux:**
```bash
# Install language packs
sudo apt-get install -y \
    tesseract-ocr-fra \
    tesseract-ocr-deu \
    tesseract-ocr-spa \
    tesseract-ocr-chi-sim \
    tesseract-ocr-ara
```

---

## Performance Optimization

### Preprocessing Images

```java
// Optimal preprocessing for better accuracy
BufferedImage preprocessImage(BufferedImage image) {
    // 1. Convert to grayscale
    // 2. Increase contrast
    // 3. Remove noise
    // 4. Deskew (straighten)
    // 5. Binarize (black & white)
    return processedImage;
}
```

### Batch Processing

```java
// Process multiple images in parallel
ExecutorService executor = Executors.newFixedThreadPool(4);
List<Future<String>> futures = new ArrayList<>();

for (File image : images) {
    Future<String> future = executor.submit(() -> 
        tesseract.doOCR(image)
    );
    futures.add(future);
}

// Collect results
for (Future<String> future : futures) {
    String text = future.get();
}
```

---

## Production Deployment Checklist

- [ ] Tesseract installed on all servers
- [ ] Language packs installed for required languages
- [ ] TESSDATA_PREFIX environment variable set
- [ ] Application configured with correct paths
- [ ] Image preprocessing enabled
- [ ] Caching implemented for processed documents
- [ ] Monitoring and logging configured
- [ ] Performance tested with production-like load
- [ ] Error handling implemented
- [ ] Backup OCR service configured (fallback)

---

## Support

For issues or questions:
- Tesseract GitHub: https://github.com/tesseract-ocr/tesseract
- Tesseract Wiki: https://github.com/tesseract-ocr/tesseract/wiki
- Tess4J GitHub: https://github.com/nguyenq/tess4j

---

## Version Information

- **Tesseract Version:** 5.3.1 (recommended)
- **Tess4J Version:** 5.7.0
- **Last Updated:** 2026-07-17

