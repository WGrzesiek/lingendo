FROM gradle:8.12-jdk21 AS builder

WORKDIR /app


COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle


RUN gradle dependencies --no-daemon || true

COPY src ./src

RUN gradle bootJar --no-daemon -x test

FROM container-registry.oracle.com/java/openjdk:21-oraclelinux9

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar /app/koog-service.jar

RUN groupadd -r koog && useradd -r -g koog koog
USER koog


HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:7777/actuator/health || exit 1

EXPOSE 7777

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/koog-service.jar"]
