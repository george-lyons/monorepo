FROM eclipse-temurin:21-jdk

MAINTAINER george-lyons

USER root

# Install useful packages
RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
    bash \
    curl \
    wget \
    vim \
    less \
    iputils-ping \
    net-tools \
    dnsutils \
    netcat-openbsd \
    telnet \
    traceroute \
    tcpdump \
    procps \
    htop \
    iotop \
    iftop \
    lsof \
    strace \
    gdb \
    build-essential \
    iproute2 \
    gnupg2 \
    lsb-release \
    unzip \
    jq \
    && apt-get clean && \
    rm -rf /var/lib/apt/lists/*


# Use Bash as the default shell
SHELL ["/bin/bash", "-c"]

## Set working directory
#WORKDIR /app

# Copy the JAR file
COPY target/Marketdata-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
COPY src/main/resources/log4j2.xml /app/log4j2.xml

# Expose ports (adjust as necessary)
EXPOSE 8080 9443

# Correct entrypoint with JVM options in one string
ENTRYPOINT java --add-opens=java.base/jdk.internal.misc=ALL-UNNAMED --add-opens=java.base/java.nio=ALL-UNNAMED -Dlog4j2.debug=true -Dlog4j.configurationFile=/app/log4j2.xml -jar /app.jar