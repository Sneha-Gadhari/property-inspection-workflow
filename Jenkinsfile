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
                bat 'schtasks /create /tn "PIW_Deploy" /tr "%WORKSPACE%\\deploy.bat %DEPLOY_PORT%" /sc once /st 23:59 /f'
                bat 'schtasks /run /tn "PIW_Deploy"'
                bat 'ping -n 20 127.0.0.1 >nul'
                bat 'schtasks /query /tn "PIW_Deploy" /v /fo LIST'
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