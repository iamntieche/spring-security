pipeline{
    agent any
    tools {
        maven 'Maven'
    }
    stages{
        stage('Quality Gate Status check'){
            steps{
                script{
                    withSonarQubeEnv(credentialsId: 'sonarQubeServer') {
                        sh "mvn sonar:sonar"
                    }
                     timeout(time: 1, unit: 'HOURS') {
                         def qg = waitForQualityGate()
                         if (qg.status != 'OK') {
                         error "Pipeline aborted due to quality gate failure: ${qg.status}"
                         }
                    }
                }
            }
        }
        stage('Maven - Build'){
            steps{
                sh 'mvn clean install'
            }
        }
    }
}