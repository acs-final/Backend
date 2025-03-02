pipeline {
    agent any
    environment {
        BUILD_NUMBER = "v9"
        HARBOR_CREDENTIALS = credentials('harbor')
        BACKEND_REPO = "https://github.com/acs-final/Backend.git"
        BACKEND_IMAGE_PREFIX = "192.168.2.141:443/k8s-project"
        LOCAL_CONFIG_BASE_PATH = "/home/kevin/Backend"
        LOCAL_COMPOSE_FILE_PATH = "/home/kevin/Backend/docker-compose.yaml"
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    echo "Current workspace: ${pwd}"

                    git branch: 'develop',
                        credentialsId: 'github-token',  // Jenkins에 등록한 GitHub Credentials ID
                        url: 'https://github.com/acs-final/Backend.git'  // GitHub 저장소 URL
                }
            }
        }


        stage('Copy Configs & Dockerfiles') {
            steps {
                script {
                    //sh "mkdir api-gateway/src/main/resources"
                    sh "cp /home/kevin/Backend/api-gateway/src/main/resources/application.yaml api-gateway/src/main/resources/application.yaml"

                    //sh "mkdir fairytale/src/main/resources"
                    sh "cp /home/kevin/Backend/fairytale/src/main/resources/application.yaml fairytale/src/main/resources/application.yaml"

                    //sh "mkdir bookstore/src/main/resources"
                    sh "cp /home/kevin/Backend/bookstore/src/main/resources/application.yaml bookstore/src/main/resources/application.yaml"

                    //sh "mkdir member/src/main/resources"
                    sh "cp /home/kevin/Backend/member/src/main/resources/application.yaml member/src/main/resources/application.yaml"

                    //sh "mkdir report/src/main/resources"
                    sh "cp /home/kevin/Backend/report/src/main/resources/application.yaml report/src/main/resources/application.yaml"

                    sh "cp /home/kevin/Backend/docker-compose.yaml docker-compose.yaml"


                }
            }
        }




        stage('Login to Harbor') {
            steps {
                script {
                    echo "Logging in to Harbor..."
                    sh "docker login -u ${HARBOR_CREDENTIALS_USR} -p ${HARBOR_CREDENTIALS_PSW} 192.168.2.141:443"
                }
            }
        }



        stage('K8S Manifest Update') {
            steps {
                git credentialsId: 'JONBERMAN',
                url: 'https://github.com/acs-final/manifest.git',
                branch: 'main'

                //sh 'git config user.email "hamo@gmail.com"'
                sh 'git config user.name "JONBERMAN"'
                sh 'git config credential.helper "cache --timeout=3600"'

                sh 'git pull --rebase origin main'

                dir('manifests/back/fairytale') {
                    sh "sed -i 's|image: 192.168.2.141:443/k8s-project/fairytale:.*|image: 192.168.2.141:443/k8s-project/fairytale:\${BUILD_NUMBER}|g' fairytale-deploy.yaml"
                }

                dir('manifests/back/bookstore') {
                    sh "sed -i 's|image: 192.168.2.141:443/k8s-project/bookstore:.*|image: 192.168.2.141:443/k8s-project/bookstore:${BUILD_NUMBER}|g' bookstore-deploy.yaml"
                }

                dir('manifests/back/member') {
                    sh "sed -i 's|image: 192.168.2.141:443/k8s-project/member:.*|image: 192.168.2.141:443/k8s-project/member:${BUILD_NUMBER}|g' member-deploy.yaml"
                }

                dir('manifests/back/report') {
                    sh "sed -i 's|image: 192.168.2.141:443/k8s-project/report:.*|image: 192.168.2.141:443/k8s-project/report:${BUILD_NUMBER}|g' report-deploy.yaml"
                }

                dir('manifests') {
                    sh """

                        git add front-deploy.yaml
                        git commit -m '[UPDATE] back-deploy ${BUILD_NUMBER} image versioning' || echo 'No changes to commit'
                        git push origin main
                    """
                }
            }
        }

    }

}
