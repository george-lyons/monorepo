import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.PushGateway;

public class PrometheusExample {
    // Define the Histogram
    private static final Histogram requestDuration = Histogram.build()
            .name("request_duration_seconds")
            .help("Request duration in seconds.")
            .labelNames("method")
            .buckets(0.1, 0.2, 0.5, 1.0, 2.5, 5.0, 10.0, 50.0, 99.0, 99.9) // Set custom buckets
            .register();

    public static void main(String[] args) throws Exception {
        // Register metrics on Prometheus registry
        CollectorRegistry registry = new CollectorRegistry();
        requestDuration.register(registry);

        // Record data in the histogram
        recordRequestDuration("GET", 0.5);
        recordRequestDuration("POST", 1.2);
        
        // Push to PushGateway (Optional)
        PushGateway pg = new PushGateway("localhost:9091");
        pg.pushAdd(registry, "request_duration_metrics");

        // Keep the server running to serve Prometheus metrics
        System.out.println("Metrics are available on http://localhost:9091/metrics");
    }

    public static void recordRequestDuration(String method, double durationInSeconds) {
        // Observe a new duration for a method
        requestDuration.labels(method).observe(durationInSeconds);
    }
}