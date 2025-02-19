FROM monorepo-base
WORKDIR /app
COPY target/prometheus-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
ENV MAIN_CLASS="PrometheusMetricsServer"
CMD ["java", "-jar", "app.jar"] 