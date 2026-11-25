@echo off
echo Arret des services DriveEasy...

for %%P in (8761 8888 8081 8082 8083 8080) do (
    for /f "tokens=5" %%a in ('netstat -aon ^| findstr :%%P ^| findstr LISTENING') do (
        echo Fermeture du processus PID %%a sur le port %%P...
        taskkill /F /PID %%a 2>nul
    )
)

echo Tous les services ont ete arretes.
pause
