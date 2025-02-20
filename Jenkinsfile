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

        stage('Detect Changed Services') {
            steps {
                script {
                    echo "Detecting changed services..."
                    def changedFiles = sh(script: "cd Backend && git diff --name-only HEAD~1", returnStdout: true).trim().split('\n')

                    def changedServices = []
                    for (file in changedFiles) {
                        def match = file =~ /^(.+?)\//
                        if (match) {
                            def serviceName = match[0][1]
                            if (!changedServices.contains(serviceName)) {
                                changedServices.add(serviceName)
                            }
                        }
                    }

                    if (changedServices.isEmpty()) {
                        echo "No changes detected. Skipping build."
                        currentBuild.result = 'SUCCESS'
                        return
                    }

                    echo "Changed services: ${changedServices.join(', ')}"
                    env.CHANGED_SERVICES = changedServices.join(',')
                }
            }
        }

        stage('Copy Configs & Dockerfiles') {
            steps {
                script {
                    def changedServices = env.CHANGED_SERVICES.split(',')

                    for (service in changedServices) {
                        def configPath = "${LOCAL_CONFIG_BASE_PATH}/${service}/src/main/resources/application.yaml"
                        def targetDir = "Backend/${service}/src/main/resources"
                        def targetPath = "${targetDir}/application.yaml"

                        if (fileExists(configPath)) {
                            echo "Copying application.yaml for ${service}..."
                            sh "mkdir -p ${targetDir}"
                            sh "cp ${configPath} ${targetPath}"
                        } else {
                            echo "WARNING: ${configPath} not found, skipping..."
                        }

                        // Dockerfile 복사
                        def dockerfilePath = "Backend/${service}/Dockerfile"
                        if (fileExists(dockerfilePath)) {
                            echo "Copying Dockerfile for ${service} to Backend root..."
                            sh "cp ${dockerfilePath} Backend/Dockerfile"
                        } else {
                            echo "WARNING: ${dockerfilePath} not found, skipping..."
                        }
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

        stage('Build Changed Services') {
            steps {
                script {
                    def changedServices = env.CHANGED_SERVICES.split(',')

                    for (service in changedServices) {
                        echo "Building ${service}..."
                        sh """
                            cd Backend
                            docker build -t ${BACKEND_IMAGE_PREFIX}/${service}:${BUILD_NUMBER} .
                        """
                    }
                }
            }
        }

        stage('Push Built Images to Harbor') {
            steps {
                script {
                    def changedServices = env.CHANGED_SERVICES.split(',')

                    for (service in changedServices) {
                        def backendImage = "${BACKEND_IMAGE_PREFIX}/${service}:${BUILD_NUMBER}"
                        echo "Pushing Docker image: ${backendImage}"
                        sh "docker push ${backendImage}"
                    }
                }
            }
        }
    }
}

