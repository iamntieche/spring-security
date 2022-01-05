def getDockerTag(){
    def tag = sh script: 'git rev-parse HEAD', returnStdout:true
    return tag
}

pipeline{
        agent any
        tools {
            maven 'Maven'
        }
        environment{
            Docker_tag = getDockerTag();
        }
    stages
    {
        stage('Sonar analyse'){
            steps{
                script{
                    withSonarQubeEnv(credentialsId: 'sonarQubeServer') {
                        sh "mvn clean package -Dsonar.sources=src/main/java/ -Dsonar.java.binaries=target sonar:sonar"
                    }

                }
            }
        }
        stage('Quality Gate Status check'){
            steps{
                script{
                timeout(time: 1, unit: 'HOURS') {
                    def qg = waitForQualityGate()
                    if (qg.status != 'OK') {
                    error "Pipeline aborted due to quality gate failure: ${qg.status}"
                    }
                }
                }
            }
        }
        stage('Maven - Clean'){
            steps{
                sh 'mvn clean install'
            }
        }
        /*stage('Docker - Build '){
            steps{
                script{
                    withCredentials([string(credentialsId: 'docker_password', variable: 'docker_hub_mfoumgroup')]) {
                       sh '''
                         docker build  -t mfoumgroup/spring-security:Docker_tag .
                         docker login -u mfoumgroup -p $docker_hub_mfoumgroup
                         docker push mfoumgroup/spring-security:Docker_tag
                         '''
                    }

                }
            }
        }*/
    }
}