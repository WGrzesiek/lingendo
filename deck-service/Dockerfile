FROM container-registry.oracle.com/java/openjdk:24.0.2-oraclelinux9
WORKDIR /app

COPY target/deck-service-*.jar /app/deck-service.jar

EXPOSE 8814

ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar /app/deck-service.jar"]
