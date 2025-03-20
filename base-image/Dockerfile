FROM eclipse-temurin:20-jdk
echo  " le"
# Install common utilities
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

# Set environment variables
ENV APP_HOME=/app
ENV LOG_DIR=/app/logs
ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:MaxGCPauseMillis=100"

# Create application directories
RUN mkdir -p ${APP_HOME} \
    && mkdir -p ${LOG_DIR} \
    && mkdir -p /tmp/aeron/logs

# Set working directory
WORKDIR ${APP_HOME}

# Add a healthcheck script
COPY healthcheck.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/healthcheck.sh

# Common entrypoint script
COPY entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/entrypoint.sh

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"] 