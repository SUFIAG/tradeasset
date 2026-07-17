# TradeSphere AI - Startup Script for Windows
# This script starts all required infrastructure and the application

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   TradeSphere AI - Starting Up" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Check if Docker is running
Write-Host "[1/5] Checking Docker..." -ForegroundColor Yellow
$dockerRunning = docker info 2>&1 | Out-Null
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Docker is not running. Please start Docker Desktop and try again." -ForegroundColor Red
    exit 1
}
Write-Host "✓ Docker is running" -ForegroundColor Green
Write-Host ""

# Start infrastructure services
Write-Host "[2/5] Starting infrastructure services..." -ForegroundColor Yellow
Write-Host "This may take 1-2 minutes for first-time setup..." -ForegroundColor Gray
docker-compose up -d

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Failed to start infrastructure services" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Infrastructure services started" -ForegroundColor Green
Write-Host ""

# Wait for services to be healthy
Write-Host "[3/5] Waiting for services to be healthy..." -ForegroundColor Yellow
Write-Host "Waiting 30 seconds for services to initialize..." -ForegroundColor Gray
Start-Sleep -Seconds 30

# Check PostgreSQL
$retries = 0
$maxRetries = 10
while ($retries -lt $maxRetries) {
    $pgReady = docker exec tradesphere-postgres pg_isready -U tradesphere 2>&1
    if ($pgReady -match "accepting connections") {
        Write-Host "✓ PostgreSQL is ready" -ForegroundColor Green
        break
    }
    $retries++
    if ($retries -eq $maxRetries) {
        Write-Host "WARNING: PostgreSQL may not be fully ready" -ForegroundColor Yellow
    }
    Start-Sleep -Seconds 2
}

# Check Redis
$redisReady = docker exec tradesphere-redis redis-cli ping 2>&1
if ($redisReady -match "PONG") {
    Write-Host "✓ Redis is ready" -ForegroundColor Green
} else {
    Write-Host "WARNING: Redis may not be fully ready" -ForegroundColor Yellow
}

Write-Host ""

# Build the application
Write-Host "[4/5] Building application..." -ForegroundColor Yellow
mvn clean install -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Build failed" -ForegroundColor Red
    Write-Host "Please check the error messages above" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Application built successfully" -ForegroundColor Green
Write-Host ""

# Start the application
Write-Host "[5/5] Starting TradeSphere AI..." -ForegroundColor Yellow
Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   Application is starting..." -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Access Points:" -ForegroundColor Green
Write-Host "  • API: http://localhost:8080" -ForegroundColor White
Write-Host "  • Swagger UI: http://localhost:8080/swagger-ui.html" -ForegroundColor White
Write-Host "  • Health: http://localhost:8080/actuator/health" -ForegroundColor White
Write-Host ""
Write-Host "Infrastructure:" -ForegroundColor Green
Write-Host "  • Kafka UI: http://localhost:8090" -ForegroundColor White
Write-Host "  • MinIO Console: http://localhost:9001" -ForegroundColor White
Write-Host "  • Kibana: http://localhost:5601" -ForegroundColor White
Write-Host "  • Grafana: http://localhost:3000 (admin/admin)" -ForegroundColor White
Write-Host ""
Write-Host "Press Ctrl+C to stop the application" -ForegroundColor Gray
Write-Host ""

mvn spring-boot:run

