package com.lion.aeron.shared;

import io.aeron.Aeron;
import io.aeron.Publication;
import org.agrona.BufferUtil;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public class AeronIpcProducer {
    private static final String CHANNEL = "aeron:ipc"; // Use IPC transport
    private static final int STREAM_ID = 1001;
    private static final int MESSAGE_SIZE = 64;

    public static void main(String[] args) {
        Aeron.Context context = new Aeron.Context();

        try (Aeron aeron = Aeron.connect(context);
             Publication publication = aeron.addPublication(CHANNEL, STREAM_ID)) {

            UnsafeBuffer buffer = new UnsafeBuffer(BufferUtil.allocateDirectAligned(MESSAGE_SIZE, 64));

            for (int i = 1; i <= 1000; i++) {
                buffer.putStringUtf8(0, "Message " + i);

                while (publication.offer(buffer, 0, MESSAGE_SIZE) < 0) {
                    System.out.println("[Producer] Waiting for space in ring buffer...");
                    Thread.yield();
                }
                System.out.println("[Producer] Sent: Message " + i);
                Thread.sleep(100); // Simulate a 100ms delay between messages
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}