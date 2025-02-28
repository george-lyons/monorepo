package com.lion.ringbuffer;

import com.lion.app.Service;
import com.lion.message.IntIdentifier;
import com.lion.message.FrameworkMsg;
import com.lion.message.publisher.ItcPublisher;
import org.agrona.DirectBuffer;
import org.agrona.ErrorHandler;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.AgentRunner;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.MessageHandler;
import org.agrona.concurrent.ringbuffer.RingBuffer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ReusableMessageFactory;

import java.util.EnumMap;


public class LogicPipeline<T extends IntIdentifier> implements Agent, MessageHandler, ItcPublisher<T>, Service {
    private static final Logger logger = LogManager.getLogger(LogicPipeline.class, ReusableMessageFactory.INSTANCE);
    private RingBuffer ringBuffer;
    private final StringBuilder logAppender = new StringBuilder();
    private final EnumMap<FrameworkMsg, ItcPublisher<FrameworkMsg>> mapToMessagePublisher;

    private final AgentRunner runner;

    public LogicPipeline(IdleStrategy idleStrategy, RingBuffer ringBuffer, EnumMap<FrameworkMsg, ItcPublisher<FrameworkMsg>> mapToMessagePublisher ) {
        this.mapToMessagePublisher = mapToMessagePublisher;
        this.ringBuffer = ringBuffer;
        //todo timer service wheel - or even my own scheduler
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
        logger.info("Starting ring buffer agent");
        AgentRunner.startOnThread(runner);
    }
    @Override
    public void stop() {
        logger.info("Stopping ring buffer agent");
        if (runner != null) {
            runner.close(); // Gracefully stops the agent
        }
    }

    @Override
    public int doWork() throws Exception {
        int workDone = 0;
        // Process pending messages
        workDone += ringBuffer.read(this);
        return workDone;
    }

    @Override
    public String roleName() {
        return "T_BLP";
    }

    @Override
    public void publish(T msgType, DirectBuffer directBuffer, int offset, int length) {
        if (!ringBuffer.write(msgType.getId(), directBuffer, offset, length)) {
            handleBackpressure(msgType.getId());
        }
    }

    @Override
    public void onMessage(int messageType, MutableDirectBuffer mutableDirectBuffer, int offset, int length) {
        final FrameworkMsg frameworkMsg = FrameworkMsg.fromId(messageType);
        final ItcPublisher<FrameworkMsg> publisher = mapToMessagePublisher.get(frameworkMsg);

        if(publisher != null) {
            publisher.publish(frameworkMsg, mutableDirectBuffer, offset, length);
        }
    }

    private void handleBackpressure(int msgType) {
        //do nothing
        //for backpressure, write to chronicle queue
        logger.log(Level.WARN, "Dropping msg as was full msgType {}", msgType);
    }



}
