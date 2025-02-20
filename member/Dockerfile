# Stage 1: Build
FROM openjdk:17-jdk-slim AS build
WORKDIR /app

# Gradle 프로젝트 파일 복사
COPY . .

# gradlew 파일에 실행 권한 부여
RUN chmod +x gradlew

# 의존성 다운로드 및 애플리케이션 빌드
RUN ./gradlew :member:build --no-daemon -x test

# Stage 2: Runtime
FROM openjdk:17-jdk-slim
WORKDIR /app

# 🔥 특정 JAR 파일 하나만 복사
COPY --from=build /app/member/build/libs/member-service.jar /app/app.jar

# 컨테이너 포트 지정
EXPOSE 8080

# JAR 파일 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

