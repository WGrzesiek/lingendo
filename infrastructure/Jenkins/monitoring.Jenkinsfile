pipeline{
	agent { label 'builder' }

    parameters{
		string(name: 'ACTION', defaultValue: 'up', description: 'Dostępne akcje: up, down, restart')
		string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Nazwa brancha z docker-compose.yml')
        booleanParam(name: 'ENABLE_PROMETHEUS', defaultValue: true, description: 'Uruchom Prometheus')
        booleanParam(name: 'ENABLE_GRAFANA', defaultValue: true, description: 'Uruchom Grafana')
        booleanParam(name: 'ENABLE_ZIPKIN', defaultValue: true, description: 'Uruchom Zipkin')
        string(name: 'COMPOSE_PATH', defaultValue: 'docker-compose.monitoring.yml', description: 'Ścieżka do pliku docker-compose.yml')
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
				script {
					def composeEnvVars = ""
                    if (params.ENABLE_PROMETHEUS) {
						composeEnvVars += " prometheus "
                    }
                    if (params.ENABLE_GRAFANA) {
						composeEnvVars += "grafana "
                    }
                    if (params.ENABLE_ZIPKIN) {
						composeEnvVars += "zipkin "
                    }

                    sh """
						docker compose -f ${params.COMPOSE_PATH} ${params.ACTION} -d ${composeEnvVars}
                    """
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
