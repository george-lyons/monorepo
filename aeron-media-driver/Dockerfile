FROM eclipse-temurin:20-jdk

# Create app directory
RUN mkdir -p /app/logs

# Copy application files
COPY target/aeron-media-driver-1.0-SNAPSHOT-jar-with-dependencies.jar /app/app.jar
COPY src/main/resources/app.sh /app/app.sh

# Make script executable
RUN chmod +x /app/app.sh

# Set working directory
WORKDIR /app

# Keep container running
ENTRYPOINT ["tail", "-f", "/dev/null"]