FROM container-registry.oracle.com/java/openjdk:24.0.2-oraclelinux9
WORKDIR /app
COPY target/vocabulary-command-service-0.0.1-SNAPSHOT.jar /app/vocabulary-command-service.jar
EXPOSE 8810

ENTRYPOINT ["java","-jar","/app/vocabulary-command-service.jar"]