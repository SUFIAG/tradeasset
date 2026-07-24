# DEBUG LOGIN ISSUE - Test Script
# Wait for backend to fully start (look for "Started AssetApplication" in the other window)
# Then run these commands:

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "STEP 1: Check if backend is running" -ForegroundColor Yellow
Write-Host "========================================`n" -ForegroundColor Cyan

try {
    $health = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method Get
    Write-Host "✓ Backend is running!" -ForegroundColor Green
    Write-Host "Status: $($health.status)`n" -ForegroundColor Green
} catch {
    Write-Host "✗ Backend is NOT running or not healthy" -ForegroundColor Red
    Write-Host "Please wait for 'Started AssetApplication' message in the other window`n" -ForegroundColor Yellow
    exit
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "STEP 2: Debug admin user in database" -ForegroundColor Yellow
Write-Host "========================================`n" -ForegroundColor Cyan

try {
    $debugUrl = "http://localhost:8080/api/v1/auth/dev/debug-user/admin?testPassword=admin123"
    $debug = Invoke-RestMethod -Uri $debugUrl -Method Get

    Write-Host "User Found: $($debug.found)" -ForegroundColor $(if ($debug.found) { "Green" } else { "Red" })

    if ($debug.found) {
        Write-Host "Username: $($debug.username)" -ForegroundColor White
        Write-Host "Email: $($debug.email)" -ForegroundColor White
        Write-Host "Status: $($debug.status)" -ForegroundColor White
        Write-Host "Is Locked: $($debug.isLocked)" -ForegroundColor White
        Write-Host "Password Hash (first 30 chars): $($debug.passwordHashPrefix)" -ForegroundColor White
        Write-Host "Password Hash Length: $($debug.passwordHashLength)" -ForegroundColor White
        Write-Host "`nTesting password 'admin123':" -ForegroundColor Yellow
        Write-Host "Password Matches: $($debug.passwordMatches)" -ForegroundColor $(if ($debug.passwordMatches) { "Green" } else { "Red" })

        if (-not $debug.passwordMatches) {
            Write-Host "`n✗ PASSWORD DOES NOT MATCH!" -ForegroundColor Red
            Write-Host "This means the password hash in database is incorrect." -ForegroundColor Red
            Write-Host "`nExpected hash prefix: `$2a`$10`$N9qo8uLOickgx2ZMRZoMye" -ForegroundColor Yellow
            Write-Host "Actual hash prefix:   $($debug.passwordHashPrefix)" -ForegroundColor Yellow
            Write-Host "`nYou need to run the SQL fix again in PostgreSQL:" -ForegroundColor Yellow
            Write-Host "UPDATE users SET password_hash = '`$2a`$10`$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' WHERE username = 'admin';" -ForegroundColor Cyan
        } else {
            Write-Host "`n✓ PASSWORD MATCHES CORRECTLY!" -ForegroundColor Green
            Write-Host "The database is correct. The issue might be elsewhere." -ForegroundColor Green
        }
    } else {
        Write-Host "✗ User 'admin' not found in database!" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Error calling debug endpoint: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "STEP 3: Try actual login" -ForegroundColor Yellow
Write-Host "========================================`n" -ForegroundColor Cyan

try {
    $loginUrl = "http://localhost:8080/api/v1/auth/login"
    $loginBody = @{
        username = "admin"
        password = "admin123"
        deviceFingerprint = "test-device"
    } | ConvertTo-Json

    $loginResponse = Invoke-RestMethod -Uri $loginUrl -Method Post -Body $loginBody -ContentType "application/json"

    Write-Host "✓ LOGIN SUCCESSFUL!" -ForegroundColor Green
    Write-Host "Access Token: $($loginResponse.accessToken.Substring(0, 50))..." -ForegroundColor Green
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    Write-Host "✗ LOGIN FAILED" -ForegroundColor Red
    Write-Host "Status Code: $statusCode" -ForegroundColor Red

    if ($_.ErrorDetails.Message) {
        $errorJson = $_.ErrorDetails.Message | ConvertFrom-Json
        Write-Host "Error Message: $($errorJson.message)" -ForegroundColor Red
    }
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "DIAGNOSTIC COMPLETE" -ForegroundColor Yellow
Write-Host "========================================`n" -ForegroundColor Cyan

