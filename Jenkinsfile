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
                    for /f "tokens=2" %%P in ('wmic process where "CommandLine like '%%property-inspection-workflow%%' and Name='java.exe'" get ProcessId ^| findstr [0-9]') do taskkill /F /PID %%P
                    exit 0
                '''
                bat 'start /B java -jar target\\property-inspection-workflow-0.0.1-SNAPSHOT.jar --server.port=%DEPLOY_PORT%'
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
    }
}