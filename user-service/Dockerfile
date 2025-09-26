FROM container-registry.oracle.com/java/openjdk:24.0.2-oraclelinux9
WORKDIR /app
COPY target/user-service-0.0.1-SNAPSHOT.jar /app/user-service.jar
EXPOSE 8812

ENTRYPOINT ["java","-jar","/app/user-service.jar"]