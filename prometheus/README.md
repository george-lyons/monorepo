#run prometheus
docker run -p 9090:9090 -v /Users/georgelyons/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml prom/prometheus