pipeline {
    agent any
    environment {
        BUILD_NUMBER = "v3"
        HARBOR_CREDENTIALS = credentials('harbor')
        BACKEND_REPO = "https://github.com/acs-final/Backend.git"
        BACKEND_IMAGE_PREFIX = "192.168.2.141:443/prototype"
        LOCAL_CONFIG_BASE_PATH = "/home/kevin/Backend"  // 로컬에서 application.yaml을 가져올 기본 경로
    }

    stages {
        stage('Checkout Backend') {
            steps {
                script {
                    if (fileExists("Backend/.git")) {
                        sh """
                            cd Backend
                            git reset --hard
                            git clean -fd
                            git checkout develop
                            git pull origin develop
                        """
                    } else {
                        sh "git clone -b develop --single-branch ${BACKEND_REPO} Backend"
                    }
                }
            }
        }

        stage('Login to Harbor') {
            steps {
                script {
                    sh "docker login -u ${HARBOR_CREDENTIALS_USR} -p ${HARBOR_CREDENTIALS_PSW} 192.168.2.141:443"
                }
            }
        }

        stage('Build & Push All Backend Services') {
            steps {
                script {
                    def backendDirs = sh(script: "ls Backend", returnStdout: true).trim().split('\n')

                    for (dir in backendDirs) {
                        if (fileExists("Backend/${dir}/Dockerfile")) {
                            echo "Copying Dockerfile from ${dir} to Backend root..."
                            sh "cp Backend/${dir}/Dockerfile Backend/Dockerfile"

                            echo "Copying application.yaml for ${dir} from local to Backend root..."
                            sh "cp ${LOCAL_CONFIG_BASE_PATH}/${dir}/application.yaml Backend/src/main/resources/application.yaml"

                            def backendImage = "${BACKEND_IMAGE_PREFIX}/${dir}:${BUILD_NUMBER}"
                            echo "Building Docker image for Backend Service: ${dir}"
                            sh """
                                cd Backend
                                docker build -t ${backendImage} .
                                docker push ${backendImage}
                            """
                            echo "Backend Service ${dir} Push Success"
                        } else {
                            echo "No Dockerfile found in Backend Service ${dir}, skipping..."
                        }
                    }
                }
            }
        }
    }
}

