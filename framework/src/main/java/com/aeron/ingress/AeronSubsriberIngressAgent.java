package com.aeron.ingress;


import com.aeron.config.AeronConfiguration;
import com.lion.app.Service;
import com.lion.clock.Clock;
import com.lion.message.GlobalMsgType;
import com.lion.message.codecs.HeaderDecoder;
import com.lion.message.publisher.ItcPublisher;
import io.aeron.Aeron;
import io.aeron.Subscription;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.agrona.ErrorHandler;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.AgentRunner;
import org.agrona.concurrent.BackoffIdleStrategy;
import org.agrona.concurrent.IdleStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO add callback onto a ringbuffer
public class AeronSubsriberIngressAgent implements FragmentHandler, Agent, Service {
    private static final Logger logger = LoggerFactory.getLogger(AeronSubsriberIngressAgent.class);
    private Aeron aeron;
    private Subscription subscription;
    private final IdleStrategy idleStrategy;
    private final ItcPublisher<GlobalMsgType> internalMsgTypeItcPublisher;
    private final HeaderDecoder headerDecoder = new HeaderDecoder();

    private final AgentRunner runner;

    private final Aeron.Context ctx;

    private final AeronConfiguration aeronConfiguration;

    public AeronSubsriberIngressAgent(AeronConfiguration config, Clock clock, ItcPublisher<GlobalMsgType> internalMsgTypeItcPublisher) {
        //TODO we should be able to have multiple channels ingress
        logger.info("Initializing Aeron Subscriber Ingress with config: {}", config);
        // Configure Aeron client
        this.ctx = new Aeron.Context()
                .aeronDirectoryName(config.getAeronDirectoryName());
        this.aeronConfiguration = config;
        this.internalMsgTypeItcPublisher = internalMsgTypeItcPublisher;
        this.idleStrategy = new BackoffIdleStrategy(100, 10, 1, 1000);
        final ErrorHandler errorHandler = throwable -> logger.error("Throwable", throwable);
        this.runner = new AgentRunner(
                idleStrategy,
                errorHandler,
                null,      // You can provide a human-readable name or "null"
                this
        );
    }

    @Override
    public void start(){
        logger.info("Starting Aeron ingress agent");
        this.aeron = Aeron.connect(ctx);
        this.subscription = aeron.addSubscription(aeronConfiguration.getChannel(), aeronConfiguration.getStreamId());
        AgentRunner.startOnThread(runner);
    }

    @Override
    public void stop() {
        logger.info("Stopping Aeron ingress agent");
        if (runner != null) {
            runner.close(); // Gracefully stops the agent
        }
    }

    @Override
    public void onFragment(DirectBuffer directBuffer, int offset, int length, Header header) {
        headerDecoder.wrap(directBuffer, offset);
        GlobalMsgType msgType = headerDecoder.messageType();
        internalMsgTypeItcPublisher.publish(msgType, directBuffer, offset + headerDecoder.length(), length - headerDecoder.length());
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