# Stage 1: Build the Fat JAR using Maven and OpenJDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build executable jar
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=builder /app/target/quickbite-api-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]