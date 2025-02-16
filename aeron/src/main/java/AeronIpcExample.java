import io.aeron.Aeron;
import io.aeron.Publication;
import io.aeron.Subscription;
import io.aeron.driver.MediaDriver;
import org.agrona.BufferUtil;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.BusySpinIdleStrategy;
import org.agrona.concurrent.SigInt;
import org.agrona.concurrent.UnsafeBuffer;

public class AeronIpcExample {
    private static final String CHANNEL = "aeron:ipc"; // IPC communication
    private static final int STREAM_ID = 1001; // Stream ID must match between producer & consumer
    private static final int MESSAGE_SIZE = 64; // Size of message buffer

    public static void main(String[] args) {
        // Start Media Driver (runs in the same JVM)
        MediaDriver mediaDriver = MediaDriver.launchEmbedded();

        // Create Aeron context
        Aeron.Context context = new Aeron.Context().aeronDirectoryName(mediaDriver.aeronDirectoryName());

        // Start the Aeron client
        try (Aeron aeron = Aeron.connect(context)) {
            // Start the producer & consumer threads
            Thread producerThread = new Thread(() -> startProducer(aeron), "ProducerThread");
            Thread consumerThread = new Thread(() -> startConsumer(aeron), "ConsumerThread");

            producerThread.start();
            consumerThread.start();

            // Wait for shutdown signal
            SigInt.register(() -> {
                System.out.println("\nShutting down...");
                producerThread.interrupt();
                consumerThread.interrupt();
            });

            producerThread.join();
            consumerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            mediaDriver.close();
        }
    }

    private static void startProducer(Aeron aeron) {
        try (Publication publication = aeron.addPublication(CHANNEL, STREAM_ID)) {
            UnsafeBuffer buffer = new UnsafeBuffer(BufferUtil.allocateDirectAligned(MESSAGE_SIZE, 64));

            for (int i = 1; i <= 1000; i++) {
                buffer.putStringUtf8(0, "Message " + i);

                while (publication.offer(buffer, 0, MESSAGE_SIZE) < 0) {
                    System.out.println("[Producer] Waiting for space in ring buffer...");
                    Thread.yield(); // Avoid busy-waiting
                }
                System.out.println("[Producer] Sent: Message " + i);

                Thread.sleep(100); // Simulate 100ms delay between messages
            }
        } catch (InterruptedException e) {
            System.err.println("[Producer] Interrupted");
            Thread.currentThread().interrupt();
        }
    }

    private static void startConsumer(Aeron aeron) {
        try (Subscription subscription = aeron.addSubscription(CHANNEL, STREAM_ID)) {
            BusySpinIdleStrategy idleStrategy = new BusySpinIdleStrategy();

            while (!Thread.currentThread().isInterrupted()) {
                int fragmentsRead = subscription.poll(AeronIpcExample::onMessage, 10);

                if (fragmentsRead == 0) {
                    idleStrategy.idle();
                }
            }
        }
    }

    private static void onMessage(DirectBuffer buffer, int offset, int length, io.aeron.logbuffer.Header header) {
        String message = buffer.getStringUtf8(offset);
        System.out.println("[Consumer] Received: " + message);
    }
}