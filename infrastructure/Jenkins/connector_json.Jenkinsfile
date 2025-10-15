pipeline {
    agent { label 'deploy' }
    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Nazwa brancha z docker-compose.yml')
        string(name: 'CONNECTOR_JSON_PATH', defaultValue: 'connector_outbox.json', description: 'Ścieżka do pliku JSON z konfiguracją konektora')
        string(name: 'CONNECTOR_METHOD', defaultValue: 'POST', description: 'Metoda HTTP: POST (pierwsze uruchomienie) lub PUT (aktualizacja)')
    }
    stages {
        stage('Check Postgres') {
            steps {
                script {
                    sh '''
                        docker ps | grep postgres
                        docker exec postgres pg_isready -U admin -d outbox
                    '''
                }
            }
        }
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
        stage('Debezium Connector PUT') {
            steps {
                script {
                    if (params.CONNECTOR_METHOD == 'POST') {
                        echo 'Tworzenie connectora (POST). Użyj POST przy pierwszym uruchomieniu.'
                        sh """
                            curl -X POST -H 'Content-Type: application/json' \
                                --data-binary @${params.CONNECTOR_JSON_PATH} \
                                http://localhost:8083/connectors
                        """
                    } else {
                        echo 'Aktualizacja connectora (PUT). Użyj PUT przy kolejnych uruchomieniach.'
                        sh """
                            curl -X PUT -H 'Content-Type: application/json' \
                                --data-binary @${params.CONNECTOR_JSON_PATH} \
                                http://localhost:8083/connectors/outbox-connector/config
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
            echo 'Connector wysłany pomyślnie!'
        }
    }
}
