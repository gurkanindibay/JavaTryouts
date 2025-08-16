# Test script to send observability metric events to Kafka and verify persistence with duplicate book testing

Write-Host "Testing Observability Metrics Persistence with Duplicate Book Scenarios..." -ForegroundColor Green

# Test URL
$baseUrl = "http://localhost:8081"

Write-Host "`n1. Starting services first..." -ForegroundColor Yellow
try {
    docker compose up -d 2>$null
    Write-Host "Services starting..." -ForegroundColor Green
    Start-Sleep -Seconds 15
} catch {
    Write-Host "Error starting services: $_" -ForegroundColor Red
}

Write-Host "`n2. Checking application health..." -ForegroundColor Yellow
$maxAttempts = 10
$attempt = 1
$healthy = $false

while ($attempt -le $maxAttempts -and -not $healthy) {
    try {
        $healthResponse = Invoke-RestMethod -Uri "$baseUrl/actuator/health" -Method GET -TimeoutSec 5
        if ($healthResponse.status -eq "UP") {
            Write-Host "Application is healthy: $($healthResponse.status)" -ForegroundColor Green
            $healthy = $true
        }
    } catch {
        Write-Host "Attempt $attempt/$maxAttempts - Application not ready yet..." -ForegroundColor Yellow
        Start-Sleep -Seconds 3
        $attempt++
    }
}

if (-not $healthy) {
    Write-Host "Application failed to start properly" -ForegroundColor Red
    exit 1
}

Write-Host "`n3. Testing duplicate book creation to trigger observability metrics..." -ForegroundColor Yellow

# Create a test book multiple times to trigger duplicate detection
$testBook = @{
    title = "Test Book for Observability Metrics"
    author = "Test Author"
    isbn = "TEST-123-OBSERVABILITY"
    publicationYear = 2025
    genre = "TEST"
    availableCopies = 5
} | ConvertTo-Json

Write-Host "Creating test book for the first time..." -ForegroundColor Cyan
try {
    $response1 = Invoke-WebRequest -Uri "$baseUrl/api/books" -Method POST -Body $testBook -ContentType "application/json" -TimeoutSec 10
    Write-Host "First creation: Status $($response1.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "First creation response: $($_.Exception.Response.StatusCode) - $($_.Exception.Message)" -ForegroundColor Yellow
}

Start-Sleep -Seconds 2

Write-Host "Attempting to create the same book again (should trigger duplicate detection)..." -ForegroundColor Cyan
try {
    $response2 = Invoke-WebRequest -Uri "$baseUrl/api/books" -Method POST -Body $testBook -ContentType "application/json" -TimeoutSec 10
    Write-Host "Second creation: Status $($response2.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "Second creation response: $($_.Exception.Response.StatusCode) - $($_.Exception.Message)" -ForegroundColor Yellow
    Write-Host "This is expected if duplicate detection is working!" -ForegroundColor Cyan
}

Start-Sleep -Seconds 2

Write-Host "Attempting to create the same book a third time..." -ForegroundColor Cyan
try {
    $response3 = Invoke-WebRequest -Uri "$baseUrl/api/books" -Method POST -Body $testBook -ContentType "application/json" -TimeoutSec 10
    Write-Host "Third creation: Status $($response3.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "Third creation response: $($_.Exception.Response.StatusCode) - $($_.Exception.Message)" -ForegroundColor Yellow
    Write-Host "This is expected if duplicate detection is working!" -ForegroundColor Cyan
}

Write-Host "`n4. Triggering other API operations to generate observability metrics..." -ForegroundColor Yellow

# Trigger some API calls that should generate observability metrics
$testRequests = @(
    @{ Method = "GET"; Uri = "$baseUrl/api/books"; Description = "Get books list" },
    @{ Method = "GET"; Uri = "$baseUrl/api/books/999"; Description = "Get non-existent book (should trigger failure)" },
    @{ Method = "POST"; Uri = "$baseUrl/api/books/1/borrow"; Body = '{"userId": 999}'; Description = "Invalid borrow attempt" }
)

foreach ($request in $testRequests) {
    try {
        Write-Host "  - $($request.Description)..." -ForegroundColor Cyan
        if ($request.Body) {
            $response = Invoke-WebRequest -Uri $request.Uri -Method $request.Method -Body $request.Body -ContentType "application/json" -TimeoutSec 10
        } else {
            $response = Invoke-WebRequest -Uri $request.Uri -Method $request.Method -TimeoutSec 10
        }
        Write-Host "    Status: $($response.StatusCode)" -ForegroundColor Green
    } catch {
        Write-Host "    Error: $($_.Exception.Response.StatusCode) - $($_.Exception.Message)" -ForegroundColor Yellow
    }
    Start-Sleep -Milliseconds 1000
}

Write-Host "`n5. Waiting for Kafka processing and observability metrics to be updated..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

Write-Host "`n6. Checking application logs for observability metric processing..." -ForegroundColor Yellow
Write-Host "=== Recent Application Logs ===" -ForegroundColor Cyan
docker logs collections-kafka-app --tail=30 | Select-String -Pattern "ObservabilityMetrics|COUNTER|GAUGE|INCREMENT|duplicate|test\." | ForEach-Object { Write-Host $_ -ForegroundColor White }

Write-Host "`n7. Checking for any observability test runner output..." -ForegroundColor Yellow
Write-Host "=== Test Runner Logs ===" -ForegroundColor Cyan
docker logs collections-kafka-app | Select-String -Pattern "STARTING OBSERVABILITY|COMPLETED|Sent:" | ForEach-Object { Write-Host $_ -ForegroundColor White }

Write-Host "`n8. Checking Kafka container logs for topic activity..." -ForegroundColor Yellow
Write-Host "=== Kafka Logs (observability-metrics topic) ===" -ForegroundColor Cyan
docker logs kafka --tail=20 | Select-String -Pattern "observability-metrics" | ForEach-Object { Write-Host $_ -ForegroundColor White }

Write-Host "`nTest completed!" -ForegroundColor Green
Write-Host "Summary:" -ForegroundColor Blue
Write-Host "- Attempted to create duplicate books to trigger observability metrics" -ForegroundColor Blue
Write-Host "- Triggered various API operations for metric generation" -ForegroundColor Blue
Write-Host "- Check the logs above for observability metric updates" -ForegroundColor Blue
Write-Host "- Check Grafana dashboards at http://localhost:3000 if the observability stack is running" -ForegroundColor Blue
Write-Host "- Check Kafka UI at http://localhost:8085 to see message activity" -ForegroundColor Blue