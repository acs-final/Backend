pipeline {
    agent any
    environment {
        BUILD_NUMBER = "v4"
        HARBOR_CREDENTIALS = credentials('harbor')
        BACKEND_REPO = "https://github.com/acs-final/Backend.git"
        BACKEND_IMAGE_PREFIX = "192.168.2.141:443/prototype"
        LOCAL_CONFIG_BASE_PATH = "/home/kevin/Backend"
        LOCAL_COMPOSE_FILE_PATH = "/home/kevin/Backend/docker-compose.yaml"
    }

    stages {
        stage('Checkout Backend') {
            steps {
                script {
                    echo "Removing old Backend directory..."
                    sh "rm -rf Backend"

                    echo "Cloning Backend repository..."
                    sh "git clone -b develop --single-branch ${BACKEND_REPO} Backend"
                }
            }
        }

        stage('Copy Configs & Docker Compose') {
            steps {
                script {
                    def backendDirs = sh(script: "ls Backend", returnStdout: true).trim().split('\n')

                    for (dir in backendDirs) {
                        echo "Processing Backend Service: ${dir}"

                        // 🔥 ✅ 수정된 로컬 경로: /home/kevin/Backend/${dir}/src/main/resources/application.yaml
                        def configPath = "${LOCAL_CONFIG_BASE_PATH}/${dir}/src/main/resources/application.yaml"
                        def targetDir = "Backend/${dir}/src/main/resources"
                        def targetPath = "${targetDir}/application.yaml"

                        if (fileExists(configPath)) {
                            echo "Copying application.yaml for ${dir}..."
                            sh "mkdir -p ${targetDir}"
                            sh "cp ${configPath} ${targetPath}"
                            echo "Copied successfully: ${targetPath}"
                        } else {
                            echo "WARNING: ${configPath} not found, skipping..."
                        }
                    }

                    // docker-compose.yaml 복사
                    if (fileExists(LOCAL_COMPOSE_FILE_PATH)) {
                        echo "Copying docker-compose.yaml to Backend directory..."
                        sh "cp ${LOCAL_COMPOSE_FILE_PATH} Backend/docker-compose.yaml"
                        echo "Copied successfully: Backend/docker-compose.yaml"
                    } else {
                        echo "WARNING: ${LOCAL_COMPOSE_FILE_PATH} not found, skipping..."
                    }
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

        stage('Build All Services using Docker Compose') {
            steps {
                script {
                    echo "Building all services using Docker Compose..."
                    sh """
                        cd Backend
                        /usr/local/bin/docker-compose build
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
                        echo "Pushing Docker image: ${backendImage}"
                        sh "docker push ${backendImage}"
                        echo "Pushed successfully: ${backendImage}"
                    }
                }
            }
        }
    }
}

