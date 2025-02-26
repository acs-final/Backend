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

        // 변경된 모듈만 빌드
        stage('Build Changed Modules') {
            steps {
                script {
                    def buildModules = ['api-gateway', 'bookstore', 'fairytale', 'member', 'report']

                    for (module in buildModules) {
                        echo "Building module: ${module}"
                        sh "chmod +x gradlew"

                        sh "./gradlew --stop"
                        sh "./gradlew clean"

                        sh "./gradlew :${module}:build --no-daemon -x test"
                    }
                    env.MODULES_TO_BUILD = buildModules.join(',')
                }
            }
        }

        // 빌드 전 소나큐브 분석 단계
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('MySonarQube') {
                    script {
                        def buildModules = ['api-gateway', 'bookstore', 'fairytale', 'member', 'report']

                        // SonarQube에 분석할 경로 설정 (변경된 모듈만 추가)
                        def sourcePaths = buildModules.collect { "${it}/src/main/java" }.join(',')
                        def binaryPaths = buildModules.collect { "${it}/build/classes/java/main" }.join(',')

                        echo "Running SonarQube scan for modules: ${buildModules}"

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

        // 2. Quality Gate 결과 확인 단계
        stage('Quality Gate') {
            steps {
                script {
                    // 분석 결과가 처리될 때까지 대기 (기본적으로 최대 2분 대기)
                    timeout(time: 2, unit: 'MINUTES') {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            // 품질 게이트가 통과되지 않으면 빌드 실패 처리
                            error "Pipeline aborted due to Quality Gate failure: ${qg.status}"
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

                    sh "docker-compose -f docker-compose.yaml build --no-cache"

                }
            }
        }

        stage('Push Built Images to Harbor') {
            steps {
                script {
                    def changedServices = env.MODULES_TO_BUILD.split(',')

                    for (service in changedServices) {
                        sh "docker tag backend-${service} ${BACKEND_IMAGE_PREFIX}/${service}:${BUILD_NUMBER}"

                        def backendImage = "${BACKEND_IMAGE_PREFIX}/${service}:${BUILD_NUMBER}"
                        echo "Pushing Docker image: ${backendImage}"
                        sh "docker push ${backendImage}"
                    }
                }
            }
        }
    }

}
