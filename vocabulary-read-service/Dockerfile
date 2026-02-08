FROM container-registry.oracle.com/java/openjdk:24.0.2-oraclelinux9
WORKDIR /app

COPY target/vocabulary-read-service-*.jar /app/vocabulary-read-service.jar

EXPOSE 8813 9099

ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar /app/vocabulary-read-service.jar"]
