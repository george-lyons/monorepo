package com.lion.aeron.replicated;

import io.aeron.Aeron;
import io.aeron.FragmentAssembler;
import io.aeron.Subscription;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.YieldingIdleStrategy;

public class AeronSecondary {
    private static final String CHANNEL = "aeron:udp?endpoint=secondary-datacenter:9010";
    private static final int STREAM_ID = 10;
    private static final IdleStrategy idleStrategy = new YieldingIdleStrategy();

    public static void main(String[] args) {
        try (Aeron aeron = Aeron.connect(new Aeron.Context());
             Subscription subscription = aeron.addSubscription(CHANNEL, STREAM_ID)) {

            FragmentAssembler fragmentHandler = new FragmentAssembler(
                (buffer, offset, length, header) -> {
                    byte[] data = new byte[length];
                    buffer.getBytes(offset, data);
                    System.out.println("[Secondary] Processed: " + new String(data));
                });

            while (true) {
                int fragmentsRead = subscription.poll(fragmentHandler, 10);
                idleStrategy.idle(fragmentsRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}