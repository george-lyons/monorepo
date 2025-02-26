docker image build -t docker-java-jar:latest .
docker run docker-java-jar:latest


docker build -t marketdata:latest .
docker run --rm -it --name marketdata marketdata:latest
docker exec -it marketdata:latest  /bin/sh


docker run -d --name marketdata marketdata:latest

# steps - to run and access container
docker build -t marketdata:latest .
docker run -d --name marketdata marketdata:latest
docker exec -it marketdata /bin/bash
docker exec -it marketdata /bin/sh
docker stop marketdata  
docker rm marketdata


# kill all docker
docker kill $(docker ps -q) 
#force
docker ps -q | xargs docker rm -f



