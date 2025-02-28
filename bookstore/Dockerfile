# Stage 2: Runtime
FROM openjdk:17-jdk-slim
WORKDIR /app



# 1. 비루트 사용자(app)를 생성합니다.
RUN adduser --disabled-password --gecos "" app

# 빌드된 JAR 파일 복사
COPY bookstore/build/libs/bookstore-0.0.1-SNAPSHOT.jar app.jar

# OpenTelemetry Java 에이전트 다운로드
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.30.0/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

# 3.5. curl 설치 (HEALTHCHECK에서 사용)
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

RUN chmod 644 /app/opentelemetry-javaagent.jar && chown app:app /app/opentelemetry-javaagent.jar

# 4. HEALTHCHECK 추가
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD curl -f http://192.168.2.141:3000/ || exit 1

# 5. non-root 사용자(app)로 전환
USER app

# 컨테이너 포트 지정
EXPOSE 8080

# JAR 파일 실행
ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-jar", "app.jar"]
