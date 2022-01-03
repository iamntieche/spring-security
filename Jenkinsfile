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
        stage('Quality Gate Status check'){
            steps{
                script{
                    withSonarQubeEnv(credentialsId: 'sonarQubeServer') {
                        sh "mvn clean -Dsonar.sources=src/main/java/ -Dsonar.java.binaries=target sonar:sonar"
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
        stage('Maven - Clean'){
            steps{
                sh 'mvn clean install'
            }
        }
        stage('Docker - Build '){
            agent {
                    docker {
                        image 'gradle:6.7-jdk11'
                        // Run the container on the node specified at the top-level of the Pipeline, in the same workspace, rather than on a new node entirely:
                        reuseNode true
                    }
                }
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
        }
    }
}