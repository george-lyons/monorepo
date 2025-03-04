package com.bootstrap;

import com.aeron.config.AeronConfiguration;
import com.aeron.ingress.AeronSubsriberIngressAgent;
import com.handler.QuoteEventHandlerLog;
import com.lion.clock.Clock;
import com.lion.clock.SystemClock;
import com.lion.config.ConfigLoader;
import com.lion.message.GlobalMsgType;
import com.lion.message.publisher.ItcPublisher;
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
        messagePublishers.put(GlobalMsgType.TOB_MARKET_DATA, new QuoteEventHandlerLog());
        final ExecutionEngineLogicPipeline<GlobalMsgType> logicPipeline = new ExecutionEngineLogicPipeline<>(ingressIdleStrategy, ingressRingBuffer, messagePublishers);
        final AeronConfiguration aeronConfiguration = new AeronConfiguration.Builder()
                .aeronDirectoryName("/tmp/logs/aeron")
                .channel("aeron:ipc")
                .streamId(1001).build();
        final AeronSubsriberIngressAgent aeronSubsriberIngressAgent = new AeronSubsriberIngressAgent(aeronConfiguration, clock, logicPipeline);

        logicPipeline.start();
        aeronSubsriberIngressAgent.start();
    }
}
