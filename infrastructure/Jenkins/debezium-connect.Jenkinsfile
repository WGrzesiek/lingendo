pipeline{
	agent { label 'builder' }

    parameters{
		string(name: 'ACTION', defaultValue: 'up', description: 'Dostępne akcje: up, down, restart')
		string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Nazwa brancha z docker-compose.yml')
        booleanParam(name: 'ENABLE_CONNECT', defaultValue: true, description: 'Uruchom Kafka Connect')
        booleanParam(name: 'ENABLE_AKHQ', defaultValue: false, description: 'Uruchom AKHQ (GUI do Kafki)')
        booleanParam(name: 'ENABLE_DEBEZIUM_UI', defaultValue: false, description: 'Uruchom Debezium UI (GUI do Connect)')
        string(name: 'COMPOSE_PATH', defaultValue: 'docker-compose.connect.yml', description: 'Ścieżka do pliku docker-compose.yml')
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
                    if (params.ENABLE_CONNECT) {
						composeEnvVars += " connect "
                    }
                    if (params.ENABLE_AKHQ) {
						composeEnvVars += "akhq "
                    }
                    if (params.ENABLE_DEBEZIUM_UI) {
						composeEnvVars += "debezium-ui "
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
