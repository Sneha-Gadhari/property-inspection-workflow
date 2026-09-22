@echo off
cd /d %~dp0
java -jar target\property-inspection-workflow-0.0.1-SNAPSHOT.jar --server.port=%1 > app-deploy.log 2>&1