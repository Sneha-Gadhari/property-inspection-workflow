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
                    powershell -Command "Get-NetTCPConnection -LocalPort %DEPLOY_PORT% -ErrorAction SilentlyContinue | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }"
                    exit 0
                '''
                bat '''
                    schtasks /create /tn "PIW_Deploy" /tr "cmd /c cd /d %WORKSPACE% && java -jar target\\property-inspection-workflow-0.0.1-SNAPSHOT.jar --server.port=%DEPLOY_PORT% > app-deploy.log 2>&1" /sc once /st 23:59 /f
                    schtasks /run /tn "PIW_Deploy"
                '''
                bat 'ping -n 15 127.0.0.1 >nul'
                bat 'type app-deploy.log'
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
    }
}