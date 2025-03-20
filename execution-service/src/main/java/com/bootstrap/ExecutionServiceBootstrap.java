package com.bootstrap;

import com.aeron.config.AeronConfiguration;
import com.aeron.ingress.AeronSubsriberIngressAgent;
import com.execution.ExecutionTask;
import com.handler.QuoteEventExecutionLog;
import com.lion.clock.Clock;
import com.lion.clock.SystemClock;
import com.lion.config.ConfigLoader;
import com.lion.message.GlobalMsgType;
import com.lion.message.publisher.ItcPublisher;
import com.msg.ExecutionEngineMsgType;
import com.pipeline.ExecutionEngineLogicPipeline;
import org.agrona.concurrent.BackoffIdleStrategy;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.concurrent.ringbuffer.OneToOneRingBuffer;
import org.agrona.concurrent.ringbuffer.RingBuffer;
import org.agrona.concurrent.ringbuffer.RingBufferDescriptor;

import java.nio.ByteBuffer;
import java.util.EnumMap;

public class ExecutionServiceBootstrap {
    public ExecutionServiceBootstrap(ConfigLoader configLoader) {
        int ringBufferSize = Integer.parseInt(configLoader.getProperty("ingress.ring.buffer.size"));
        final Clock clock = new SystemClock();
        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(ringBufferSize + RingBufferDescriptor.TRAILER_LENGTH);
        final UnsafeBuffer unsafeBuffer = new UnsafeBuffer(byteBuffer);
        final RingBuffer ingressRingBuffer = new OneToOneRingBuffer(unsafeBuffer);
        final IdleStrategy ingressIdleStrategy = new BackoffIdleStrategy();
        final EnumMap<GlobalMsgType, ItcPublisher<GlobalMsgType>> messagePublishers = new EnumMap<>(GlobalMsgType.class);
        final ExecutionEngineLogicPipeline<GlobalMsgType> logicPipeline = new ExecutionEngineLogicPipeline<>(ingressIdleStrategy, ingressRingBuffer, messagePublishers);
        final ExecutionTask logMarketData = new QuoteEventExecutionLog();
        logicPipeline.addExecutionTask(ExecutionEngineMsgType.TOB_MARKET_DATA, logMarketData);
        final AeronConfiguration aeronConfiguration = new AeronConfiguration.Builder()
                .aeronDirectoryName("/tmp/aeron/logs")
                .channel("aeron:ipc")
                .streamId(1001).build();
        final AeronSubsriberIngressAgent aeronSubsriberIngressAgent = new AeronSubsriberIngressAgent(aeronConfiguration, clock, logicPipeline);

        logicPipeline.start();
        aeronSubsriberIngressAgent.start();
    }
}
