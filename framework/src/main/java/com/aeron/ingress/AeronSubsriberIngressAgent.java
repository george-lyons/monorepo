package com.aeron.ingress;


import com.aeron.config.AeronConfiguration;
import com.lion.clock.Clock;
import com.lion.message.InternalMsgType;
import com.lion.message.codecs.HeaderDecoder;
import com.lion.message.publisher.ItcPublisher;
import io.aeron.Aeron;
import io.aeron.Subscription;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.BackoffIdleStrategy;
import org.agrona.concurrent.IdleStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO add callback onto a ringbuffer
public class AeronSubsriberIngressAgent implements FragmentHandler, Agent {
    private static final Logger logger = LoggerFactory.getLogger(AeronSubsriberIngressAgent.class);

    private final Aeron aeron;
    private final Subscription subscription;
    private final StringBuilder container = new StringBuilder();
    private final IdleStrategy idleStrategy;
    private final ItcPublisher<InternalMsgType> itcPublisher;
    private static final long CONNECT_TIMEOUT_NS = 5_000_000_000L; // 5 seconds
    private final HeaderDecoder headerDecoder = new HeaderDecoder();

    public AeronSubsriberIngressAgent(AeronConfiguration config, Clock clock, ItcPublisher<InternalMsgType>  itcPublisher) {
        //TODO we should be able to have multiple channels ingress
        logger.info("Initializing Aeron Subscriber Ingress with config: {}", config);
        // Configure Aeron client
        final Aeron.Context ctx = new Aeron.Context()
                .aeronDirectoryName(config.getAeronDirectoryName());
        this.itcPublisher = itcPublisher;
        this.aeron = Aeron.connect(ctx);
        this.subscription = aeron.addSubscription(config.getChannel(), config.getStreamId());
        this.idleStrategy = new BackoffIdleStrategy(100, 10, 1, 1000);
        waitForConnection();
    }

    private void waitForConnection() {
        long startTime = System.nanoTime();
        while (!subscription.isConnected()) {
            if (System.nanoTime() - startTime > CONNECT_TIMEOUT_NS) {
                logger.warn("No subscriber detected. Continuing anyway...");
                return; // Don't throw exception, just return
            }
            idleStrategy.idle(0);
        }
        logger.info("Publication connected successfully");
    }

    @Override
    public void onFragment(DirectBuffer directBuffer, int offset, int length, Header header) {
        headerDecoder.wrap(directBuffer, offset);
        InternalMsgType msgType = headerDecoder.messageType();
        itcPublisher.publish(msgType, directBuffer, offset, length);
    }

    @Override
    public int doWork() {
        return subscription.poll(this, 10);
    }

    @Override
    public String roleName() {
        return "INGRESS_AERON";
    }

}