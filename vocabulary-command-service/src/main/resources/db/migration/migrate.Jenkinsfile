pipeline {
    agent { label "builder" }
    tools {
        jdk   'JDK24'
        maven 'Maven'
    }
    parameters {
        string(name: 'PARENT_BRANCH', defaultValue: 'main', description: 'Git branch dla parent')
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Branch z migracjami')
        string(name: 'FLYWAY_URL',  defaultValue: 'jdbc:postgresql://192.168.23.9:5432/outbox', description: 'JDBC URL do DB')
        string(name: 'FLYWAY_USER', defaultValue: 'admin', description: 'Użytkownik DB')
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
                dir('vocabulary-command-service') {
                checkout scmGit(
                    branches: [[name: "*/${params.BRANCH_NAME}"]],
                    userRemoteConfigs: [[
                        url: 'git@bitbucket.org:grzegorz5/vocabulary-command-service.git',
                        credentialsId: 'bitbucket'
                    ]]
                )
            }
        }
        }
    stage('Flyway INFO (before)') {
      steps {
        dir('vocabulary-command-service') {
          withEnv([
            "FW_URL=${params.FLYWAY_URL}",
            "FW_USER=${params.FLYWAY_USER}",
            "FW_PASS=${params.FLYWAY_PASSWORD}",
            "FW_SCHEMAS=${params.FLYWAY_SCHEMAS}"
          ]) {
            sh '''
              mvn -B -DskipTests flyway:info \
                -Dflyway.url="$FW_URL" \
                -Dflyway.user="$FW_USER" \
                -Dflyway.password="$FW_PASS" \
                -Dflyway.schemas="$FW_SCHEMAS" \
              | tee flyway-info-before.txt
            '''
          }
        }
      }
    }

    stage('Flyway MIGRATE') {
      steps {
        dir('vocabulary-command-service') {
          withEnv([
            "FW_URL=${params.FLYWAY_URL}",
            "FW_USER=${params.FLYWAY_USER}",
            "FW_PASS=${params.FLYWAY_PASSWORD}",
            "FW_SCHEMAS=${params.FLYWAY_SCHEMAS}"
          ]) {
            sh '''
              mvn -B -DskipTests flyway:migrate \
                -Dflyway.url="$FW_URL" \
                -Dflyway.user="$FW_USER" \
                -Dflyway.password="$FW_PASS" \
                -Dflyway.schemas="$FW_SCHEMAS"
            '''
          }
        }
      }
    }

    stage('Flyway INFO (after)') {
      steps {
        dir('vocabulary-command-service') {
          withEnv([
            "FW_URL=${params.FLYWAY_URL}",
            "FW_USER=${params.FLYWAY_USER}",
            "FW_PASS=${params.FLYWAY_PASSWORD}",
            "FW_SCHEMAS=${params.FLYWAY_SCHEMAS}"
          ]) {
            sh '''
              mvn -B -DskipTests flyway:info \
                -Dflyway.url="$FW_URL" \
                -Dflyway.user="$FW_USER" \
                -Dflyway.password="$FW_PASS" \
                -Dflyway.schemas="$FW_SCHEMAS" \
              | tee flyway-info-after.txt
            '''
          }
          archiveArtifacts artifacts: 'flyway-info-*.txt', fingerprint: true
        }
      }
    }

    stage('Flyway Delta (diff)') {
      steps {
        dir('vocabulary-command-service') {
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
