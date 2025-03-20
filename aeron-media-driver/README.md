# steps - to run and access container
docker build -t aeron-media-driver:latest .
docker run -d --name aeron-media-driver aeron-media-driver:latest
docker exec -it aeron-media-driver /bin/bash
docker exec -it aeron-media-driver /bin/sh
docker stop aeron-media-driver  
docker rm aeron-media-driver
docker ps

# kill all docker
docker kill $(docker ps -q)
#force
docker ps -q | xargs docker rm -f
