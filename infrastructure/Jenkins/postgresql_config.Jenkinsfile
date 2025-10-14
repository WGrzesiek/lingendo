pipeline {
    agent { label 'deploy' }
    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Nazwa brancha z docker-compose.yml')
        string(name: 'POSTGRESQL_CONF_PATH', defaultValue: 'postgresql.conf', description: 'Ścieżka do pliku postgresql.conf')
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
        stage('Kopiuj postgresql.conf do kontenera') {
            steps {
                script {
                    sh """
                        docker cp ${params.POSTGRESQL_CONF_PATH} postgres:/var/lib/postgresql/data/postgresql.conf
                    """
                }
            }
        }
        stage('Restartuj kontener postgres') {
            steps {
                script {
                    sh """
                        docker restart postgres
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
            echo 'Plik postgresql.conf skopiowany i kontener zrestartowany!'
        }
    }
}