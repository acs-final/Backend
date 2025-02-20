# Stage 1: Build
FROM openjdk:17-jdk-slim AS build
WORKDIR /app

# Gradle 프로젝트 파일 복사
COPY . .

# gradlew 파일에 실행 권한 부여 (chmod 755)
RUN chmod +x gradlew

# 의존성 다운로드 및 애플리케이션 빌드
RUN ./gradlew :member:build --no-daemon -x test


# Stage 2: Runtime
FROM openjdk:17-jdk-slim
WORKDIR /app


# 1. 비루트 사용자(app)를 생성합니다.
RUN adduser --disabled-password --gecos "" app
# 2. 빌드 단계에서 생성된 JAR 파일을 non-root 사용자 소유로 복사합니다.
COPY --from=build --chown=app:app /app/member/build/libs/*.jar app.jar
# 3. 컨테이너 포트 노출
EXPOSE 8080
# 4. non-root 사용자(app)로 전환
USER app


# JAR 파일 실행
ENTRYPOINT ["java", "-jar", "app.jar"]

