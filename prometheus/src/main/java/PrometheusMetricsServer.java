import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;

import java.io.IOException;

public class PrometheusMetricsServer {

    // Define your custom metrics
    static final Gauge exampleMetric = Gauge.build()
            .name("example_metric_total")
            .help("An example of a custom metric.")
            .register();

    public static void main(String[] args) throws IOException {
        // Register the default JVM metrics (e.g., memory, GC, etc.)
        DefaultExports.initialize();

        // Initialize the HTTP server on port 8081
        HTTPServer server = new HTTPServer.Builder().withPort(8081).withHostname("0.0.0.0").build();

        // You can update your metrics here
        new Thread(() -> {
            while (true) {
                try {
                    // Example: Increment the metric by 1 every second
                    exampleMetric.inc();
                    Thread.sleep(1000); // Sleep for 1 second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        System.out.println("✅ Metrics exposed at http://localhost:8081/metrics");
    }
}