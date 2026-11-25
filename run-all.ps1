# Demarrage de la plateforme DriveEasy
$javaExe = "C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot\bin\java.exe"
if (-not (Test-Path $javaExe)) {
    if ($env:JAVA_HOME) {
        $javaExe = Join-Path $env:JAVA_HOME "bin\java.exe"
    } else {
        $javaExe = "java"
    }
}

Write-Host "Utilisation de Java : $javaExe" -ForegroundColor Cyan
& $javaExe -version

Set-Location $PSScriptRoot

Write-Host "[1/6] Demarrage de Service Registry (8761)..." -ForegroundColor Yellow
Start-Process -FilePath $javaExe -ArgumentList "-jar", "service-registry\build\libs\service-registry-0.0.1-SNAPSHOT.jar" -WorkingDirectory $PSScriptRoot
Start-Sleep -Seconds 15

Write-Host "[2/6] Demarrage de Config Server (8888)..." -ForegroundColor Yellow
Start-Process -FilePath $javaExe -ArgumentList "-jar", "config-server\build\libs\config-server-0.0.1-SNAPSHOT.jar" -WorkingDirectory $PSScriptRoot
Start-Sleep -Seconds 10

Write-Host "[3/6] Demarrage de Vehicule Service (8081)..." -ForegroundColor Yellow
Start-Process -FilePath $javaExe -ArgumentList "-jar", "vehicule-service\build\libs\vehicule-service-0.0.1-SNAPSHOT.jar" -WorkingDirectory $PSScriptRoot

Write-Host "[4/6] Demarrage de Client Service (8082)..." -ForegroundColor Yellow
Start-Process -FilePath $javaExe -ArgumentList "-jar", "client-service\build\libs\client-service-0.0.1-SNAPSHOT.jar" -WorkingDirectory $PSScriptRoot

Write-Host "[5/6] Demarrage de Location Service (8083)..." -ForegroundColor Yellow
Start-Process -FilePath $javaExe -ArgumentList "-jar", "location-service\build\libs\location-service-0.0.1-SNAPSHOT.jar" -WorkingDirectory $PSScriptRoot
Start-Sleep -Seconds 12

Write-Host "[6/6] Demarrage de API Gateway (8080)..." -ForegroundColor Yellow
Start-Process -FilePath $javaExe -ArgumentList "-jar", "api-gateway\build\libs\api-gateway-0.0.1-SNAPSHOT.jar" -WorkingDirectory $PSScriptRoot

Write-Host "`nTous les services ont ete lances avec succes !" -ForegroundColor Green
Write-Host " - Eureka Dashboard : http://localhost:8761"
Write-Host " - Config Server    : http://localhost:8888"
Write-Host " - API Gateway      : http://localhost:8080"
