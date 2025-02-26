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
        stage('Checkout') {
            steps {
                script {
                    echo "Current workspace: ${pwd}"

                }
            }
        }

        stage('Detect Changed Services') {
            steps {
                script {
                    echo "Detecting changed services..."

                    // Git 최신 상태 동기화
                    sh "git config --global --add safe.directory /var/lib/jenkins/workspace/backend-docker-ci"
                    sh "git fetch origin develop"

                    // 브랜치의 마지막 성공 빌드와 비교
                    def lastSuccessfulCommit = sh(script: "git rev-parse refs/remotes/origin/develop", returnStdout: true).trim()
                    def changedFiles = sh(script: """
                        git diff --name-only origin/develop  # Uncommitted changes
                        sh "echo ${git diff --name-only origin/develop}"

                    """, returnStdout: true).trim().split('\n')

                    echo "changedFiles: ${changedFiles}"

                    def changedServices = []
                    for (file in changedFiles) {
                        echo "file in changedFiles: ${file}"

                        // 루트 레벨 변경사항 체크
                        if (!file.contains('/')) {
                            changedServices.add('root')
                            continue
                        }
                        
                        // 서비스 디렉토리 체크
                        def servicePath = file.split('/')
                        echo "servicePath: ${servicePath}"
                        if (servicePath.length >= 2) {
                            def serviceName = servicePath[0]
                            echo "serviceName: ${serviceName}"
                            if (!changedServices.contains(serviceName)) {
                                changedServices.add(serviceName)
                            }
                        }
                        
                        // 공통 모듈 체크
                        if (file.contains('common/') || file.contains('shared/')) {
                            // 공통 모듈이 변경된 경우 모든 서비스를 다시 빌드
                            sh "cd Backend && ls -d */ | sed 's#/##'".split('\n').each { service ->
                                if (!changedServices.contains(service)) {
                                    changedServices.add(service)
                                }
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
                    // 변경된 모듈 목록을 환경 변수로 저장 (다음 단계에서 사용)
                    env.BUILD_MODULES = changedServices.join(',')
                }
            }
        }

        // 변경된 모듈만 빌드
        stage('Build Changed Modules') {
            steps {
                script {
                    def buildModules = ['api-gateway', 'bookstore', 'fairytale', 'member', 'report']
                    def changedServices = env.CHANGED_SERVICES.split(',')

                    def modulesToBuild = changedServices.findAll { it in buildModules }

                    if (modulesToBuild.isEmpty()) {
                        echo "No relevant modules changed, skipping build."
                        return
                    }

                    for (module in modulesToBuild) {
                        echo "Building module: ${module}"
                        sh "chmod +x gradlew"
                        sh "./gradlew :${module}:build --no-daemon -x test"
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