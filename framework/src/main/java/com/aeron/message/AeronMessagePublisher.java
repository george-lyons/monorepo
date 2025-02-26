package com.aeron.message;

import com.aeron.config.AeronConfiguration;
import com.lion.message.IntIdentifier;
import com.lion.message.publisher.IpcPublisher;
import io.aeron.Aeron;
import io.aeron.Publication;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.BackoffIdleStrategy;
import org.agrona.concurrent.IdleStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class AeronMessagePublisher<T extends IntIdentifier> implements IpcPublisher<T>, AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(AeronMessagePublisher.class);
    private final Aeron aeron;
    private final Publication publication;
    private final StringBuilder container = new StringBuilder();
    private final IdleStrategy idleStrategy;
    private static final long CONNECT_TIMEOUT_NS = 5_000_000_000L; // 5 seconds

    public AeronMessagePublisher(AeronConfiguration config) {
        logger.info("Initializing Aeron publisher with config: {}", config);

        // Configure Aeron client
        final Aeron.Context ctx = new Aeron.Context()
                .aeronDirectoryName(config.getAeronDirectoryName());
        this.aeron = Aeron.connect(ctx);
        this.publication = aeron.addPublication(config.getChannel(), config.getStreamId());
        this.idleStrategy = new BackoffIdleStrategy(100, 10, 1, 1000);

        logger.info("Aeron publisher initialized. Channel: {}, Stream: {}",
                config.getChannel(), config.getStreamId());
        waitForConnection();
    }

    private void waitForConnection() {
        long startTime = System.nanoTime();
        while (!publication.isConnected()) {
            if (System.nanoTime() - startTime > CONNECT_TIMEOUT_NS) {
                logger.warn("No subscriber detected. Continuing anyway...");
                return; // Don't throw exception, just return
            }
            idleStrategy.idle(0);
        }
        logger.info("Publication connected successfully");
    }

    @Override
    public void publish(T msgType, DirectBuffer directBuffer, int offset, int length) {
        final long result = publication.offer(directBuffer, 0, length);

        if (result > 0) {
            logger.debug("Published message on channel {}", container);
        } else if (result == Publication.NOT_CONNECTED) {
            logger.debug("Publication not connected yet");
        } else if (result == Publication.BACK_PRESSURED) {
            logger.debug("Publication back pressured");
        } else {
            logger.warn("Failed to publish message on channel {} result: {}", container, result);
        }

    }

    @Override
    public void close() {
        publication.close();
        aeron.close();
    }


}