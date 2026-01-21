pipeline {
  agent { label "deploy" }

  environment {
    REGISTRY_HOST = "192.168.23.5:5000"
    IMAGE_NAME = "${REGISTRY_HOST}/learnwords/koog-service"
  }

  parameters {
    string(name: 'ACTION', defaultValue: 'up', description: 'Dostępne akcje: up, down, restart')
    string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Nazwa brancha z docker-compose.yml')
    string(name: 'IMAGE_TAG', defaultValue: 'dev-1', description: 'Tag obrazu Docker')
    string(name: 'SPRING_PROFILES_ACTIVE', defaultValue: 'dev', description: 'Aktywne profile Springa')
  }

  stages {
    stage('Compose Down') {
      when { expression { params.ACTION == 'down' } }
      steps {
        sh "docker compose -f docker-compose.koog.yml down"
      }
    }

    stage('Compose Restart') {
      when { expression { params.ACTION == 'restart' } }
      steps {
        sh "docker compose -f docker-compose.koog.yml restart"
      }
    }

    stage('Pobierz obraz z repository') {
      when { expression { params.ACTION == 'up' } }
      steps {
        sh "docker pull ${IMAGE_NAME}:${params.IMAGE_TAG}"
      }
    }

    stage('Checkout') {
      when { expression { params.ACTION == 'up' } }
      steps {
        checkout scmGit(
          branches: [[name: "*/${params.BRANCH_NAME}"]],
          userRemoteConfigs: [[
            url: 'git@bitbucket.org:grzegorz5/koog-service.git',
            credentialsId: 'bitbucket'
          ]]
        )
      }
    }

    stage('Uruchom Docker Compose') {
      when { expression { params.ACTION == 'up' } }
      steps {
        withCredentials([string(credentialsId: 'openai-api-key-dev', variable: 'OPENAI_API_KEY')]) {
          sh """
            docker compose -f docker-compose.koog.yml down
            IMAGE_NAME=${IMAGE_NAME} \
            IMAGE_TAG=${params.IMAGE_TAG} \
            SPRING_PROFILES_ACTIVE=${params.SPRING_PROFILES_ACTIVE} \
            OPENAI_API_KEY=\$OPENAI_API_KEY \
            docker compose -f docker-compose.koog.yml up -d
          """

          sh """
            docker exec -i koog-service sh -lc 'echo "OPENAI_API_KEY length=\${#OPENAI_API_KEY}"'
          """
        }
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
