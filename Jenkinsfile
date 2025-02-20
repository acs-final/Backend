pipeline {
    agent any
    environment {
        BUILD_NUMBER = "v2"
        HARBOR_CREDENTIALS = credentials('harbor')
        BACKEND_REPO = "https://github.com/acs-final/Backend.git"
        BACKEND_IMAGE_PREFIX = "192.168.2.141:443/prototype"
        LOCAL_CONFIG_BASE_PATH = "/home/kevin/Backend"
        LOCAL_COMPOSE_FILE_PATH = "/home/kevin/docker-compose.yaml"  // 루트 디렉터리에 있는 docker-compose.yml 경로
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

        stage('Copy Configs & Docker Compose') {
            steps {
                script {
                    def backendDirs = sh(script: "ls Backend", returnStdout: true).trim().split('\n')

                    for (dir in backendDirs) {
                        if (fileExists("Backend/${dir}/Dockerfile")) {
                            echo "Processing Backend Service: ${dir}"

                            // application.yaml 복사 전 존재 여부 확인
                            def configPath = "${LOCAL_CONFIG_BASE_PATH}/${dir}/application.yaml"
                            def targetPath = "Backend/${dir}/src/main/resources/application.yaml"

                            if (fileExists(configPath)) {
                                echo "Copying application.yaml for ${dir}..."
                                sh "cp ${configPath} ${targetPath}"
                                echo "Copied successfully: ${targetPath}"
                            } else {
                                echo "WARNING: ${configPath} not found, skipping..."
                            }
                        } else {
                            echo "No Dockerfile found in Backend Service ${dir}, skipping..."
                        }
                    }

                    // docker-compose.yml 복사 전 존재 여부 확인
                    if (fileExists(LOCAL_COMPOSE_FILE_PATH)) {
                        echo "Copying docker-compose.yml from root to Backend directory..."
                        sh "cp ${LOCAL_COMPOSE_FILE_PATH} Backend/docker-compose.yaml"
                        echo "Copied successfully: Backend/docker-compose.yaml"
                    } else {
                        echo "WARNING: ${LOCAL_COMPOSE_FILE_PATH} not found, skipping..."
                    }
                }
            }
        }

        stage('Build All Services using Docker Compose') {
            steps {
                script {
                    sh """
                        cd Backend
                        docker-compose build
                    """
                }
            }
        }

        stage('Push Built Images to Harbor') {
            steps {
                script {
                    def backendDirs = sh(script: "ls Backend", returnStdout: true).trim().split('\n')

                    for (dir in backendDirs) {
                        def backendImage = "${BACKEND_IMAGE_PREFIX}/${dir}:${BUILD_NUMBER}"
                        if (fileExists("Backend/${dir}/Dockerfile")) {
                            echo "Pushing Docker image: ${backendImage}"
                            sh "docker push ${backendImage}"
                            echo "Pushed successfully: ${backendImage}"
                        } else {
                            echo "Skipping push for ${dir}, no Dockerfile found."
                        }
                    }
                }
            }
        }
    }
}

