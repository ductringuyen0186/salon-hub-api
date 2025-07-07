# Use a Java runtime as the base image
FROM eclipse-temurin:17-jre

# Set the working directory
WORKDIR /app

# Copy the built jar file (update the name if needed)
COPY build/libs/salon-hub-api-0.0.1-SNAPSHOT.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]