package com.lion.aeron.ringbuffer.benchmark;

import org.agrona.DirectBuffer;
import org.agrona.concurrent.ringbuffer.ManyToOneRingBuffer;
import org.agrona.concurrent.ringbuffer.OneToOneRingBuffer;
import org.agrona.concurrent.ringbuffer.RingBufferDescriptor;
import org.agrona.concurrent.AtomicBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput) // Measure messages per second
@State(Scope.Thread) // Separate state for each thread
@OutputTimeUnit(TimeUnit.SECONDS) // Results in messages per second
@Threads(2) // One producer, one consumer
public class AgronaRingBufferPipelineJMH {

    private static final int RING_BUFFER_CAPACITY = 1024 * 1024; // 1MB buffer
    private static final int MESSAGE_SIZE = 64; // 64-byte message
    private static final int MSG_TYPE_ID = 1;

    private AtomicBuffer buffer;
    private OneToOneRingBuffer ringBuffer;
    private DirectBuffer directBuffer;

    @Setup(Level.Iteration)
    public void setup() {
        buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(RING_BUFFER_CAPACITY + RingBufferDescriptor.TRAILER_LENGTH));
        ringBuffer = new OneToOneRingBuffer(buffer);

        byte[] message = new byte[MESSAGE_SIZE]; // Fixed-size message
        directBuffer = new UnsafeBuffer();
        directBuffer.wrap(message);
    }

    @Benchmark
    @Group("producer_consumer")
    @GroupThreads(1) // 1 producer thread
    public void producerWrite() {
        ringBuffer.write(MSG_TYPE_ID, directBuffer, 0, MESSAGE_SIZE);
    }

    @Benchmark
    @Group("producer_consumer")
    @GroupThreads(1) // 1 consumer thread
    public int consumerRead(Blackhole blackhole) {
        return ringBuffer.read((msgTypeId, buffer1, index, length) -> {
            blackhole.consume(buffer1.getByte(index)); // Simulate processing
        });
    }
}