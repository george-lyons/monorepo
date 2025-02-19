FROM eclipse-temurin:20-jdk

WORKDIR /app

# Create logs directory
RUN mkdir -p logs && chmod 777 logs

# Default command with proper module access and logging
ENTRYPOINT ["sh", "-c", "exec java \
    --add-opens=java.base/jdk.internal.misc=ALL-UNNAMED \
    --add-opens=java.base/java.nio=ALL-UNNAMED \
    -Dlog4j2.debug=true \
    -jar app.jar"] 