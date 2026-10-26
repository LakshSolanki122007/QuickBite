# Stage 1: Build JAR with Maven
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests && \
    cp "$(ls -S target/*.jar | head -n 1)" target/app.jar

# Stage 2: Multi-Arch Runtime Image (ARM64 & AMD64 compatible)
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/target/app.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
