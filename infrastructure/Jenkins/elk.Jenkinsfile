pipeline {
	agent {
		label 'deploy'
	}
	parameters {
		string(name: 'ACTION', defaultValue: 'up', description: 'Dostępne akcje: up, down, restart')
		string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Nazwa brancha z docker-compose.yml')
		string(name: 'COMPOSE_PATH', defaultValue: 'docker-compose.yml', description: 'Ścieżka do pliku docker-compose.yml')
	}
    stages {
		stage('Checkout') {
			steps {
				checkout scmGit(
				  branches: [[name: "*/${params.BRANCH_NAME}"]],
				  userRemoteConfigs: [[
					url: 'git@bitbucket.org:grzegorz5/infrastructure.git',
					credentialsId: 'bitbucket'
				  ]]
    			)
  			}
		}
		stage('Run Docker Compose') {
			steps {
			dir('docker-elk') {
                    script {
                        sh """
                            docker compose -f ${params.COMPOSE_PATH} ${params.ACTION} -d
                        """
                    }
				}
			}
		}
	}
	post {
		failure {
			echo 'Pipeline failed!'
		}
		success {
			sh """ docker ps """
			echo 'Docker Compose uruchomiony pomyślnie!'
		}
	}
}