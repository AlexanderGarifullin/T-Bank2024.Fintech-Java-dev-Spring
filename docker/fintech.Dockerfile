# Use a slim base image to reduce the size of the final image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the application JAR file into the container
COPY ./build/libs/*.jar app.jar

# Expose the port your application listens on (adjust if needed)
EXPOSE 8080

# Define the command to run your Spring Boot application
CMD ["java", "-jar", "app.jar"]