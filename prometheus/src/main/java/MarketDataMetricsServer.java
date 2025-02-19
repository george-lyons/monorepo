import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.PushGateway;
import io.prometheus.client.hotspot.DefaultExports;

import java.io.IOException;
public class MarketDataMetricsServer {
    public static void main(String[] args) throws IOException {
        // Create a Prometheus registry
        CollectorRegistry registry = new CollectorRegistry();

        // Register default JVM metrics (CPU, memory, GC, etc.)
        DefaultExports.register(registry);

        // Initialize PushGateway
        PushGateway pushGateway = new PushGateway("localhost:9091");

        // Push metrics to Prometheus PushGateway
        pushGateway.pushAdd(registry, "marketdata_metrics");
        System.out.println("✅ Sending Metrics to Prometheus PushGateway...");
    }

}