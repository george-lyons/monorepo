package com.lion.aeron.ringbuffer;

import org.agrona.DirectBuffer;
import org.agrona.concurrent.ringbuffer.ManyToOneRingBuffer;
import org.agrona.concurrent.ringbuffer.RingBufferDescriptor;
import org.agrona.concurrent.AtomicBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

public class AgronaRingBufferBenchmark {

    private static final int RING_BUFFER_CAPACITY = 1024 * 1024; // 1MB buffer
    private static final int MESSAGE_SIZE = 64; // 64-byte message
    private static final int MSG_TYPE_ID = 1;
    private static final int TOTAL_MESSAGES = 100_000_000; // 100M messages

    public static void main(String[] args) throws InterruptedException {
        // Allocate off-heap buffer for ring buffer
        AtomicBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(RING_BUFFER_CAPACITY + RingBufferDescriptor.TRAILER_LENGTH));
        ManyToOneRingBuffer ringBuffer = new ManyToOneRingBuffer(buffer);

        byte[] message = new byte[MESSAGE_SIZE]; // Fixed-size message
        DirectBuffer directBuffer = new UnsafeBuffer();
        directBuffer.wrap(message);
        CountDownLatch latch = new CountDownLatch(1);

        // Producer Thread
        Thread producer = new Thread(() -> {
            long startTime = System.nanoTime();
            for (int i = 0; i < TOTAL_MESSAGES; i++) {
                while (!ringBuffer.write(MSG_TYPE_ID, directBuffer, 0, MESSAGE_SIZE)) {
                    // Busy-wait if the ring buffer is full
                }
            }
            long endTime = System.nanoTime();
            double durationSeconds = (endTime - startTime) / 1_000_000_000.0;
            System.out.printf("Producer finished in %.2f seconds%n", durationSeconds);
        });

        // Consumer Thread
        Thread consumer = new Thread(() -> {
            int received = 0;
            long startTime = System.nanoTime();

            while (received < TOTAL_MESSAGES) {
                received += ringBuffer.read((msgTypeId, buffer1, index, length) -> {
                    // Process message (dummy operation)
                });
            }

            long endTime = System.nanoTime();
            double durationSeconds = (endTime - startTime) / 1_000_000_000.0;
            double messagesPerSecond = TOTAL_MESSAGES / durationSeconds;
            System.out.printf("Consumer finished in %.2f seconds%n", durationSeconds);
            System.out.printf("Throughput: %.2f million messages/second%n", messagesPerSecond / 1_000_000);
            latch.countDown();
        });

        producer.start();
        consumer.start();
        latch.await(); // Wait for consumer to finish

        System.out.println("Benchmark completed!");
    }
}