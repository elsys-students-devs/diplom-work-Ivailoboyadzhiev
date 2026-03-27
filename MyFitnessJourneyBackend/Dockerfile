# === Build stage ===
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

ENV MAVEN_OPTS="-Xmx2048m"

COPY pom.xml .

# Download dependencies (this will cache them if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application with explicit compiler settings
RUN mvn clean compile -B && \
    mvn package -DskipTests -B

# === Run stage ===
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
