package com.lion.aeron.replicated;

import io.aeron.Aeron;
import io.aeron.Publication;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public class AeronPrimary {
    private static final String CHANNEL = "aeron:udp?endpoint=secondary-datacenter:9010";
    private static final int STREAM_ID = 10;

    public static void main(String[] args) {
        try (Aeron aeron = Aeron.connect(new Aeron.Context());
             Publication publication = aeron.addPublication(CHANNEL, STREAM_ID)) {

            UnsafeBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(128));

            for (int i = 1; i <= 10; i++) {
                String order = "Order " + i + " - BUY 100 AAPL";
                buffer.putBytes(0, order.getBytes());

                while (publication.offer(buffer, 0, order.length()) < 0) {
                    // Busy spin until message is sent
                }

                System.out.println("[Primary] Sent: " + order);
                Thread.sleep(500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}