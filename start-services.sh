#!/bin/bash
set -e  # Stop script on first error

#docker-compose down -v  # Remove volumes
#docker volume create aeron-data  # Create fresh volume

echo "🔴 Killing all running containers..."
docker kill $(docker ps -q) 2>/dev/null || true
docker container prune -f

# Ensure Aeron directory exists with proper permissions
mkdir -p /tmp/aeron/logs
chmod 777 /tmp/logs/aeron

echo "🔄 Rebuilding and starting all services..."
docker-compose up -d --build

echo "✅ All services started. Checking status..."
docker-compose ps