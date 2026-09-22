@echo off
echo DEPLOY SCRIPT STARTED > app-deploy.log
echo Current directory: %CD% >> app-deploy.log
cd /d "C:\Users\SNEHA\AppData\Local\Jenkins\.jenkins\workspace\property-inspection-workflow-pipeline"
echo Changed directory to: %CD% >> app-deploy.log
java -jar target\property-inspection-workflow-0.0.1-SNAPSHOT.jar --server.port=%1 >> app-deploy.log 2>&1