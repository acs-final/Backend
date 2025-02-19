# Stage 1: Build
FROM openjdk:17-jdk-slim AS build
WORKDIR /app

# 전체 파일 복사
COPY . .

# gradlew 파일에 실행 권한 부여 (chmod 755)
RUN chmod +x gradlew

# 필요한 모듈만 빌드. 빌드 시 필요한 의존성이 주입 되어 jar 파일에 크기에 반영됨.
RUN ./gradlew :fairytale:build --no-daemon -x test

# Stage 2: Runtime
FROM openjdk:17-jdk-slim
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=build /app/fairytale/build/libs/*.jar app.jar

# 컨테이너 포트 지정
EXPOSE 8080

# JAR 파일 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
