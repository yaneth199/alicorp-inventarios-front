FROM maven:3.9.9-eclipse-temurin-17 AS source
WORKDIR /build
COPY pom.xml ./
RUN mvn -B -ntp dependency:go-offline
COPY src ./src

FROM source AS test-runner
CMD ["mvn", "-B", "-ntp", "verify", "-Pintegration"]

FROM source AS build
# Unit tests must pass: no skipTests flag.
RUN mvn -B -ntp clean package
COPY docker/Healthcheck.java /tmp/Healthcheck.java
RUN javac -d /healthcheck /tmp/Healthcheck.java

FROM eclipse-temurin:17-jre-jammy AS runtime
WORKDIR /app
COPY --from=build --chown=10001:10001 /build/target/sigpi-alicorp-1.0.0.jar /app/app.jar
COPY --from=build --chown=10001:10001 /healthcheck /app/healthcheck
USER 10001:10001
EXPOSE 8080
HEALTHCHECK --interval=10s --timeout=6s --start-period=60s --retries=12 CMD ["java", "-cp", "/app/healthcheck", "Healthcheck"]
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
