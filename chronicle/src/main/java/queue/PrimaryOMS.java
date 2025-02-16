package queue;

import net.openhft.chronicle.queue.*;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;

public class PrimaryOMS {
    public static void main(String[] args) {
        try (ChronicleQueue queue = SingleChronicleQueueBuilder.binary("my-queue").build()) {
            ExcerptAppender appender = queue.acquireAppender();
            appender.writeText("Order Placed: BUY 100 AAPL");
            System.out.println("Order written to queue.");
        }
    }
}