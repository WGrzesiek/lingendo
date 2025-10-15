pipeline {
    agent { label "deploy" }
    tools {
        jdk   'JDK24'
        maven 'Maven'
    }
    parameters {
        string(name: 'PARENT_BRANCH', defaultValue: 'main', description: 'Git branch dla parent')
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Branch z migracjami')
        string(name: 'FLYWAY_URL',  defaultValue: 'jdbc:postgresql://postgres:5432/deck', description: 'JDBC URL do DB')
        string(name: 'FLYWAY_USER', defaultValue: 'postgres', description: 'Użytkownik DB')
        password(name: 'FLYWAY_PASSWORD', defaultValue: '', description: 'Hasło DB')
        string(name: 'FLYWAY_SCHEMAS', defaultValue: 'public', description: 'Schemat(y) DB')
    }
    stages {
        stage('Checkout parent') {
			steps {
				dir('parent') {
                    checkout scmGit(
                        branches: [[name: "*/${params.PARENT_BRANCH}"]],
                        userRemoteConfigs: [[
                            url: 'git@bitbucket.org:grzegorz5/learnwords-parent.git',
                            credentialsId: 'bitbucket'
                        ]]
                    )
                }
            }
        }
        stage('Checkout') {
            steps {
                dir('deck-service') {
                checkout scmGit(
                    branches: [[name: "*/${params.BRANCH_NAME}"]],
                    userRemoteConfigs: [[
                        url: 'git@bitbucket.org:grzegorz5/deck-service.git',
                        credentialsId: 'bitbucket'
                    ]]
                )
            }
        }
        }
        stage('Flyway Migration') {
            steps {
                sh """
                    mvn -B -q -DskipTests flyway:migrate \
                        -Dflyway.url="${params.FLYWAY_URL}" \
                        -Dflyway.user="${params.FLYWAY_USER}" \
                        -Dflyway.password="${params.FLYWAY_PASSWORD}" \
                        -Dflyway.schemas="${params.FLYWAY_SCHEMAS}"
                """
            }
        }
    }
    post {
        always  { echo "========always========" }
        success { echo "========migration executed successfully ========" }
        failure { echo "========migration execution failed========" }
    }
    
}
