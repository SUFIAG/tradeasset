# PowerShell script to clean the database
# This will drop all old schemas and tables

$dbHost = "localhost"
$dbPort = "5432"
$dbName = "tradeasset"
$dbUser = "postgres"
$dbPassword = "postgres"

Write-Host "Cleaning database $dbName..." -ForegroundColor Yellow

# Set password environment variable
$env:PGPASSWORD = $dbPassword

# Try to find PostgreSQL bin directory
$postgresqlPaths = @(
    "C:\Program Files\PostgreSQL\*\bin\psql.exe",
    "C:\Program Files (x86)\PostgreSQL\*\bin\psql.exe",
    "C:\PostgreSQL\*\bin\psql.exe"
)

$psqlPath = $null
foreach ($path in $postgresqlPaths) {
    $found = Get-ChildItem -Path $path -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($found) {
        $psqlPath = $found.FullName
        break
    }
}

if ($psqlPath) {
    Write-Host "Found psql at: $psqlPath" -ForegroundColor Green

    # Execute cleanup script
    & $psqlPath -h $dbHost -p $dbPort -U $dbUser -d $dbName -f "clean_database.sql"

    if ($LASTEXITCODE -eq 0) {
        Write-Host "`nDatabase cleaned successfully!" -ForegroundColor Green
        Write-Host "You can now run the application to create fresh schema with all tables in public schema." -ForegroundColor Cyan
    } else {
        Write-Host "`nError cleaning database. Exit code: $LASTEXITCODE" -ForegroundColor Red
    }
} else {
    Write-Host "PostgreSQL psql not found. Please execute clean_database.sql manually:" -ForegroundColor Red
    Write-Host "psql -h localhost -U postgres -d tradeasset -f clean_database.sql" -ForegroundColor Yellow
}

# Clear password
Remove-Item Env:\PGPASSWORD

