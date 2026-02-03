pipeline {
    agent { label 'deploy' }
    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Nazwa brancha z docker-compose.yml')
        string(name: 'CONNECTOR_JSON_PATH', defaultValue: 'connector_outbox_AI.json', description: 'Ścieżka do pliku JSON z konfiguracją konektora AI')
        string(name: 'CONNECTOR_METHOD', defaultValue: 'POST', description: 'Metoda HTTP: POST (pierwsze uruchomienie) lub PUT (aktualizacja)')
        string(name: 'TARGET_HOST', defaultValue: '192.168.23.9', description: 'Adres IP maszyny docelowej (staging/prod)')
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
        stage('Debezium Connector AI') {
            steps {
                script {
                    if (params.CONNECTOR_METHOD == 'POST') {
                        echo "Tworzenie connectora AI (POST) na ${params.TARGET_HOST}"
                        sh """
                            curl -X POST -H 'Content-Type: application/json' \
                                --data-binary @${params.CONNECTOR_JSON_PATH} \
                                http://${params.TARGET_HOST}:8083/connectors
                        """
                    } else {
                        echo "Aktualizacja connectora AI (PUT) na ${params.TARGET_HOST}"
                        sh """
                            curl -X PUT -H 'Content-Type: application/json' \
                                --data-binary @${params.CONNECTOR_JSON_PATH} \
                                http://${params.TARGET_HOST}:8083/connectors/outbox-connector-ai/config
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
            echo 'Connector AI wysłany pomyślnie!'
        }
    }
}
