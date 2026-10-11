@echo off
set PORT=%1
set WS=%~dp0
set DEPLOY_DIR=%WS%..\..\deploy
echo [%date% %time%] deploy.bat started, port=%PORT% > "%WS%app-deploy.log"
if not exist "%DEPLOY_DIR%" mkdir "%DEPLOY_DIR%"
copy /Y "%WS%target\property-inspection-workflow-0.0.1-SNAPSHOT.jar" "%DEPLOY_DIR%\app.jar" >> "%WS%app-deploy.log" 2>&1
cd /d "%DEPLOY_DIR%"
"C:\Program Files\Eclipse Adoptium\jdk-21.0.12.8-hotspot\bin\java.exe" -jar app.jar --server.port=%PORT% >> "%WS%app-deploy.log" 2>&1