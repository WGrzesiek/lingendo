FROM container-registry.oracle.com/java/openjdk:24.0.2-oraclelinux9
WORKDIR /app
COPY build/libs/koog-service-0.0.1-SNAPSHOT.jar /app/koog-service.jar
EXPOSE 7777

ENTRYPOINT ["java","-jar","/app/koog-service.jar"]
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/koog-service.jar"]
