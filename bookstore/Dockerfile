# Stage 1: Build
FROM openjdk:17-jdk-slim AS build
WORKDIR /app

# Gradle 프로젝트 파일 복사
COPY --chmod=755 gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src


# 의존성 다운로드 및 애플리케이션 빌드
RUN ./gradlew build --no-daemon -x test

# Stage 2: Runtime
FROM openjdk:17-jdk-slim
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 컨테이너 포트 지정
EXPOSE 8083

# JAR 파일 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
