# Stage 2: Runtime
FROM openjdk:17-jdk-slim
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY fairytale/build/libs/fairytale-0.0.1-SNAPSHOT.jar app.jar

# 컨테이너 포트 지정
EXPOSE 8080

# JAR 파일 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
