#!/bin/bash

# Configuration
APP_NAME="aeron-media-driver"
APP_JAR="aeron-media-driver-1.0-SNAPSHOT-jar-with-dependencies.jar"
PID_FILE="/app/app.pid"
LOG_FILE="/app/logs/app.log"

# JVM options for optimal Media Driver performance
JVM_OPTS="-XX:+UnlockExperimentalVMOptions \
          -XX:+UseEpsilonGC \
          -XX:+AlwaysPreTouch \
          -XX:+UseNUMA"

# Function to check if app is running
is_running() {
    [ -f "$PID_FILE" ] && ps -p $(cat "$PID_FILE") > /dev/null 2>&1
}

# Function to start the app
start() {
    if is_running; then
        echo "Application is already running."
        return 1
    fi
    
    echo "Starting $APP_NAME..."
    java $JVM_OPTS -jar $APP_JAR > $LOG_FILE 2>&1 &
    echo $! > "$PID_FILE"
    sleep 2
    
    if is_running; then
        echo "$APP_NAME started successfully"
    else
        echo "Failed to start $APP_NAME"
        rm -f "$PID_FILE"
        return 1
    fi
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
    sleep 2
    
    if ! is_running; then
        echo "$APP_NAME stopped successfully"
    else
        echo "Failed to stop $APP_NAME"
        return 1
    fi
}

# Function to restart the app
restart() {
    stop
    sleep 2
    start
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
        restart
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