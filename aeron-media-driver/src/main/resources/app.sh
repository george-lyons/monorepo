#!/bin/bash

# Configuration
APP_NAME="aeron-media-driver"
APP_JAR="/app/var/media_driver.jar"
PID_FILE="/app/app.pid"
LOG_FILE="/app/logs/app.log"

# JVM options for optimal Media Driver performance
JVM_OPTS="-XX:+UnlockExperimentalVMOptions \
          -XX:+UseEpsilonGC \
          -XX:+AlwaysPreTouch \
          -XX:+UseNUMA \
          --add-opens=java.base/jdk.internal.misc=ALL-UNNAMED \
          --add-opens=java.base/java.nio=ALL-UNNAMED \
          -Dlog4j.configurationFile=/app/etc/log4j2.xml"

# Function to check if app is running
is_running() {
    [ -f "$PID_FILE" ] && ps -p $(cat "$PID_FILE") > /dev/null 2>&1
}

# Function to start the app
start() {
    echo "Starting $APP_NAME..."
    mkdir -p $(dirname "$LOG_FILE")

    exec java $JVM_OPTS -jar $APP_JAR >> $LOG_FILE 2>&1
}

# Function to stop the app
stop() {
    if ! is_running; then
        echo "Application is not running."
        return 0
    fi
    
    echo "Stopping $APP_NAME..."
    kill $(cat "$PID_FILE")
    rm -f "$PID_FILE"
    
    echo "$APP_NAME stopped"
}

# Function to show status
status() {
    if is_running; then
        echo "$APP_NAME is running (PID: $(cat "$PID_FILE"))"
    else
        echo "$APP_NAME is not running"
    fi
}

# Show usage if no arguments provided
if [ $# -eq 0 ]; then
    echo "Usage: $0 {start|stop|restart|status}"
    exit 1
fi

# Handle commands
case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        stop
        start
        ;;
    status)
        status
        ;;
    *)
        echo "Usage: $0 {start|stop|restart|status}"
        exit 1
        ;;
esac

exit 0 