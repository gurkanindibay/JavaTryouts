# Observability Stack Validation Script

param(
    [int]$TimeoutSeconds = 300
)

function Test-ServiceEndpoint {
    param(
        [string]$Name,
        [string]$Url,
        [int]$ExpectedStatus = 200
    )
    
    try {
        Write-Host "Testing $Name..." -NoNewline
        $response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 10 -ErrorAction Stop
        
        if ($response.StatusCode -eq $ExpectedStatus) {
            Write-Host " ✅ OK" -ForegroundColor Green
            return $true
        } else {
            Write-Host " ❌ FAIL (Status: $($response.StatusCode))" -ForegroundColor Red
            return $false
        }
    } catch {
        Write-Host " ❌ FAIL (Error: $($_.Exception.Message))" -ForegroundColor Red
        return $false
    }
}

function Wait-ForService {
    param(
        [string]$Name,
        [string]$Url,
        [int]$TimeoutSeconds = 120
    )
    
    Write-Host "Waiting for $Name to be ready..." -ForegroundColor Yellow
    $startTime = Get-Date
    
    while ((Get-Date) -lt $startTime.AddSeconds($TimeoutSeconds)) {
        try {
            $response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 5 -ErrorAction Stop
            if ($response.StatusCode -eq 200) {
                Write-Host "$Name is ready! ✅" -ForegroundColor Green
                return $true
            }
        } catch {
            # Service not ready yet
        }
        
        Write-Host "." -NoNewline
        Start-Sleep -Seconds 5
    }
    
    Write-Host ""
    Write-Host "$Name did not become ready within $TimeoutSeconds seconds ❌" -ForegroundColor Red
    return $false
}

Write-Host "🔍 Observability Stack Validation" -ForegroundColor Cyan
Write-Host "=================================" -ForegroundColor Cyan
Write-Host ""

# Check if Docker is running
Write-Host "Checking Docker..." -NoNewline
try {
    docker version | Out-Null
    Write-Host " ✅ OK" -ForegroundColor Green
} catch {
    Write-Host " ❌ FAIL - Docker is not running" -ForegroundColor Red
    exit 1
}

# Check if stack is running
Write-Host "Checking stack status..." -NoNewline
$containers = docker-compose ps --services --filter "status=running" 2>$null
if ($containers) {
    Write-Host " ✅ OK" -ForegroundColor Green
} else {
    Write-Host " ❌ FAIL - Stack is not running" -ForegroundColor Red
    Write-Host "Run: .\manage-stack.ps1 start" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "🌐 Testing Service Endpoints" -ForegroundColor Cyan
Write-Host "=============================" -ForegroundColor Cyan

# Wait for core services to be ready
$coreServices = @(
    @{ Name = "Elasticsearch"; Url = "http://localhost:9200/_cluster/health" },
    @{ Name = "Prometheus"; Url = "http://localhost:9090/-/ready" },
    @{ Name = "Tempo"; Url = "http://localhost:3200/ready" }
)

foreach ($service in $coreServices) {
    Wait-ForService -Name $service.Name -Url $service.Url -TimeoutSeconds 60
}

Write-Host ""

# Test all service endpoints
$services = @(
    @{ Name = "Grafana"; Url = "http://localhost:3000/api/health" },
    @{ Name = "Prometheus"; Url = "http://localhost:9090/-/healthy" },
    @{ Name = "Kibana"; Url = "http://localhost:5601/api/status" },
    @{ Name = "Elasticsearch"; Url = "http://localhost:9200" },
    @{ Name = "Tempo"; Url = "http://localhost:3200/ready" },
    @{ Name = "AlertManager"; Url = "http://localhost:9093/-/healthy" },
    @{ Name = "Node Exporter"; Url = "http://localhost:9100/metrics" },
    @{ Name = "cAdvisor"; Url = "http://localhost:8080/metrics" },
    @{ Name = "OpenTelemetry Collector"; Url = "http://localhost:13133" }
)

$results = @()
foreach ($service in $services) {
    $result = Test-ServiceEndpoint -Name $service.Name -Url $service.Url
    $results += @{ Name = $service.Name; Status = $result }
}

Write-Host ""
Write-Host "📊 Results Summary" -ForegroundColor Cyan
Write-Host "==================" -ForegroundColor Cyan

$successCount = ($results | Where-Object { $_.Status -eq $true }).Count
$totalCount = $results.Count

foreach ($result in $results) {
    $status = if ($result.Status) { "✅ PASS" } else { "❌ FAIL" }
    $color = if ($result.Status) { "Green" } else { "Red" }
    Write-Host "$($result.Name.PadRight(25)) $status" -ForegroundColor $color
}

Write-Host ""
Write-Host "Overall: $successCount/$totalCount services healthy" -ForegroundColor $(if ($successCount -eq $totalCount) { "Green" } else { "Yellow" })

if ($successCount -eq $totalCount) {
    Write-Host ""
    Write-Host "🎉 All services are healthy! Your observability stack is ready." -ForegroundColor Green
    Write-Host ""
    Write-Host "🔗 Quick Links:" -ForegroundColor Cyan
    Write-Host "  • Grafana:      http://localhost:3000 (admin/admin123)" -ForegroundColor Yellow
    Write-Host "  • Prometheus:   http://localhost:9090" -ForegroundColor Yellow
    Write-Host "  • Kibana:       http://localhost:5601" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "📚 Next Steps:" -ForegroundColor Cyan
    Write-Host "  1. Instrument your Java applications" -ForegroundColor White
    Write-Host "  2. Create custom dashboards in Grafana" -ForegroundColor White
    Write-Host "  3. Set up alerting rules" -ForegroundColor White
    Write-Host "  4. Configure log parsing in Logstash" -ForegroundColor White
} else {
    Write-Host ""
    Write-Host "⚠️ Some services are not healthy. Check the logs:" -ForegroundColor Yellow
    Write-Host "   .\manage-stack.ps1 logs" -ForegroundColor White
    Write-Host ""
    Write-Host "🔧 Troubleshooting:" -ForegroundColor Cyan
    Write-Host "  1. Check Docker resources (8GB+ RAM recommended)" -ForegroundColor White
    Write-Host "  2. Verify no port conflicts" -ForegroundColor White
    Write-Host "  3. Check service dependencies" -ForegroundColor White
}
