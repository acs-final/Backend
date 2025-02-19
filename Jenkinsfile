pipeline {
    agent any
    environment {
        BUILD_NUMBER = "v2"  // 빌드 번호
        HARBOR_CREDENTIALS = credentials('harbor') // Jenkins에 등록한 Harbor Credentials ID
        BACKEND_REPO = "https://github.com/acs-final/Backend.git"  // 백엔드 Git 저장소 URL
        BACKEND_IMAGE_PREFIX = "192.168.2.141:443/prototype"  // 백엔드 Docker 이미지 기본 경로
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
                            git checkout main
                            git pull origin main
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

        stage('Detect Changed Backend Directories') {
            steps {
                script {
                    def changedDirs = sh(script: "cd Backend && git diff --name-only HEAD~1 | awk -F'/' '{print \$1}' | sort -u", returnStdout: true).trim().split('\n')
                    env.CHANGED_DIRS = changedDirs.join(' ')
                    echo "Changed Backend Directories: ${env.CHANGED_DIRS}"
                }
            }
        }

        stage('Build & Push Changed Backend Services') {
            steps {
                script {
                    def dirs = env.CHANGED_DIRS.split(' ')
                    for (dir in dirs) {
                        if (fileExists("Backend/${dir}/Dockerfile")) {
                            def backendImage = "${BACKEND_IMAGE_PREFIX}/${dir}:${BUILD_NUMBER}"
                            echo "Building Docker image for Backend Service: ${dir}"
                            sh """
                                cd Backend/${dir}
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

