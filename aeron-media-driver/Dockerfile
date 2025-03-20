FROM eclipse-temurin:21-jdk

MAINTAINER george-lyons

USER root

# Install useful system tools
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
    && rm -rf /var/lib/apt/lists/*

# Set Bash as the default shell
SHELL ["/bin/bash", "-c"]

# Create application directories
RUN mkdir -p /app/logs /app/var /app/etc /app/bin

# Copy application JAR and config files
COPY target/aeron-media-driver-1.0-SNAPSHOT-jar-with-dependencies.jar /app/var/media_driver.jar
COPY src/main/resources/log4j2.xml /app/etc/log4j2.xml
COPY src/main/resources/app.sh /app/bin/app.sh

# Ensure scripts are executable
RUN chmod +x /app/bin/app.sh

# Set working directory
WORKDIR /app

# Define the entrypoint
ENTRYPOINT ["/app/bin/app.sh", "start"]

#CMD ["tail", "-f", "/dev/null"]