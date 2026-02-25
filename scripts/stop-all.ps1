$ErrorActionPreference = "Continue"

$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

$pidFile = ".running-services.json"
if (Test-Path $pidFile) {
    $entries = Get-Content $pidFile -Raw | ConvertFrom-Json
    foreach ($entry in $entries) {
        try {
            Stop-Process -Id $entry.pid -Force -ErrorAction Stop
            Write-Host "Stopped $($entry.name) (PID: $($entry.pid))"
        } catch {
            Write-Host "Process already stopped for $($entry.name) (PID: $($entry.pid))"
        }
    }
    Remove-Item $pidFile -Force
} else {
    Write-Host "No .running-services.json found. Skipping service process shutdown."
}

Write-Host "Stopping infra (MySQL + Redis)..."
docker compose stop mysql redis | Out-Host
Write-Host "Done."
