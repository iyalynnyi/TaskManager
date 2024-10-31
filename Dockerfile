# Use OpenJDK 17 as the base image for building
FROM eclipse-temurin:17-jdk-jammy as builder

# Set the working directory
WORKDIR /app

# Copy the Gradle build files
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Copy the Gradle wrapper script and make it executable
COPY gradlew ./
RUN chmod +x gradlew

RUN ./gradlew resolveAllDependencies

# Copy the source code
COPY src ./src

# Build the application
RUN ./gradlew build

# Create a new image for running the application
FROM eclipse-temurin:17-jdk-jammy

# Set the working directory
WORKDIR /app

# Copy the built jar file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8090

# Set the entry point for the container
ENTRYPOINT ["java", "-jar", "app.jar"]
