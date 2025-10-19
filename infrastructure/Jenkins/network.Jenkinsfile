pipeline{
	agent { label 'deploy' }

	stages{
		stage('Create network') {
			steps {
				sh 'docker network create learnwords-net -d bridge'
			}
		}
	}
	post {
		success {
			echo 'Network created successfully.'
		}
		failure {
			echo 'Failed to create network.'
			}
	}
}