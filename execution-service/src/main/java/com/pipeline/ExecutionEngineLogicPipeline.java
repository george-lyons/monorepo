package com.pipeline;

import com.lion.app.Service;
import com.execution.ExecutionManager;
import com.execution.ExecutionTask;
import com.lion.message.IntIdentifier;
import com.lion.message.GlobalMsgType;
import com.lion.message.publisher.ItcPublisher;
import com.msg.ExecutionEngineMsgType;
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

/**
 * add execution
 * first integration tests - MD to algo
 * Add a heartbeat
 * histogram on packet processing time - monitor in prometheus
 * @param <T>
 */
public class ExecutionEngineLogicPipeline<T extends IntIdentifier> implements Agent, MessageHandler, ItcPublisher<T>, Service {
    private static final Logger logger = LogManager.getLogger(ExecutionEngineLogicPipeline.class, ReusableMessageFactory.INSTANCE);

    private final ExecutionManager executionManager;
    private final RingBuffer ringBuffer;
    private final EnumMap<GlobalMsgType, ItcPublisher<GlobalMsgType>> mapToMessagePublisher;
    private final AgentRunner runner;

    public ExecutionEngineLogicPipeline(IdleStrategy idleStrategy, RingBuffer ringBuffer, EnumMap<GlobalMsgType, ItcPublisher<GlobalMsgType>> mapToMessagePublisher) {
        this.executionManager = new ExecutionManager();
        this.ringBuffer = ringBuffer;
        this.mapToMessagePublisher = mapToMessagePublisher;

        final ErrorHandler errorHandler = throwable -> logger.error("Throwable", throwable);
        this.runner = new AgentRunner(idleStrategy, errorHandler, null, this);
    }

    @Override
    public void start() {
        logger.info("Starting ring buffer agent");
        AgentRunner.startOnThread(runner);
    }

    @Override
    public void stop() {
        logger.info("Stopping ring buffer agent");
        if (runner != null) {
            runner.close();
        }
    }

    @Override
    public int doWork() {
        return ringBuffer.read(this);
    }

    @Override
    public String roleName() {
        return "T_BLP";
    }

    /**
     * Adds an execution task to handle specific message types.
     *
     * @param messageType the type of messages this execution should handle
     * @param executionTask the execution task
     */
    public void addExecutionTask(ExecutionEngineMsgType messageType, ExecutionTask executionTask) {
        executionManager.registerExecution(messageType, executionTask);
    }

    @Override
    public void publish(T msgType, DirectBuffer directBuffer, int offset, int length) {
        if (!ringBuffer.write(msgType.getId(), directBuffer, offset, length)) {
            handleBackpressure(msgType.getId());
        }
    }

    public void registerExecution(ExecutionTask executionTask) {

    }

    @Override
    public void onMessage(int messageType, MutableDirectBuffer mutableDirectBuffer, int offset, int length) {
        final GlobalMsgType globalMsgType = GlobalMsgType.fromId(messageType);
        final ItcPublisher<GlobalMsgType> publisher = mapToMessagePublisher.get(globalMsgType);

        if(publisher != null) {
            publisher.publish(globalMsgType, mutableDirectBuffer, offset, length);
        }
    }

    private void handleBackpressure(int msgType) {
        logger.log(Level.WARN, "Dropping msg as was full msgType {}", msgType);
    }
}