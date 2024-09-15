pipeline {
    agent any

    environment {
        REPO_URL = "git@github.com:the7ofdiamonds/orb-services-java.git"
        DOCKER_IMAGE = "orb-products-services"
        DOCKER_TAG = "latest"
        CONTAINER = "products-services"
        DIR = "/Users/jamellyons/Documents/J_C_LYONS_ENTERPRISES_LLC/ORB/Development/orb-backend"
        DIR_PROJECT = "${DIR}/java/${DOCKER_IMAGE}"
        DIR_POM = "${DIR_PROJECT}/${CONTAINER}"
    }

    stages {
        stage('Build JAR') {
            steps {
                dir("${DIR_POM}") {
                    sh "mvn clean package"
                }
            }
        }

        stage('Test Code') {
            steps {
                dir("${DIR_POM}") {
                    sh "mvn test"
                }
            }
        }

        stage('Create Container') {
            steps {
                dir("${DIR_PROJECT}") {
                    sh "docker build -f ${DOCKER_IMAGE}.Dockerfile -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                }
            }
        }
    }

    post {
        success {
            echo 'Build completed successfully.'
        }
        failure {
            echo 'Build failed.'
        }
        always {
            sh "${DIR}/clean-up.sh ${DIR}"
            sh "docker image prune -f"
        }
    }
}