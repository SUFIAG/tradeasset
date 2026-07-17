# TradeSphere AI - Shutdown Script for Windows
# This script stops all infrastructure services

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   TradeSphere AI - Shutting Down" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Stopping infrastructure services..." -ForegroundColor Yellow
docker-compose down

if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ All services stopped successfully" -ForegroundColor Green
} else {
    Write-Host "WARNING: Some services may not have stopped cleanly" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "To remove all data volumes, run:" -ForegroundColor Gray
Write-Host "  docker-compose down -v" -ForegroundColor White
Write-Host ""

