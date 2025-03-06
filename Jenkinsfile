pipeline {
    agent any
    environment {
        BUILD_NUMBER = "v23"
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

                    git branch: 'main',
                        credentialsId: 'github-token',  // Jenkins에 등록한 GitHub Credentials ID
                        url: 'https://github.com/acs-final/Backend.git'  // GitHub 저장소 URL
                }
            }
        }


        stage('Copy Configs & Dockerfiles') {
            steps {
                script {

                    sh """
                    if [ ! -d "api-gateway/src/main/resources/" ]; then
                        mkdir -p "api-gateway/src/main/resources/"
                        mkdir fairytale/src/main/resources
                        mkdir bookstore/src/main/resources
                        mkdir member/src/main/resources
                        mkdir report/src/main/resources
                        echo "📁 디렉토리를 생성했습니다: api-gateway/src/main/resources/"
                    else
                        echo "✅ 디렉토리가 이미 존재합니다: api-gateway/src/main/resources/"
                    fi
                    """

                    sh "cp /home/kevin/Backend/api-gateway/src/main/resources/application.yaml api-gateway/src/main/resources/application.yaml"

                    sh "cp /home/kevin/Backend/fairytale/src/main/resources/application.yaml fairytale/src/main/resources/application.yaml"

                    sh "cp /home/kevin/Backend/bookstore/src/main/resources/application.yaml bookstore/src/main/resources/application.yaml"

                    sh "cp /home/kevin/Backend/member/src/main/resources/application.yaml member/src/main/resources/application.yaml"

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
                    try {
                        timeout(time: 2, unit: 'MINUTES') {
                            def qg = waitForQualityGate()
                            if (qg.status != 'OK') {
                                echo "Quality Gate failed with status: ${qg.status}"
                            }
                        }
                    } catch (Exception e) {
                        echo "Quality Gate check failed: ${e.message}"
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
                        sh "docker tag backend-docker-ci_${service} ${BACKEND_IMAGE_PREFIX}/${service}:${BUILD_NUMBER}"

                        def backendImage = "${BACKEND_IMAGE_PREFIX}/${service}:${BUILD_NUMBER}"
                        echo "Pushing Docker image: ${backendImage}"
                        sh "docker push ${backendImage}"
                    }
                }
            }
        }

        stage('K8S Manifest Update') {
            steps {
                git credentialsId: 'JONBERMAN',
                    url: 'https://github.com/acs-final/manifest.git',
                    branch: 'main'

                sh 'git config user.email "hamo2814@gmail.com"'
                sh 'git config user.name "JONBERMAN"'
                sh 'git config credential.helper "cache --timeout=3600"'

                sh 'git pull --rebase origin main'


                dir('back/fairytale') {
                    echo "Current workspace: ${pwd}"
                    sh "sed -i 's|image: 192.168.2.141:443/k8s-project/fairytale:.*|image: 192.168.2.141:443/k8s-project/fairytale:${BUILD_NUMBER}|g' fairytale-deploy.yaml"
                }

                dir('back/bookstore') {
                    echo "Current workspace: ${pwd}"
                    sh "sed -i 's|image: 192.168.2.141:443/k8s-project/bookstore:.*|image: 192.168.2.141:443/k8s-project/bookstore:${BUILD_NUMBER}|g' bookstore-deploy.yaml"
                }

                dir('back/member') {
                    echo "Current workspace: ${pwd}"
                    sh "sed -i 's|image: 192.168.2.141:443/k8s-project/member:.*|image: 192.168.2.141:443/k8s-project/member:${BUILD_NUMBER}|g' member-deploy.yaml"
                }

                dir('back/report') {
                    echo "Current workspace: ${pwd}"
                    sh "sed -i 's|image: 192.168.2.141:443/k8s-project/report:.*|image: 192.168.2.141:443/k8s-project/report:${BUILD_NUMBER}|g' report-deploy.yaml"
                }

                dir('') {
                    sh """
                        echo "Current workspace: ${pwd}"

                        git status
                        git add back/bookstore/bookstore-deploy.yaml back/fairytale/fairytale-deploy.yaml back/member/member-deploy.yaml back/report/report-deploy.yaml
                        git commit -m '[UPDATE] back-deploy ${BUILD_NUMBER} image versioning' || echo 'No changes to commit'
                        git push origin main
                    """
                }
            }
        }

    }

}
