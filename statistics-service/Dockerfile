FROM container-registry.oracle.com/java/openjdk:24.0.2-oraclelinux9
WORKDIR /app

COPY target/statistics-service-*.jar /app/statistics-service.jar

EXPOSE 8799

ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar /app/statistics-service.jar"]
