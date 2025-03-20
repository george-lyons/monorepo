# steps - to run and access container
docker build -t execution-service:latest .
docker run -d --name execution-service execution-service:latest
docker exec -it execution-service /bin/bash
docker exec -it execution-service /bin/sh
docker stop execution-service  
docker rm execution-service

