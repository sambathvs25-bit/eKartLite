param(
    [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

$services = @(
    @{ Name = "auth-service"; Pom = "auth-service/pom.xml" },
    @{ Name = "api-gateway"; Pom = "api-gateway/pom.xml" },
    @{ Name = "customer-service"; Pom = "customer-service/pom.xml" },
    @{ Name = "product-service"; Pom = "product-service/pom.xml" },
    @{ Name = "cart-service"; Pom = "cart-service/pom.xml" },
    @{ Name = "order-service"; Pom = "order-service/pom.xml" },
    @{ Name = "payment-service"; Pom = "payment-service/pom.xml" },
    @{ Name = "batch-service"; Pom = "batch-service/pom.xml" }
)

Write-Host "Starting infra (MySQL + Redis) via docker compose..."
docker compose up -d mysql redis | Out-Host

Write-Host "Waiting for MySQL and Redis to become healthy..."
for ($i = 0; $i -lt 30; $i++) {
    $mysqlStatus = docker inspect --format='{{.State.Health.Status}}' eflipkartlite-mysql 2>$null
    $redisStatus = docker inspect --format='{{.State.Health.Status}}' eflipkartlite-redis 2>$null
    if ($mysqlStatus -eq "healthy" -and $redisStatus -eq "healthy") {
        break
    }
    Start-Sleep -Seconds 2
}

if (-not $SkipBuild) {
    Write-Host "Compiling project..."
    mvn -s mvn-settings.xml -DskipTests compile | Out-Host
}

$started = @()

foreach ($svc in $services) {
    $cmd = "Set-Location '$root'; mvn -s mvn-settings.xml -f '$($svc.Pom)' spring-boot:run"
    $proc = Start-Process -FilePath "powershell" -ArgumentList @("-NoProfile", "-NoExit", "-Command", $cmd) -PassThru
    $started += [PSCustomObject]@{
        name = $svc.Name
        pid  = $proc.Id
    }
    Write-Host "Started $($svc.Name) (PID: $($proc.Id))"
    Start-Sleep -Milliseconds 700
}

$started | ConvertTo-Json | Set-Content ".running-services.json"
Write-Host ""
Write-Host "All services started. Process map saved to .running-services.json"
Write-Host "Use .\scripts\stop-all.ps1 to stop services and infra."
