pipeline {
    agent any

    parameters {
        string(name: 'DEPLOY_PORT', defaultValue: '8081', description: 'Port to run the app on')
    }

    tools {
        maven 'Maven-3.9.16'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Sneha-Gadhari/property-inspection-workflow.git'
            }
        }
        stage('Build') {
            steps {
                bat 'mvn clean compile'
            }
        }
        stage('Package') {
            steps {
                bat 'mvn package -DskipTests'
            }
        }
        stage('Deploy') {
            steps {
                bat '''
                    schtasks /end /tn "PIW_Deploy"
                    powershell -Command "Get-NetTCPConnection -LocalPort %DEPLOY_PORT% -ErrorAction SilentlyContinue | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }"
                    if exist app-deploy.log del app-deploy.log
                    exit 0
                '''
                bat 'powershell -ExecutionPolicy Bypass -File register-task.ps1 -Port %DEPLOY_PORT% -Workspace "%WORKSPACE%"'
                bat 'ping -n 45 127.0.0.1 >nul'
                bat 'schtasks /query /tn "PIW_Deploy" /v /fo LIST | findstr /i "Status Last"'
                bat 'netstat -ano | findstr :%DEPLOY_PORT% || exit 0'
                bat 'if exist app-deploy.log (type app-deploy.log) else (echo NO LOG FILE CREATED)'
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
    }
}