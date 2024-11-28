# Use a slim base image with JDK for building
FROM openjdk:21-jdk-slim as builder

# Set the working directory for the build process
WORKDIR /app

# Copy the Gradle wrapper and project files to the container
COPY ./gradlew ./gradlew
COPY ./gradle ./gradle
COPY ./build.gradle ./build.gradle
COPY ./settings.gradle ./settings.gradle
COPY ./src ./src

# Grant execute permissions for the Gradle wrapper
RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew bootJar --no-daemon

# Use a slim base image for the final runtime
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port your application listens on
EXPOSE 8080

# Define the command to run your Spring Boot application
CMD ["java", "-jar", "app.jar"]
