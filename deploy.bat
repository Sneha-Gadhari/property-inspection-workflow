@echo off
set PORT=%1
set WS=%~dp0
echo [%date% %time%] deploy.bat started, port=%PORT% > "%WS%app-deploy.log"
cd /d "%WS%"
"C:\Program Files\Eclipse Adoptium\jdk-21.0.12.8-hotspot\bin\java.exe" -jar target\property-inspection-workflow-0.0.1-SNAPSHOT.jar --server.port=%PORT% >> "%WS%app-deploy.log" 2>&1