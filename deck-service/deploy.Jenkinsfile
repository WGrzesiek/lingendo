pipeline {
    agent { label "deploy" }
    environment {
        REGISTRY_HOST = "192.168.23.5:5000"
        IMAGE_NAME = "${REGISTRY_HOST}/learnwords/deck-service"
    }
    parameters {
        string(name: 'ACTION', defaultValue: 'up', description: 'Dostępne akcje: up, down, restart')
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Nazwa brancha z docker-compose.yml')
        string(name: 'SPRING_PROFILES_ACTIVE', defaultValue: 'dev', description: 'Aktywne profile Springa')
    }
    stages {
        stage('Compose Down') {
            when { expression { params.ACTION == 'down' } }
            steps {
                sh "docker compose -f docker-compose.deck.yml down"
            }
        }
        stage('Compose Restart') {
            when { expression { params.ACTION == 'restart' } }
            steps {
                sh "docker compose -f docker-compose.deck.yml restart"
            }
        }
        stage('Pobierz obraz z repository') {
            when { expression { params.ACTION == 'up' } }
            steps {
                sh "docker pull ${IMAGE_NAME}:${IMAGE_TAG}"
            }
        }
        stage('Checkout') {
            when { expression { params.ACTION == 'up' } }
            steps {
                checkout scmGit(
                    branches: [[name: "*/${params.BRANCH_NAME}"]],
                    userRemoteConfigs: [[
                        url: 'git@bitbucket.org:grzegorz5/deck-service.git',
                        credentialsId: 'bitbucket'
                    ]]
                )
            }
        }
        stage('Uruchom Docker Compose') {
            when { expression { params.ACTION == 'up' } }
            steps {
                sh """
                    docker compose -f docker-compose.deck.yml down
                    IMAGE_NAME=${IMAGE_NAME} IMAGE_TAG=${IMAGE_TAG} SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE} docker compose -f docker-compose.deck.yml up -d
                """
            }
        }
    }
    post {
        always {
            echo "========always========"
        }
        success {
            echo "========pipeline executed successfully ========"
        }
        failure {
            echo "========pipeline execution failed========"
        }
    }
}