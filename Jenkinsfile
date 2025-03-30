pipeline {
    agent any

    stages {
        stage('Build Backend') {
            steps {
                dir('back') {
                    echo 'Building..'
                    sh 'mkdir -p ~/.m2'
                    sh 'docker run --rm -v "$PWD":/usr/src/mymaven -v ~/.m2:/root/.m2 -w /usr/src/mymaven maven:3.8.6-openjdk-18 mvn clean package -DskipTests'
                }
            }
        }
        stage('Test Backend') {
            steps {
                dir('back') {
                        echo 'Testing..'
                        echo 'Start Postgresql'
                        sh 'docker run -p 5432:5432 --net=host --expose 5432 -e POSTGRES_DB=tb -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -d postgres'
                        echo 'Run tests'
                        sh 'docker run --rm --net=host -v "$PWD":/usr/src/mymaven -v ~/.m2:/root/.m2 -w /usr/src/mymaven maven:3.8.6-openjdk-18 mvn test'
                }

            }
            post {
                always {
                    junit 'back/target/surefire-reports/*.xml'
                    echo 'stop all dockers'
                    sh 'docker stop $(docker ps -q)'
                }
            }

        }
        stage('Create backend docker image') {
            steps {
                sh './docker/server_rest/build_rest.sh'
            }
        }

        stage('Push backend on dev server') {
            steps {
                sh './docker/server_rest/update_server_rest.sh'
            }
        }

    }
    post {
        always {
            cleanWs()
        }
    }
}
