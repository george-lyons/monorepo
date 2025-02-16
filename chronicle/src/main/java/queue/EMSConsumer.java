package queue;

import net.openhft.chronicle.queue.*;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;

public class EMSConsumer {
    public static void main(String[] args) {
        try (ChronicleQueue queue = SingleChronicleQueueBuilder.binary("my-queue").build()) {
            ExcerptTailer tailer = queue.createTailer();
            String order;
            while ((order = tailer.readText()) != null) {
                System.out.println("Processing order: " + order);
            }
        }
    }
}