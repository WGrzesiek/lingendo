pipeline {
    agent { label "builder" }
    tools {
        jdk   'JDK24'
        maven 'Maven'
    }

    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Branch z migracjami')
        string(name: 'FLYWAY_URL', defaultValue: 'jdbc:postgresql://192.168.23.9:5432/koog', description: 'JDBC URL do DB')
        string(name: 'FLYWAY_USER', defaultValue: 'admin', description: 'Użytkownik DB')
        password(name: 'FLYWAY_PASSWORD', defaultValue: '', description: 'Hasło DB')
        string(name: 'FLYWAY_SCHEMAS', defaultValue: 'public', description: 'Schemat(y) DB')
    }

    environment {
        GRADLE_OPTS = '-Dorg.gradle.daemon=false'
    }

    stages {
        stage('Checkout') {
            steps {
                dir('koog-service') {
                    checkout scmGit(
                        branches: [[name: "*/${params.BRANCH_NAME}"]],
                        userRemoteConfigs: [[
                            url: 'git@bitbucket.org:grzegorz5/koog-service.git',
                            credentialsId: 'bitbucket'
                        ]]
                    )
                }
            }
        }

        stage('Flyway INFO (before)') {
            steps {
                dir('koog-service') {
                    withEnv([
                        "FLYWAY_URL=${params.FLYWAY_URL}",
                        "FLYWAY_USER=${params.FLYWAY_USER}",
                        "FLYWAY_PASSWORD=${params.FLYWAY_PASSWORD}",
                        "FLYWAY_SCHEMAS=${params.FLYWAY_SCHEMAS}"
                    ]) {
                        sh 'chmod +x gradlew'
                        sh '''
                            ./gradlew flywayInfo \
                                -Dflyway.url="$FLYWAY_URL" \
                                -Dflyway.user="$FLYWAY_USER" \
                                -Dflyway.password="$FLYWAY_PASSWORD" \
                                -Dflyway.schemas="$FLYWAY_SCHEMAS" \
                            | tee flyway-info-before.txt
                        '''
                    }
                }
            }
        }

        stage('Flyway MIGRATE') {
            steps {
                dir('koog-service') {
                    withEnv([
                        "FLYWAY_URL=${params.FLYWAY_URL}",
                        "FLYWAY_USER=${params.FLYWAY_USER}",
                        "FLYWAY_PASSWORD=${params.FLYWAY_PASSWORD}",
                        "FLYWAY_SCHEMAS=${params.FLYWAY_SCHEMAS}"
                    ]) {
                        sh '''
                            ./gradlew flywayMigrate \
                                -Dflyway.url="$FLYWAY_URL" \
                                -Dflyway.user="$FLYWAY_USER" \
                                -Dflyway.password="$FLYWAY_PASSWORD" \
                                -Dflyway.schemas="$FLYWAY_SCHEMAS"
                        '''
                    }
                }
            }
        }

        stage('Flyway INFO (after)') {
            steps {
                dir('koog-service') {
                    withEnv([
                        "FLYWAY_URL=${params.FLYWAY_URL}",
                        "FLYWAY_USER=${params.FLYWAY_USER}",
                        "FLYWAY_PASSWORD=${params.FLYWAY_PASSWORD}",
                        "FLYWAY_SCHEMAS=${params.FLYWAY_SCHEMAS}"
                    ]) {
                        sh '''
                            ./gradlew flywayInfo \
                                -Dflyway.url="$FLYWAY_URL" \
                                -Dflyway.user="$FLYWAY_USER" \
                                -Dflyway.password="$FLYWAY_PASSWORD" \
                                -Dflyway.schemas="$FLYWAY_SCHEMAS" \
                            | tee flyway-info-after.txt
                        '''
                    }
                    archiveArtifacts artifacts: 'flyway-info-*.txt', fingerprint: true
                }
            }
        }

        stage('Flyway Delta (diff)') {
            steps {
                dir('koog-service') {
                    sh '''
                        diff -u flyway-info-before.txt flyway-info-after.txt > flyway-delta.diff || true
                    '''
                    archiveArtifacts artifacts: 'flyway-delta.diff', fingerprint: true, allowEmptyArchive: true
                }
            }
        }
    }

    post {
        always  { echo "========always========" }
        success { echo "========migration executed successfully ========" }
        failure { echo "========migration execution failed========" }
    }
}
