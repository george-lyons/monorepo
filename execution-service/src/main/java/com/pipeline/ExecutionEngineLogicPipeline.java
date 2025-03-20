package com.pipeline;

import com.lion.app.Service;
import com.execution.ExecutionTask;
import com.lion.message.IntIdentifier;
import com.lion.message.GlobalMsgType;
import com.lion.message.publisher.ItcPublisher;
import com.market.data.sbe.MessageHeaderDecoder;
import com.market.data.sbe.QuoteMessageDecoder;
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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * add execution - done
 * first integration tests - MD to algo
 * Add a heartbeat
 * histogram on packet processing time - monitor in prometheus
 * add a scheduler
 * add equivalent of JVM metrics - that will work with prometheus
 * @param <T>
 */
public class ExecutionEngineLogicPipeline<T extends IntIdentifier> implements Agent, MessageHandler, ItcPublisher<T>, Service {
    private static final Logger logger = LogManager.getLogger(ExecutionEngineLogicPipeline.class, ReusableMessageFactory.INSTANCE);
    private final RingBuffer ringBuffer;
    private final EnumMap<GlobalMsgType, ItcPublisher<GlobalMsgType>> mapToMessagePublisher;
    private final AgentRunner runner;
    private final EnumMap<ExecutionEngineMsgType, List<ExecutionTask>> executionTaskMap = new EnumMap<>(ExecutionEngineMsgType.class);

    private final QuoteMessageDecoder quoteMessageDecoder = new QuoteMessageDecoder();
    private final MessageHeaderDecoder messageHeaderDecoder = new MessageHeaderDecoder();

    public ExecutionEngineLogicPipeline(IdleStrategy idleStrategy, RingBuffer ringBuffer, EnumMap<GlobalMsgType, ItcPublisher<GlobalMsgType>> mapToMessagePublisher) {
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
        return "BLP";
    }

    /**
     * Adds an execution task to handle specific message types.
     *
     * @param messageType the type of messages this execution should handle
     * @param executionTask the execution task
     */
    public void addExecutionTask(ExecutionEngineMsgType messageType, ExecutionTask executionTask) {
        executionTaskMap.computeIfAbsent(messageType, k -> new ArrayList<>()).add(executionTask);
    }

    @Override
    public void publish(T msgType, DirectBuffer directBuffer, int offset, int length) {
        if (!ringBuffer.write(msgType.getId(), directBuffer, offset, length)) {
            handleBackpressure(msgType.getId());
        }
    }

    @Override
    public void onMessage(int messageType, MutableDirectBuffer mutableDirectBuffer, int offset, int length) {
        final GlobalMsgType globalMsgType = GlobalMsgType.fromId(messageType);
        final ExecutionEngineMsgType executionEngineMsgType = ExecutionEngineMsgType.from(globalMsgType);
        final List<ExecutionTask> tasks = executionTaskMap.get(executionEngineMsgType);

        //handle app mesaages if required
        if(mapToMessagePublisher.containsKey(globalMsgType)) {
            mapToMessagePublisher.get(globalMsgType).publish(globalMsgType, mutableDirectBuffer, offset, length);
        }

        if (tasks != null && !tasks.isEmpty()) {
            for (ExecutionTask task : tasks) {
                if (task.isAvailable()) {
                    if(ExecutionEngineMsgType.TOB_MARKET_DATA.equals(executionEngineMsgType)) {
                        //decode once here
                        quoteMessageDecoder.wrapAndApplyHeader(mutableDirectBuffer, offset, messageHeaderDecoder);
                        task.handleMarketData(executionEngineMsgType, quoteMessageDecoder);
                    } else {
                        logger.info("Not expected event, will implement more");
                    }
                }
            }
        }
    }

    private void handleBackpressure(int msgType) {
        logger.log(Level.WARN, "Dropping msg as was full msgType {}", msgType);
    }
}