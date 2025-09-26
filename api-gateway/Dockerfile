FROM container-registry.oracle.com/java/openjdk:24.0.2-oraclelinux9
WORKDIR /app
COPY target/api-gateway-*.jar /app/api-gateway.jar
EXPOSE 8811
ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar /app/api-gateway.jar"]
