package com.lion.ringbuffer;

import com.lion.message.MsgType;
import com.lion.message.publisher.MessagePublisher;
import org.agrona.DirectBuffer;
import org.agrona.ErrorHandler;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.*;
import org.agrona.concurrent.ringbuffer.OneToOneRingBuffer;
import org.agrona.concurrent.ringbuffer.RingBuffer;
import org.agrona.concurrent.ringbuffer.RingBufferDescriptor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ReusableMessageFactory;
import java.nio.ByteBuffer;
import java.util.EnumMap;


public class RingBufferIngress implements Agent, MessageHandler, MessagePublisher {
   //Uses ReusableParameterizedMessageFactory to avoid creating temporary String objects.
    private static final Logger logger = LogManager.getLogger(RingBufferIngress.class, ReusableMessageFactory.INSTANCE);

    private RingBuffer ringBuffer;
    private final StringBuilder logAppender = new StringBuilder();
    private final EnumMap<MsgType,MessagePublisher> mapToMessagePublisher;


    public RingBufferIngress(IdleStrategy idleStrategy, int ringBufferSize, EnumMap<MsgType,MessagePublisher> mapToMessagePublisher ) {
        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(ringBufferSize + RingBufferDescriptor.TRAILER_LENGTH);
        final UnsafeBuffer unsafeBuffer = new UnsafeBuffer(byteBuffer);
        this.mapToMessagePublisher = mapToMessagePublisher;
        //todo inject ring buffer object (factory)
        this.ringBuffer = new OneToOneRingBuffer(unsafeBuffer);
        // Initialize the time wheel (resolution: 1ms, ticks per wheel: 1024)
        final ErrorHandler errorHandler = Throwable::printStackTrace;
        final AgentRunner runner = new AgentRunner(
                idleStrategy,
                errorHandler,
                null,      // You can provide a human-readable name or "null"
                this
        );
        // 5) Launch the runner on a new thread, which calls doWork() in a loop
        AgentRunner.startOnThread(runner);
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
    public void onMessage(int messageType, MutableDirectBuffer mutableDirectBuffer, int offset, int length) {
        final MsgType msgType = MsgType.fromId(messageType);
        final MessagePublisher publisher = mapToMessagePublisher.get(msgType);

        if(publisher != null) {
            publisher.publish(messageType, mutableDirectBuffer, offset, length);
        }
    }

    @Override
    public void publish(int msgType, DirectBuffer directBuffer, int offset, int length) {
        if (!ringBuffer.write(msgType, directBuffer, offset, length)) {
            handleBackpressure(msgType);
        }
    }

    private void handleBackpressure(int msgType) {
        //do nothing
        //for backpressure, write to chronicle queue
        logger.log(Level.WARN, "Dropping msg as was full");
    }
}
