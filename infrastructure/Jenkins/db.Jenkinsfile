pipeline{
	agent { label 'deploy' }

    parameters{
		string(name: 'ACTION', defaultValue: 'up', description: 'Dostępne akcje: up, down, restart')
		string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Nazwa brancha z docker-compose.yml')
        booleanParam(name: 'ENABLE_POSTGRES', defaultValue: true, description: 'Uruchom Postgres')
        booleanParam(name: 'ENABLE_PGADMIN', defaultValue: false, description: 'Uruchom PGAdmin')
        booleanParam(name: 'ENABLE_MONGODB', defaultValue: true, description: 'Uruchom MongoDB')
        booleanParam(name: 'ENABLE_MONGOEXPRESS', defaultValue: false, description: 'Uruchom Mongo Express')
		booleanParam(name: 'ENABLE_REDIS', defaultValue: true, description: 'Uruchom Redis')
		booleanParam(name: 'ENABLE_REDISINSIGHT', defaultValue: false, description: 'Uruchom RedisInsight')
        string(name: 'COMPOSE_PATH', defaultValue: 'docker-compose.db.yml', description: 'Ścieżka do pliku docker-compose.yml')
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
          if (params.ENABLE_POSTGRES) {
            composeEnvVars += " postgres "
          }
          if (params.ENABLE_PGADMIN) {
            composeEnvVars += " pgadmin "
          }
          if (params.ENABLE_MONGODB) {
            composeEnvVars += " mongodb "
          }
          if (params.ENABLE_MONGOEXPRESS) {
            composeEnvVars += " mongo-express "
          }
		  if (params.ENABLE_REDIS) {
			composeEnvVars += " redis "
		  }
		  if (params.ENABLE_REDISINSIGHT) {
			composeEnvVars += " redisinsight "
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
