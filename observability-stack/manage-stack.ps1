# PowerShell script to manage the observability stack

param(
    [Parameter(Mandatory=$true)]
    [ValidateSet("start", "stop", "restart", "status", "logs", "cleanup", "backup")]
    [string]$Action,
    
    [string]$Service = "",
    [switch]$Follow = $false
)

$ComposeFile = "docker-compose.yml"

function Write-ColorOutput {
    param(
        [string]$Message,
        [string]$Color = "White"
    )
    Write-Host $Message -ForegroundColor $Color
}

function Start-Stack {
    Write-ColorOutput "🚀 Starting observability stack..." "Green"
    docker-compose up -d
    
    if ($LASTEXITCODE -eq 0) {
        Write-ColorOutput "✅ Stack started successfully!" "Green"
        Write-ColorOutput ""
        Write-ColorOutput "📊 Access URLs:" "Cyan"
        Write-ColorOutput "  • Grafana:      http://localhost:3000 (admin/admin123)" "Yellow"
        Write-ColorOutput "  • Prometheus:   http://localhost:9090" "Yellow"
        Write-ColorOutput "  • Kibana:       http://localhost:5601" "Yellow"
        Write-ColorOutput "  • Elasticsearch: http://localhost:9200" "Yellow"
        Write-ColorOutput "  • Tempo:        http://localhost:3200" "Yellow"
        Write-ColorOutput "  • AlertManager: http://localhost:9093" "Yellow"
    } else {
        Write-ColorOutput "❌ Failed to start stack" "Red"
    }
}

function Stop-Stack {
    Write-ColorOutput "🛑 Stopping observability stack..." "Yellow"
    docker-compose down
    
    if ($LASTEXITCODE -eq 0) {
        Write-ColorOutput "✅ Stack stopped successfully!" "Green"
    } else {
        Write-ColorOutput "❌ Failed to stop stack" "Red"
    }
}

function Restart-Stack {
    if ($Service) {
        Write-ColorOutput "🔄 Restarting service: $Service..." "Yellow"
        docker-compose restart $Service
    } else {
        Write-ColorOutput "🔄 Restarting entire stack..." "Yellow"
        docker-compose restart
    }
    
    if ($LASTEXITCODE -eq 0) {
        Write-ColorOutput "✅ Restart completed!" "Green"
    } else {
        Write-ColorOutput "❌ Restart failed" "Red"
    }
}

function Show-Status {
    Write-ColorOutput "📋 Stack Status:" "Cyan"
    docker-compose ps
    
    Write-ColorOutput ""
    Write-ColorOutput "💾 Volume Usage:" "Cyan"
    docker system df
}

function Show-Logs {
    if ($Service) {
        Write-ColorOutput "📜 Showing logs for: $Service" "Cyan"
        if ($Follow) {
            docker-compose logs -f $Service
        } else {
            docker-compose logs --tail=100 $Service
        }
    } else {
        Write-ColorOutput "📜 Showing logs for all services" "Cyan"
        if ($Follow) {
            docker-compose logs -f
        } else {
            docker-compose logs --tail=20
        }
    }
}

function Cleanup-Stack {
    Write-ColorOutput "🧹 This will remove all containers, networks, and volumes!" "Red"
    Write-ColorOutput "⚠️  All metrics, logs, and traces will be permanently deleted!" "Red"
    
    $confirmation = Read-Host "Are you sure? Type 'yes' to continue"
    
    if ($confirmation -eq "yes") {
        Write-ColorOutput "🗑️ Cleaning up observability stack..." "Yellow"
        docker-compose down -v --remove-orphans
        
        if ($LASTEXITCODE -eq 0) {
            Write-ColorOutput "✅ Cleanup completed!" "Green"
        } else {
            Write-ColorOutput "❌ Cleanup failed" "Red"
        }
    } else {
        Write-ColorOutput "❌ Cleanup cancelled" "Yellow"
    }
}

function Backup-Data {
    $BackupDir = "backup"
    $Timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
    
    Write-ColorOutput "💾 Creating backup directory..." "Cyan"
    if (!(Test-Path $BackupDir)) {
        New-Item -ItemType Directory -Path $BackupDir | Out-Null
    }
    
    Write-ColorOutput "📦 Backing up Prometheus data..." "Cyan"
    docker run --rm -v "observability-stack_prometheus_data:/data" -v "${PWD}/backup:/backup" alpine tar czf "/backup/prometheus_${Timestamp}.tar.gz" -C /data .
    
    Write-ColorOutput "📦 Backing up Grafana data..." "Cyan"
    docker run --rm -v "observability-stack_grafana_data:/data" -v "${PWD}/backup:/backup" alpine tar czf "/backup/grafana_${Timestamp}.tar.gz" -C /data .
    
    Write-ColorOutput "📦 Backing up Elasticsearch data..." "Cyan"
    docker run --rm -v "observability-stack_elasticsearch_data:/data" -v "${PWD}/backup:/backup" alpine tar czf "/backup/elasticsearch_${Timestamp}.tar.gz" -C /data .
    
    Write-ColorOutput "✅ Backup completed! Files saved in ./backup/" "Green"
    Get-ChildItem -Path $BackupDir -Filter "*${Timestamp}*"
}

# Main execution
switch ($Action) {
    "start" { Start-Stack }
    "stop" { Stop-Stack }
    "restart" { Restart-Stack }
    "status" { Show-Status }
    "logs" { Show-Logs }
    "cleanup" { Cleanup-Stack }
    "backup" { Backup-Data }
}
