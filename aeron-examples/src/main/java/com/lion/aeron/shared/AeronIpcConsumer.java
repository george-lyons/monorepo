package com.lion.aeron.shared;

import io.aeron.Aeron;
import io.aeron.Subscription;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.BusySpinIdleStrategy;

public class AeronIpcConsumer {
    private static final String CHANNEL = "aeron:ipc"; // Use IPC transport
    private static final int STREAM_ID = 1001;

    public static void main(String[] args) {
        Aeron.Context context = new Aeron.Context();

        try (Aeron aeron = Aeron.connect(context);
             Subscription subscription = aeron.addSubscription(CHANNEL, STREAM_ID)) {

            System.out.println("[Consumer] Waiting for messages...");

            BusySpinIdleStrategy idleStrategy = new BusySpinIdleStrategy();

            while (true) {
                int fragmentsRead = subscription.poll(AeronIpcConsumer::onMessage, 10);
                if (fragmentsRead == 0) idleStrategy.idle();
            }
        }
    }

    private static void onMessage(DirectBuffer buffer, int offset, int length, io.aeron.logbuffer.Header header) {
        System.out.println("[Consumer] Received: " + buffer.getStringUtf8(offset));
    }
}