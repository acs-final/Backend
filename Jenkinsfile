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

                    sh "whoami"
                    sh "ls -la /var/lib/jenkins/workspace/backend-docker-ci/.git"

                    sh "sudo chown -R jenkins:jenkins /var/lib/jenkins/workspace/backend-docker-ci"
                    sh "sudo chmod -R u+rwx /var/lib/jenkins/workspace/backend-docker-ci"


                    dir("/var/lib/jenkins/workspace/backend-docker-ci") {
                        // Git 최신 상태 동기화
                        sh "git config --global --add safe.directory /var/lib/jenkins/workspace/backend-docker-ci"
                        sh "git fetch origin develop"

                        echo "Current workspace: ${pwd}"

                        // 브랜치의 마지막 성공 빌드와 비교
                        //def lastSuccessfulCommit = sh(script: "git rev-parse refs/remotes/origin/develop", returnStdout: true).trim()
                        def changedFiles = sh(script: """
                            git diff --name-only HEAD origin/develop  # Uncommitted changes
                        """, returnStdout: true).trim().split('\n')

                        if (changedFiles) {
                            echo "changedFiles: ${changedFiles}"
                        } else {
                            echo "changedFiles is null"
                        }

                        sh "pwd"
                        sh "ls -la"

                        sh "git pull origin develop"
                    }



                    //

                    def changedServices = []
                    for (file in changedFiles) {
                        echo "file in changedFiles: ${file}"

                        // 루트 레벨 변경사항 체크
                        if (!file.contains('/')) {
                            changedServices.add('root')
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
                    echo "Current workspace: ${pwd}"
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
                    env.MODULES_TO_BUILD = modulesToBuild.join(',')
                }
            }
        }

        // 빌드 전 소나큐브 분석 단계
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('MySonarQube') {
                    script {
                        def buildModules = ['api-gateway', 'bookstore', 'fairytale', 'member', 'report']
                        def changedServices = env.BUILD_MODULES.split(',')
                        echo "changeServices: ${changedServices}"
                        def modulesToScan = changedServices.findAll { it in buildModules }

                        if (modulesToScan.isEmpty()) {
                            echo "No relevant modules changed, skipping SonarQube analysis."
                            return
                        }

                        // SonarQube에 분석할 경로 설정 (변경된 모듈만 추가)
                        def sourcePaths = modulesToScan.collect { "Backend/${it}/src/main/java" }.join(',')
                        def binaryPaths = modulesToScan.collect { "Backend/${it}/build/classes/java/main" }.join(',')

                        echo "Running SonarQube scan for modules: ${modulesToScan}"

                        def scannerHome = tool 'LocalSonarScanner'

                        sh """
                        ${scannerHome}/bin/sonar-scanner \
                          -Dsonar.projectKey=my_project_key \
                          -Dsonar.projectName=MyProject_backend \
                          -Dsonar.projectVersion=1.0 \
                          -Dsonar.sources=${sourcePaths} \
                          -Dsonar.java.binaries=${binaryPaths} \
                          -Dsonar.host.url=http://192.168.3.131:9000 \

                        """
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
                    def changedServices = env.MODULES_TO_BUILD.split(',')

                    echo "changedServices: ${changedServices}"

                    for (service in changedServices) {
                        echo "Building ${service}..."
                        sh """
                            echo "Current workspace: ${pwd}"
                            docker build -t ${BACKEND_IMAGE_PREFIX}/${service}:${BUILD_NUMBER} -f Backend/Dockerfile Backend/
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