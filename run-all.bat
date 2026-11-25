@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo     Demarrage de la plateforme DriveEasy
echo ===================================================

set "JAVA_EXE=C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot\bin\java.exe"
if not exist "%JAVA_EXE%" (
    if defined JAVA_HOME (
        set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
    ) else (
        set "JAVA_EXE=java"
    )
)

echo Utilisation de Java : "%JAVA_EXE%"
"%JAVA_EXE%" -version

cd /d "%~dp0"

echo [1/6] Demarrage de Service Registry (Eureka : 8761)...
start "DriveEasy - Service Registry (8761)" "%JAVA_EXE%" -jar service-registry\build\libs\service-registry-0.0.1-SNAPSHOT.jar
timeout /t 15 /nobreak >nul

echo [2/6] Demarrage de Config Server (8888)...
start "DriveEasy - Config Server (8888)" "%JAVA_EXE%" -jar config-server\build\libs\config-server-0.0.1-SNAPSHOT.jar
timeout /t 10 /nobreak >nul

echo [3/6] Demarrage de Vehicule Service (8081)...
start "DriveEasy - Vehicule Service (8081)" "%JAVA_EXE%" -jar vehicule-service\build\libs\vehicule-service-0.0.1-SNAPSHOT.jar

echo [4/6] Demarrage de Client Service (8082)...
start "DriveEasy - Client Service (8082)" "%JAVA_EXE%" -jar client-service\build\libs\client-service-0.0.1-SNAPSHOT.jar

echo [5/6] Demarrage de Location Service (8083)...
start "DriveEasy - Location Service (8083)" "%JAVA_EXE%" -jar location-service\build\libs\location-service-0.0.1-SNAPSHOT.jar
timeout /t 15 /nobreak >nul

echo [6/6] Demarrage de API Gateway (8080)...
start "DriveEasy - API Gateway (8080)" "%JAVA_EXE%" -jar api-gateway\build\libs\api-gateway-0.0.1-SNAPSHOT.jar

echo.
echo ===================================================
echo  Tous les services ont ete lances !
echo  - Eureka Dashboard : http://localhost:8761
echo  - Config Server    : http://localhost:8888
echo  - API Gateway      : http://localhost:8080
echo    * /api/vehicules/** -> Vehicule Service (8081)
echo    * /api/clients/**   -> Client Service (8082)
echo    * /api/locations/** -> Location Service (8083)
echo ===================================================
pause
