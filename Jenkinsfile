pipeline {
    agent any
    environment {
        IMAGE_NAME     = 'snehagadhari17/property-inspection-workflow'
        CONTAINER_PORT = '8085'
        DOCKERHUB      = credentials('dockerhub-creds')
    }
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
        stage('Test') {
            steps {
                bat 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'target/screenshots/*.png', allowEmptyArchive: true
                }
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
        stage('Docker Build') {
            steps {
                bat 'docker build -t %IMAGE_NAME%:%BUILD_NUMBER% -t %IMAGE_NAME%:latest .'
                bat 'docker images %IMAGE_NAME%'
            }
        }

        stage('Docker Push') {
            steps {
                bat 'echo %DOCKERHUB_PSW%| docker login -u %DOCKERHUB_USR% --password-stdin'
                bat 'docker push %IMAGE_NAME%:%BUILD_NUMBER%'
                bat 'docker push %IMAGE_NAME%:latest'
            }
            post {
                always {
                    bat 'docker logout || exit 0'
                }
            }
        }

        stage('Docker Deploy') {
            steps {
                bat 'docker rm -f piw-app || exit 0'
                bat 'docker run -d --name piw-app -p %CONTAINER_PORT%:8081 %IMAGE_NAME%:%BUILD_NUMBER%'
                bat 'ping -n 40 127.0.0.1 >nul'
                bat 'docker ps --filter name=piw-app'
                bat 'curl -f -s -o NUL http://localhost:%CONTAINER_PORT%/ && echo HEALTH CHECK PASSED'
                bat 'docker logs --tail 5 piw-app'
            }
        }

    }

    post {
        success {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
    }
}