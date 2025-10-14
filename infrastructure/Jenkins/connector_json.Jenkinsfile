pipeline {
    agent { label 'deploy' }
    	parameters {
		string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Nazwa brancha z docker-compose.yml')
		string(name: 'CONNECTOR_JSON_PATH', defaultValue: 'connector_outbox.json', description: 'Ścieżka do pliku JSON z konfiguracją konektora')
	}
    stages {
        steps {
				checkout scmGit(
				  branches: [[name: "*/${params.BRANCH_NAME}"]],
				  userRemoteConfigs: [[
					url: 'git@bitbucket.org:grzegorz5/infrastructure.git',
					credentialsId: 'bitbucket'
				  ]]
    			)
  			}
        stage('Debezium Connector PUT') {
            steps {
                script {
                    sh '''
                        curl -X PUT -H "Content-Type: application/json" \
                            --data-binary @${params.CONNECTOR_JSON_PATH} \
                            http://localhost:8083/connectors/outbox-connector/config
                    '''
                }
            }
        }
    }
    post {
        failure {
            echo 'Pipeline failed!'
        }
        success {
            echo 'Connector wysłany pomyślnie!'
        }
    }
}
